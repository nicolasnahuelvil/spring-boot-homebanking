package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientLoanDTO;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientLoanController {

    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @GetMapping("/clientloan/{id}")
    public ClientLoanDTO getPayments(@PathVariable Long id){
        return clientLoanRepository.findById(id).map(ClientLoanDTO::new).orElse(null);
    }
}
