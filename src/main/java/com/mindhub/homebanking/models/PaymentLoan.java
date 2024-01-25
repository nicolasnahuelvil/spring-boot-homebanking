package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PaymentLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime date;
    private double amount;
    private int paymentNumber;
    private String subject;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_loan_id")
    private ClientLoan clientLoan;

    public PaymentLoan() {
    }

    public PaymentLoan(LocalDateTime date, double amount, int paymentNumber, String subject, ClientLoan clientLoan) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.paymentNumber = paymentNumber;
        this.subject = subject;
        this.clientLoan = clientLoan;
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

    public ClientLoan getClientLoan() {
        return clientLoan;
    }

    public void setClientLoan(ClientLoan clientLoan) {
        this.clientLoan = clientLoan;
    }
}
