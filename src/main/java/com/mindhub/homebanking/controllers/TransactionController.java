package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> transaction(
            Authentication authentication,
            @RequestParam double amount, @RequestParam String description,
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber) {

        Client clientAuth = clientRepository.findByEmail(authentication.getName());

        // Identifico las cuentas de origen y destino.
        Account fromAccount = accountRepository.findByNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByNumber(toAccountNumber);

        //Verificar que los parámetros no estén vacíos
        if (amount == 0 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }// Verificar que los números de cuenta no sean iguales
        else if (fromAccount.getNumber().equals(toAccountNumber)) {
            return new ResponseEntity<>("Number of equal accounts", HttpStatus.FORBIDDEN);
        }//Verificar que la cuenta de origen tenga el monto disponible
        else if (fromAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient  balance!", HttpStatus.FORBIDDEN);
        }// Verificar que exista la cuenta de origen
        else if (fromAccount == null) {
            return new ResponseEntity<>("Origin account doesn't exist", HttpStatus.FORBIDDEN);
        }// Verificar que exista la cuenta de destino
        else if (toAccount == null) {
            return new ResponseEntity<>("Destiny account doesn't exist", HttpStatus.FORBIDDEN);
        }//Verificar que la cuenta de origen pertenezca al cliente autenticado
        else if(fromAccount.getClient() != clientAuth){
            return new ResponseEntity<>("Destiny account doesn't exist", HttpStatus.FORBIDDEN);
        } else {
            double newBalanceFromAccount = fromAccount.getBalance() - amount;
            double newBalanceToAccount = toAccount.getBalance() + amount;

            fromAccount.setBalance(newBalanceFromAccount);
            toAccount.setBalance(newBalanceToAccount);

            // Se realiza la transacción cuenta origen y desitno.
            Transaction fromAccountTransaction = new Transaction(TransactionType.DEBIT, -amount, "To:" + toAccount.getNumber() + "-" + description, LocalDate.now(), fromAccount);
            Transaction toAccountTransaction = new Transaction(TransactionType.CREDIT, amount, "From:" + fromAccount.getNumber() + "-" + description, LocalDate.now(), toAccount);

            transactionRepository.save(fromAccountTransaction);
            transactionRepository.save(toAccountTransaction);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}




