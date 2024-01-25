package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentLoanController {
    @Autowired
    private PaymentLoanRepository paymentLoanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailServiceImpl emailService;


    @Transactional
    @PostMapping("/paymentsloans")
    public ResponseEntity<Object> createTransaction(@RequestParam String fromAccountNumber, @RequestParam Long loanTo, Authentication authentication) throws MessagingException, IOException {

        Account originAccount = accountRepository.findByNumber(fromAccountNumber);
        ClientLoan clientLoan = clientLoanRepository.getLoanById(loanTo);

        double feetoPay =  clientLoan.getFee();

        int paymentNumber = clientLoan.getPaymentLoans().size() + 1;

        if(fromAccountNumber.isEmpty() || String.valueOf(loanTo).isEmpty()){
            return new ResponseEntity<>("Revise los datos ingresados", HttpStatus.FORBIDDEN);
        }

        if(originAccount == null) {
            return new ResponseEntity<>("Cuenta no existe", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName());

        if (originAccount.getClient().getId() != client.getId()){
            return new ResponseEntity<>("Cuenta de Origen no corresponde al cliente", HttpStatus.FORBIDDEN);
        }

        if(originAccount.getBalance() == 0){
            return new ResponseEntity<>("Cuenta no tiene fondos suficientes", HttpStatus.FORBIDDEN);
        }

        if(originAccount.getBalance() < feetoPay){
            return new ResponseEntity<>("Monto supera el saldo de la cuenta", HttpStatus.FORBIDDEN);
        }

        double newAmountOrigin = (originAccount.getBalance() - feetoPay);

        transactionRepository.save(new Transaction(TransactionType.DEBIT, -feetoPay, "Pago Credito", LocalDateTime.now(), originAccount));

        originAccount.setBalance(newAmountOrigin);

        int feeNumber = (clientLoan.getPaymentLoans().size() + 1);

        paymentLoanRepository.save(new PaymentLoan(LocalDateTime.now(), feetoPay, feeNumber, "Payment fee "+ paymentNumber + "of " + clientLoan.getPayments(), clientLoan));

        //Servicio de e-mail, una vez realizada el pago de créditos.
        emailService.sendEmailPaymentLoanHTML(originAccount.getClient().getEmail(), clientLoan.getLoan().getName(), paymentNumber+" de "+clientLoan.getPayments(),
                originAccount.getNumber(), String.format("%.0f", feetoPay));

        return new ResponseEntity<>("Success Payment", HttpStatus.CREATED);
    }

}
