package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ClientLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private double amount;
    private double fee;
    private int payments;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_id")
    private Loan loan;
    @OneToMany(mappedBy = "clientLoan", fetch = FetchType.EAGER)
    Set<PaymentLoan> paymentLoans = new HashSet<>();

    public ClientLoan() {
    }

    public ClientLoan(double amount, double fee, int payments, Client client, Loan loan) {
        this.amount = amount;
        this.fee = fee;
        this.payments = payments;
        this.client = client;
        this.loan = loan;
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

   public double getFee() { return fee; }

   public void setFee(double fee) { this.fee = fee; }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Set<PaymentLoan> getPaymentLoans() {
        return paymentLoans;
    }

    public void setPaymentLoans(Set<PaymentLoan> paymentLoans) {
        this.paymentLoans = paymentLoans;
    }
    public void addPaymentLoan(PaymentLoan paymentLoan) {
        paymentLoan.setClientLoan(this);
        paymentLoans.add(paymentLoan);
    }
}
