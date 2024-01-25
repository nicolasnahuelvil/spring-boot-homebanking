package com.mindhub.homebanking.dto;

import java.time.LocalDate;

public class InversionApplicationDTO {

    private String accountNumber;
    private Long inversionOptionId;
    private double initialAmount;
    private LocalDate initialDate;

    public InversionApplicationDTO() {
    }

    public InversionApplicationDTO(String accountNumber, Long inversionOptionId, double initialAmount, LocalDate initialDate) {
        this.accountNumber = accountNumber;
        this.inversionOptionId = inversionOptionId;
        this.initialAmount = initialAmount;
        this.initialDate = initialDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getInversionOptionId() {
        return inversionOptionId;
    }

    public void setInversionOptionId(Long inversionOptionId) {
        this.inversionOptionId = inversionOptionId;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }
}