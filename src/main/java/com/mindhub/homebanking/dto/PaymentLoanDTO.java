package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.PaymentLoan;

import java.time.LocalDateTime;

public class PaymentLoanDTO {
    private Long id;
    private LocalDateTime date;
    private double amount;
    private int paymentNumber;
    private String subject;

    public PaymentLoanDTO(PaymentLoan paymentLoan) {
        this.id = paymentLoan.getId();
        this.date = paymentLoan.getDate();
        this.amount = paymentLoan.getAmount();
        this.paymentNumber = paymentLoan.getPaymentNumber();
        this.subject = paymentLoan.getSubject();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
