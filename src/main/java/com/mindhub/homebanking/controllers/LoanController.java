package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ClientLoanRepository clientLoanRepository;

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> loan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){

            Loan loan = loanRepository.findLoanById(loanApplicationDTO.getLoanId());
            Client clientAuth = clientRepository.findByEmail(authentication.getName());
            Account toAccount = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());

            //Verificar que los campos existan.
            if(loanApplicationDTO.getToAccountNumber() == null || loanApplicationDTO.getAmount() == 0 || loanApplicationDTO.getPayments() == 0){
                return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }
            //Verificar que el prestamo exista
            else if(loan == null){
                return new ResponseEntity<>("Loan doesnt exist", HttpStatus.FORBIDDEN);
            }
            //Verificar que el monto ingresado no exceda el monto del credito
            else if(loanApplicationDTO.getAmount()  > loan.getMaxAmount()){
                return new ResponseEntity<>("Amount exceeded", HttpStatus.FORBIDDEN);
            }
            //Verificar que la cuenta destino exista.
            else if(toAccount == null){
                return new ResponseEntity<>("Account destiny doesnt exist", HttpStatus.FORBIDDEN);
            }
            //Verificar que la cuenta corresponda al cliente autenticado
            else if(toAccount.getClient() != clientAuth){
                return new ResponseEntity<>("Account other than authenticated client", HttpStatus.FORBIDDEN);
            }
            //Verifica que la cantidad de cuotas se encuentre entre las disponibles del préstamo
            else if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){
                return new ResponseEntity<>("Account other than authenticated client", HttpStatus.FORBIDDEN);
            }else{
                double newAmount = loanApplicationDTO.getAmount() * 1.20;
                toAccount.setBalance(toAccount.getBalance() - newAmount);
                transactionRepository.save(new Transaction(TransactionType.CREDIT, newAmount, loan.getName()+" / Loan approved", LocalDate.now(), toAccount));
                clientLoanRepository.save(new ClientLoan(newAmount, loanApplicationDTO.getPayments(), clientAuth, loan));
            }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/loans")
    public List<LoanDTO>  getLoans(){
        return loanRepository
                .findAll()
                .stream()
                .map(LoanDTO::new)
                .collect(Collectors.toList());
    }


}
