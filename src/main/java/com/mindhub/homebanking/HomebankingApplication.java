package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;


@SpringBootApplication
public class HomebankingApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        //Creación de Clients
        Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
        Client client2 = new Client("Nicolás", "Nahuelvil", "nicolasnahuelvil@gmail.com");
        clientRepository.save(client1);
        clientRepository.save(client2);

        //Creación de cuentas bancarias
        accountRepository.save(new Account("VIN001", LocalDate.now(), 5000, client1));
        accountRepository.save(new Account("VIN002", LocalDate.now().plusDays(1), 7500, client1));
        accountRepository.save(new Account("VIN003", LocalDate.now(), 1450000, client2));

    }


}