package com.mindhub.homebanking.dto;


import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class ClientLoanDTO {

    private Long id;
    private double amount;
    private int payments;
    private double fee;
    private String loanType;
    private LoanDTO loanDTO;


    private Set<PaymentLoanDTO> paymentLoanDTOS = new HashSet<>();

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
        this.fee = clientLoan.getFee();
        this.loanType = clientLoan.getLoan().getName();
        this.paymentLoanDTOS = clientLoan.getPaymentLoans().stream().map(PaymentLoanDTO::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Set<PaymentLoanDTO> getPaymentLoanDTOS() {
        return paymentLoanDTOS;
    }

    public void setPaymentLoanDTOS(Set<PaymentLoanDTO> paymentLoanDTOS) {
        this.paymentLoanDTOS = paymentLoanDTOS;
    }
}
