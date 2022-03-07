package com.mindhub.homebanking.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Account  {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @NotNull
    private String number;
    @NotNull
    private LocalDate dateNow;
    @NotNull
    private double balance;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client client;
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Transaction> transactions = new HashSet<>();


    public Account() {
    }

    public Account(String number, LocalDate dateNow, double balance) {
        this.number = number;
        this.dateNow = dateNow;
        this.balance = balance;
    }

    public Account(String number, LocalDate dateNow, double balance, Client client) {
        this.number = number;
        this.dateNow = dateNow;
        this.balance = balance;
        this.client = client;
    }

    public Account(long id, String number, LocalDate dateNow, double balance, Client client) {
        this.id = id;
        this.number = number;
        this.dateNow = dateNow;
        this.balance = balance;
        this.client = client;
    }

    public long getId() {
        return id;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getDateNow() {
        return dateNow;
    }

    public void setDateNow(LocalDate dateNow) {
        this.dateNow = dateNow;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", dateNow=" + dateNow +
                ", balance=" + balance +
                ", client=" + client +
                '}';
    }
}
