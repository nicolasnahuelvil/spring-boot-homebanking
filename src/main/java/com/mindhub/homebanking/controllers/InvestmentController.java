package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.dto.InversionApplicationDTO;
import com.mindhub.homebanking.dto.InvestmentDTO;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class InvestmentController {

    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private ClientInvestmentRepository accountInversionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    ClientInvestmentRepository clientInvestmentRepository;
    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/inversions")
    public List<InvestmentDTO> getData(){
        return this.investmentRepository.findAll().stream().map(InvestmentDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping(path = "/inversions")
    public ResponseEntity<Object> setInversion(@RequestBody InversionApplicationDTO inversionApplicationDTO, Authentication authentication){

        if (inversionApplicationDTO.getInversionOptionId() == 0 || inversionApplicationDTO.getAccountNumber().isEmpty() || inversionApplicationDTO.getInitialAmount() == 0 || inversionApplicationDTO.getInitialDate() == null) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        Investment inversionOption = investmentRepository.findInversionOptionsById(inversionApplicationDTO.getInversionOptionId());

        if ( inversionOption == null) {
            return new ResponseEntity<>("Inversión no válida", HttpStatus.FORBIDDEN);
        }

        if (inversionApplicationDTO.getInitialAmount() < inversionOption.getMinAmount()){
            return new ResponseEntity<>("El monto a invertir es menor que el monto mínimo", HttpStatus.FORBIDDEN);
        }

        Account account = accountRepository.findByNumber(inversionApplicationDTO.getAccountNumber());
        if (account == null){
            return new ResponseEntity<>("La cuenta ingresada no existe", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName());
        if (account.getClient().getId() != client.getId()){
            return new ResponseEntity<>("Cuenta de Origen no corresponde al cliente", HttpStatus.FORBIDDEN);
        }

        ClientInvestment clientInvestment = new ClientInvestment(inversionOption, inversionApplicationDTO.getInitialAmount(),inversionApplicationDTO.getInitialDate(), client);
        Transaction transaction = new Transaction(TransactionType.DEBIT, (inversionApplicationDTO.getInitialAmount() * -1), (inversionApplicationDTO.getInversionOptionId() + "Investment approved"), LocalDateTime.now(), account);
        account.setBalance((account.getBalance() - inversionApplicationDTO.getInitialAmount()));

        accountInversionRepository.save(clientInvestment); transactionRepository.save(transaction);
        accountRepository.save(account);


        //Servicio de correo
        try {
            emailService.sendEmailInvestmentHTML(clientInvestment.getClient().getEmail(),clientInvestment.getInvestment().getMacroName() + " " + clientInvestment.getInvestment().getName(), String.valueOf(clientInvestment.getInvestment().getApr()), String.format("%.0f", clientInvestment.compoundAmountProjection(90)), String.format("%.0f",clientInvestment.getInitialAmount()));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Transactional
    @PostMapping("/investments")
    public ResponseEntity<Object> getInvestment(@RequestParam Long id, @RequestParam String toAccountNumber, Authentication authentication){

        if (toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        ClientInvestment clientInvestment = clientInvestmentRepository.getById(id);
        if (clientInvestment == null){
            return new ResponseEntity<>("La inversión solicitada no existe", HttpStatus.FORBIDDEN);
        }

        Account account = accountRepository.findByNumber(toAccountNumber);
        if (account == null){
            return new ResponseEntity<>("La cuenta ingresada no existe", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName());
        if (account.getClient().getId() != client.getId()){
            return new ResponseEntity<>("Cuenta de Origen no corresponde al cliente", HttpStatus.FORBIDDEN);
        }

        if (clientInvestment.isWithdrawn() == true){
            return new ResponseEntity<>("La inversión solicitada tiene el monto retirado", HttpStatus.FORBIDDEN);
        }

        if (!clientInvestment.getInvestment().isFlexible()){
            Long diasTranscurridos = Duration.between(clientInvestment.getInitialDate().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
            if (diasTranscurridos >= clientInvestment.getInvestment().getLockUp()){
                Transaction transaction = new Transaction(TransactionType.CREDIT, Math.round(clientInvestment.compoundAmountToday()), ("Retiro de invesión " + clientInvestment.getInvestment().getMacroName() + clientInvestment.getInvestment().getName()), LocalDateTime.now(), account);
                account.setBalance((account.getBalance() + Math.round(clientInvestment.compoundAmountToday())));
                transactionRepository.save(transaction);
                accountRepository.save(account);
                clientInvestment.setWithdrawn(true);
                clientInvestment.setFinalDate(LocalDate.now());
                clientInvestmentRepository.save(clientInvestment);
            }
            else{
                return new ResponseEntity<>("No ha pasado el tiempo de maduración para retirar la inversion", HttpStatus.FORBIDDEN);
            }
        } else{
            Transaction transaction = new Transaction(TransactionType.CREDIT, Math.round(clientInvestment.compoundAmountToday()), ("Retiro de invesión " + clientInvestment.getInvestment().getMacroName() + clientInvestment.getInvestment().getName()), LocalDateTime.now().plusDays(clientInvestment.getInvestment().getLockUp()), account);
            account.setBalance((account.getBalance() + Math.round(clientInvestment.compoundAmountToday())));
            transactionRepository.save(transaction);
            accountRepository.save(account);
            clientInvestment.setWithdrawn(true);
            clientInvestment.setFinalDate(LocalDate.now());
            clientInvestmentRepository.save(clientInvestment);
        }

        //Uno debe mandar un correo de se retiró el dinero de forma inmediata (no flexible) y el otro se realizó solicitud (flexible) y el dinero se retirará en X día;
        try {
            emailService.sendEmailInvestmentHTML(clientInvestment.getClient().getEmail(),clientInvestment.getInvestment().getMacroName() + " " + clientInvestment.getInvestment().getName(), String.valueOf(clientInvestment.getInvestment().getApr()), String.format("%.0f", clientInvestment.compoundAmountProjection(90)), String.format("%.0f",clientInvestment.getInitialAmount()));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}