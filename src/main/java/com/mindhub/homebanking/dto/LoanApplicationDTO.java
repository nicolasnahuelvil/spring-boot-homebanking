package com.mindhub.homebanking.dto;

public class LoanApplicationDTO {

    private Long loanId;
    private double amount;
    private int payments;
    private double fee;
    private String toAccountNumber;

    public LoanApplicationDTO(Long loanId, double amount, int payments, double fee, String toAccountNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payments = payments;
        this.fee = fee;
        this.toAccountNumber = toAccountNumber;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
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

    public double getFee() { return fee; }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }
}
