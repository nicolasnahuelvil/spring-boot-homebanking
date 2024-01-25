package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@EnableAsync
@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initialData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository, InvestmentRepository investmentRepository,
							   ClientInvestmentRepository clientInvestmentRepository) {
		return (args) -> {

			String passwordClientOne = passwordEncoder.encode("1234");

			Client clientOne = clientRepository.save(new Client("Melba", "Morel", "melba@mindhub.com", "955106509", passwordClientOne));
			Client clientTwo = clientRepository.save(new Client("Lorenzo", "Sanjuan", "lorenzo@mindhub.com", "923541223", passwordClientOne));
			clientRepository.save(new Client("Nicolas", "Nahuelvil", "nicolasnahuelvil@gmail.com", "955106509", passwordEncoder.encode("nicolas")));

			Account accountOne = accountRepository.save(new Account("VIN001", LocalDateTime.now(), 1978432, clientOne));
			Account accountTwo = accountRepository.save(new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, clientOne));
			Account accountThree = accountRepository.save(new Account("VIN003", LocalDateTime.now().minusDays(5), 1000, clientTwo));
			Account accountFour = accountRepository.save(new Account("VIN004", LocalDateTime.now().minusDays(1), 20000, clientTwo));

			transactionRepository.save(new Transaction(TransactionType.CREDIT, 10000, "Transferencia Recibida", LocalDateTime.now().minusWeeks(2), accountOne));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -4352, "Compra en Tienda", LocalDateTime.now().plusDays(2), accountOne));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -2890, "Transferencia Saliente", LocalDateTime.now().minusDays(3), accountOne));
			transactionRepository.save(new Transaction(TransactionType.CREDIT, 10000, "Devolución compra", LocalDateTime.now().minusDays(1), accountTwo));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -2000, "Transferencia Saliente", LocalDateTime.now().minusDays(4), accountTwo));
			transactionRepository.save(new Transaction(TransactionType.CREDIT, 40000, "Transferencia Recibida", LocalDateTime.now().minusDays(8), accountThree));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -7500, "Compra Comercio", LocalDateTime.now().minusDays(9), accountThree));
			transactionRepository.save(new Transaction(TransactionType.CREDIT, 9990, "Pago Recibido", LocalDateTime.now().minusDays(2), accountFour));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -45600, "Pago Cuentas", LocalDateTime.now().minusDays(1), accountFour));

			Loan loan1 = loanRepository.save(new Loan("Hipotecario", 500000, Arrays.asList(12, 24, 36, 48, 60)));
			Loan loan2 = loanRepository.save(new Loan("Personal", 100000, Arrays.asList(6, 12, 24)));
			Loan loan3 = loanRepository.save(new Loan("Automotriz", 300000, Arrays.asList(6, 12, 24, 36)));

			clientLoanRepository.save(new ClientLoan(400000, 7000, 60, clientOne, loan1));
			clientLoanRepository.save(new ClientLoan(50000, 5000, 12, clientOne, loan2));
			clientLoanRepository.save(new ClientLoan(100000, 5000, 24, clientTwo, loan2));
			clientLoanRepository.save(new ClientLoan(200000, 6666, 36, clientTwo, loan3));

			cardRepository.save(new Card(clientOne.getFirstName() + " " + clientOne.getLastName(), CardType.DEBIT, Color.GOLD, "5085-1234-0987-8976", 987, LocalDateTime.now(), LocalDateTime.now().plusYears(5), clientOne));
			cardRepository.save(new Card(clientOne.getFirstName() + " " + clientOne.getLastName(), CardType.CREDIT, Color.TITANIUM, "5085-4356-1234-1234", 673, LocalDateTime.now(), LocalDateTime.now().plusYears(5), clientOne));
			cardRepository.save(new Card(clientTwo.getFirstName() + " " + clientTwo.getLastName(), CardType.CREDIT, Color.GOLD, "5085-7635-1927-6347", 613, LocalDateTime.now(), LocalDateTime.now().plusYears(5), clientTwo));

			Investment invopt1 = new Investment("30 Días", "Deposito a Plazo", 0.08, 10000, 30, false);
			Investment invopt2 = new Investment("60 Días", "Deposito a Plazo", 0.08, 10000, 60, false);
			Investment invopt3 = new Investment("90 Días", "Deposito a Plazo", 0.08, 10000, 90, false);
			Investment invopt4 = new Investment("120 Días", "Deposito a Plazo", 0.08, 10000, 120, false);
			Investment invopt5 = new Investment("T+1 Días", "Deposito a Plazo", 0.05, 20000, 1, true);

			investmentRepository.save(invopt1);
			investmentRepository.save(invopt2);
			investmentRepository.save(invopt3);
			investmentRepository.save(invopt4);
			investmentRepository.save(invopt5);

			ClientInvestment clientInvestmentOne = new ClientInvestment(invopt1, 15000, LocalDate.now().minusDays(7), clientOne);
			ClientInvestment clientInvestmentTwo = new ClientInvestment(invopt2, 30000, LocalDate.now().minusDays(3), clientOne);
			clientInvestmentRepository.save(clientInvestmentOne);
			clientInvestmentRepository.save(clientInvestmentTwo);
		};
	}


}
