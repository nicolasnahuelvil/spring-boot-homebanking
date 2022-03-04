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

import java.time.LocalDateTime;
import java.util.Set;

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
	public void run(ApplicationArguments args) throws Exception {
		// clientRepository.save(new Client("Nicolás", "Nahuelvil", "nicolasnahuelvil@gmail.com"));
		Client client = new Client("Melba", "Morel", "melba@mindhub.com");
		clientRepository.save(client);

		// clientRepository.save(new Client("Melba", "Morel", "melba@mindhub.com", (Set<Account>) new Account("VIN001", LocalDateTime.now(), 0)));
		 accountRepository.save(new Account("VIN001", LocalDateTime.now().plusDays(1), 5000, client));
		 accountRepository.save(new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, client));
	}


}