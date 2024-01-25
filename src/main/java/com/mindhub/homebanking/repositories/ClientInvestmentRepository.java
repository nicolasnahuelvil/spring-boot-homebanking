package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.ClientInvestment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ClientInvestmentRepository extends JpaRepository<ClientInvestment, Long> {
    ClientInvestment getById(Long id);
}