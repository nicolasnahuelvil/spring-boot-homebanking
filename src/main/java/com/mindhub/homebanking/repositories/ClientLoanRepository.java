package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientLoanRepository extends JpaRepository<ClientLoan, Long> {
     List< ClientLoan> findAllByClient(Client client);
     ClientLoan getLoanById(Long id);
}
