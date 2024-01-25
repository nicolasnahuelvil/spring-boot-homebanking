package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.PaymentLoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentLoanRepository extends JpaRepository<PaymentLoan, Long> {

    List<PaymentLoan> findPaymentByClientLoan(ClientLoan clientLoan);
}
