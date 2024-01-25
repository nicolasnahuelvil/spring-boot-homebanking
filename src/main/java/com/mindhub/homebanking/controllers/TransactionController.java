package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.EmailServiceImpl;
import com.mindhub.homebanking.util.CacheContext;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @Qualifier("twilio.ssid")
    private String SSID;
    @Qualifier("twilio.token")
    private String TOKEN;
    private String fromPhoneNumber = "+16518672764";

    @PostConstruct
    public void initTwilioSdk() {
        Twilio.init("AC7f473c81acace7ee93845cd575faf0fe", "ed4d9fcfe1959e243c9ae419e3c9e282");
    }


    @PostMapping("/pre-transaction")
    public ResponseEntity<Object> createTransaction(Authentication authentication, @RequestParam double amount, @RequestParam String description,
                                                    @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber){

        Account originAccount = accountRepository.findByNumber(fromAccountNumber);
        Account destinyAccount = accountRepository.findByNumber(toAccountNumber);

        if(String.valueOf(amount).isEmpty() || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()){
            return new ResponseEntity<>("Revise los datos ingresados",HttpStatus.FORBIDDEN);
        }

        if(originAccount == null || destinyAccount == null) {
            return new ResponseEntity<>("Cuenta no existe", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName());

        if (!originAccount.getClient().getEmail().equals(client.getEmail())){
            return new ResponseEntity<>("Cuenta de Origen no corresponde al cliente", HttpStatus.FORBIDDEN);
        }

        if(originAccount.getBalance() == 0){
            return new ResponseEntity<>("Cuenta no tiene fondos suficientes", HttpStatus.FORBIDDEN);
        }

        if(originAccount.getBalance() < amount){
            return new ResponseEntity<>("Monto supera el saldo de la cuenta", HttpStatus.FORBIDDEN);
        }

        if(fromAccountNumber == toAccountNumber){
            return new ResponseEntity<>("Cuentas de origen y destino no pueden ser la misma", HttpStatus.FORBIDDEN);
        }

        double newAmountOrigin = (originAccount.getBalance() - amount);
        double newAmountDestiny = (destinyAccount.getBalance() + amount);

        Transaction fromAccountTransaction = new Transaction(TransactionType.DEBIT, -amount, "Transferencia Saliente", LocalDateTime.now(), originAccount);
        Transaction toAccountTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now(), destinyAccount);

        transactionRepository.save(fromAccountTransaction);
        transactionRepository.save(toAccountTransaction);

        originAccount.setBalance(newAmountOrigin);
        destinyAccount.setBalance(newAmountDestiny);

        accountRepository.save(originAccount);
        accountRepository.save(destinyAccount);


        int transactionCode = new Random().nextInt(999999 - 100000) + 100000;

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("fromAccountTransaction", fromAccountTransaction);
        transaction.put("toAccountTransaction", toAccountTransaction);
        transaction.put("originAccount", originAccount);
        transaction.put("destinyAccount", destinyAccount);
        transaction.put("transactionCode", String.valueOf(transactionCode));

        //SERVICIO SMS
        CacheContext.addKey(fromAccountNumber, transaction);
        System.out.println("saved");

        Message message = Message.creator(
                new PhoneNumber("+56" + originAccount.getClient().getPhoneNumber()),
                new PhoneNumber(fromPhoneNumber),
                "Estas realizando una transferencia de $" + String.format("%.0f", amount) + " hacia la cuenta " + toAccountNumber + ", código de autorización: " + transactionCode + "\nNO COMPARTAS ESTE CÓDIGO CON NADIE!").create();

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam String transactionCode, @RequestParam double amount, @RequestParam String description,
                                                    @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, Authentication authentication) throws MessagingException, IOException {

        Optional<Object> transactionOpt = (Optional<Object>) CacheContext.getKey(fromAccountNumber);

        if (transactionOpt.isEmpty()) {
            return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, Object> transaction = (Map<String, Object>) transactionOpt.get();

        String transactionCodeCached = (String) transaction.get("transactionCode");

        if (!transactionCodeCached.equals(transactionCode)) {
            System.out.println("TransactionCodeError");
            return new ResponseEntity<>("Código incorrecto", HttpStatus.BAD_REQUEST);
        }

        Transaction fromAccountTransaction = (Transaction) transaction.get("fromAccountTransaction");
        Transaction toAccountTransaction = (Transaction) transaction.get("toAccountTransaction");
        Account originAccount = (Account) transaction.get("originAccount");
        Account destinyAccount = (Account) transaction.get("destinyAccount");

        transactionRepository.saveAll(List.of(fromAccountTransaction, toAccountTransaction));

        //Servicio de e-mail, una vez realizada la transacción se envía un comprobante de transferencia a los emails.
        emailService.sendEmailTransactionHTML(originAccount.getClient().getFirstName(), originAccount.getClient().getLastName(),
                String.format("%.0f", amount), description, originAccount.getClient().getEmail(), destinyAccount.getClient().getEmail(), fromAccountNumber,
                toAccountNumber, destinyAccount.getClient().getFirstName(), destinyAccount.getClient().getLastName());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
