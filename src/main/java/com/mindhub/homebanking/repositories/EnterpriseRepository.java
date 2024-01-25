package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.PaymentLoan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<PaymentLoan, Long> {
}
