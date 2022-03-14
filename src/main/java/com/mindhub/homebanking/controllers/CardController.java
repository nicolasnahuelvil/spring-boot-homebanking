package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Random;

@Controller
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientRepository clientRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(
            @RequestParam CardType cardType, @RequestParam CardColor cardColor,
            Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        int numCardTypes = cardRepository.countByClientAndType(client, cardType);
        Random random = new Random();
        String generateNumberCard = String.valueOf(random.nextInt(9999-1000)+1000);
        String generateCvv = String.valueOf(random.nextInt(999-100)+100);

        if (cardType == null || cardColor == null) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (numCardTypes  >= 3) {
            return new ResponseEntity<>("Number of cards exceeded", HttpStatus.FORBIDDEN);
        } else {
            cardRepository.save(new Card(
                    client.getFirstName()+" "+client.getLastName(), cardType, cardColor,
                    generateNumberCard+"-"+generateNumberCard+"-"+generateNumberCard+"-"+generateNumberCard, generateCvv,
                    LocalDate.now(),
                    LocalDate.now().plusYears(5),
                    client));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}


