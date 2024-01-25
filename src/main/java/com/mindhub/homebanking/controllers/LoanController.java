package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.dto.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/loan")
    public List<LoanDTO> getAccounts(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @GetMapping("/loan/{id}")
    public LoanDTO getAccounts(@PathVariable Long id){
        return loanRepository.findById(id).map(LoanDTO::new).orElse(null);
    }

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<String> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication)  throws IOException, MessagingException {

        Account toAccount = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());

        if (loanApplicationDTO.getLoanId() == 0) {
            return new ResponseEntity<>("No loan id", HttpStatus.FORBIDDEN);
        }

        Optional<Loan> loanOptional = loanRepository.findById(loanApplicationDTO.getLoanId());
        return loanOptional.map(loan -> {
            if (loanApplicationDTO.getAmount() == 0 || loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
                return new ResponseEntity<>("No amount or the amount exceeds the maximum loan amount", HttpStatus.FORBIDDEN);
            }

            if (loanApplicationDTO.getPayments() == 0 || loan.getPayments().stream().noneMatch(integer -> loanApplicationDTO.getPayments() == integer)) {
                return new ResponseEntity<>("No payments or the loan does not have the amount of payments ", HttpStatus.FORBIDDEN);
            }

            Account account = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
            if (account == null) {
                return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
            }

            Client client = clientRepository.findByEmail(authentication.getName());
            if (client.getAccounts().stream().noneMatch(account1 -> account1.getNumber().equals(loanApplicationDTO.getToAccountNumber()))) {
                return new ResponseEntity<>("Destination account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
            }

            double newAmount = loanApplicationDTO.getAmount() * 1.20;
            double amount = loanApplicationDTO.getAmount() * 0.2;
            double fee = ((loanApplicationDTO.getAmount() + amount) / loanApplicationDTO.getPayments());

            clientLoanRepository.save(new ClientLoan(loanApplicationDTO.getAmount() + amount, fee, loanApplicationDTO.getPayments(), client, loan));
            transactionRepository.save(new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " loan approved", LocalDateTime.now(), account));
            account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());
            accountRepository.save(account);


            //Servicio de email
            try {
                emailService.sendEmailLoanHTML(toAccount.getClient().getEmail(), toAccount.getClient().getFirstName(),
                        toAccount.getClient().getLastName(), loan.getName(), loanApplicationDTO.getPayments(),
                        String.format("%.0f", loanApplicationDTO.getAmount()),String.format("%.0f", amount) , String.format("%.0f", newAmount));
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ResponseEntity<>("Crédito aprobado!", HttpStatus.CREATED);
        }).orElse(new ResponseEntity<>("Loan does not exist", HttpStatus.FORBIDDEN));
    }
}
