package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

        @GetMapping("/accounts")
        public List<AccountDTO> getAccounts(){
            return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
        }

        @GetMapping("/accounts/{id}")
        public AccountDTO getAccounts(@PathVariable Long id){
            return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
        }

        @PostMapping("/clients/current/accounts")
        public ResponseEntity<Object> createAccount(Authentication authentication){

            Client client = clientRepository.findByEmail(authentication.getName());

            if(client.getAccounts().size() > 3){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            String accountNumber = "VIN" + (int) ((Math.random() * (10000000 - 1) + 1));
            this.accountRepository.save(new Account(accountNumber, LocalDateTime.now(), 0, client));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
}
