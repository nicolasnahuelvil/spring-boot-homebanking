package com.mindhub.homebanking.models;


import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
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
    private LocalDateTime dateNow;
    @NotNull
    private double balance;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    private Client client;


    public Account() {
    }

    public Account(String number, LocalDateTime dateNow, double balance) {
        this.number = number;
        this.dateNow = dateNow;
        this.balance = balance;
    }

    public Account(String number, LocalDateTime dateNow, double balance, Client client) {
        this.number = number;
        this.dateNow = dateNow;
        this.balance = balance;
        this.client = client;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getDateNow() {
        return dateNow;
    }

    public void setDateNow(LocalDateTime dateNow) {
        this.dateNow = dateNow;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
