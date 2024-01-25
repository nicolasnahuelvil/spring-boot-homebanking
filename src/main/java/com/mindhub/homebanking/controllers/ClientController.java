package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.EmailServiceImpl;
import com.mindhub.homebanking.util.CacheContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private PaymentLoanRepository paymentLoanRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceImpl emailService;


    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @GetMapping("/clients/current")
    public ClientDTO getClient(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository
                .findAll()
                .stream()
                .map(ClientDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return accountRepository.findAccountsByClient(client).stream().map(AccountDTO::new).collect(toList());
    }


    @PostMapping("/clients")
    public ResponseEntity<Object> createClient(@RequestParam String firstName, @RequestParam String lastName,
                                               @RequestParam String email, @RequestParam String phoneNumber,@RequestParam String password){
        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>("Data desconocida", HttpStatus.FORBIDDEN);
        }

        if(clientRepository.findByEmail(email) != null){
            return new ResponseEntity<>("Nombre está en uso", HttpStatus.UNAUTHORIZED);
        }

        Client client = clientRepository.save(new Client(firstName, lastName, email, phoneNumber,  passwordEncoder.encode(password)));
        String accountNumber = "VIN" + (int) ((Math.random() * (10000000 - 1) + 1));
        this.accountRepository.save(new Account(accountNumber, LocalDateTime.now(), 0, client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam Color cardColor, @RequestParam CardType cardType, Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());

        List<Card> cards = client.getCards().stream().collect(Collectors.toList());
        Random random = new Random();

        int typeCard = 0;

        for(int i = 0; i< cards.size(); i++){
            if(cards.get(i).getType().equals(cardType)){
                typeCard++;
            }
        }

        String card = (cardType == CardType.DEBIT)?"Débito":"Crédito";

        if(typeCard >= 3){
            return new ResponseEntity<>("Cliente ya cuenta con 3 tarjetas de " + card,HttpStatus.FORBIDDEN);
        }

        String cardCode = "";
        String aux;

        for (int i = 0; i < 16; i++){
            if(i % 4 == 0 && i > 0){
                cardCode += "-";
            }
            aux = String.valueOf(random.nextInt(10 - 0) + 0);
            cardCode += aux;
        }

        System.out.println(cardCode);

        String cvv =  "";
        for (int i = 0; i < 3; i++){
            aux = String.valueOf(random.nextInt(10 - 0) + 0);
            cvv += aux;
        }
        String cardHolder = client.getFirstName() + ' ' + client.getLastName();
        this.cardRepository.save(new Card(cardHolder, cardType, cardColor, cardCode, Integer.parseInt(cvv), LocalDateTime.now(), LocalDateTime.now().plusYears(5), client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/clients/pre-login")
    public ResponseEntity<Object> codeForgotPassword(@RequestParam String email) throws MessagingException, IOException {

        int codeNewPassword = new Random().nextInt(999999 - 100000) + 100000;

        Client client = clientRepository.findByEmail(email);

        emailService.sendEmailForgtoPassword(client.getEmail(), client.getFirstName(), client.getLastName(), String.valueOf(codeNewPassword));

        CacheContext.addKey("resetCode-"+email, String.valueOf(codeNewPassword));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/clients/change-pwd")
    public ResponseEntity<Object>  codeForgotPassword(@RequestParam String email, @RequestParam String codeEmailPassword, @RequestParam String newPassword) throws MessagingException, IOException {

        Optional<String> resetKeyOpt = CacheContext.getKeyStr("resetCode-"+email);

        if(resetKeyOpt.isEmpty() || !resetKeyOpt.get().equals(codeEmailPassword)){
            return new ResponseEntity<>("Cdigo invalido", HttpStatus.BAD_REQUEST);
        }

        Client client = clientRepository.findByEmail(email);

        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);

        return  new ResponseEntity<>(HttpStatus.OK);
    }
}
