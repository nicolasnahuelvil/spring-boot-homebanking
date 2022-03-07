package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class AccountDTO {

    private  long id;
    private String number;
    private LocalDate dateNow;
    private double balance;
    private Set<TransactionDTO> transactions = new HashSet<>();



    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.dateNow = account.getDateNow();
        this.balance = account.getBalance();
        this.transactions = account.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toSet());
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

    public Set<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", dateNow=" + dateNow +
                ", balance=" + balance +
                '}';
    }
}
