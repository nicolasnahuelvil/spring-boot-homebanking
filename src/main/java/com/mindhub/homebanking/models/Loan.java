package com.mindhub.homebanking.models;

import com.sun.istack.NotNull;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private double maxAmount;
    @NotNull
    @ElementCollection
    @Column(name = "PAYMENTS")
    private List<Integer> payments = new ArrayList<>();
    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<ClientLoan> clientLoans = new HashSet<>();


    //Constructor sin parámetros
    public Loan() {
    }

    public Loan(Long id, String name, double maxAmount, List<Integer> payments) {
        this.id = id;
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }

    public Loan(String name, double maxAmount, List<Integer> payments) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }

    public Loan(String name, double maxAmount, List<Integer> payments, Set<ClientLoan> clientLoans) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
        this.clientLoans = clientLoans;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }

    public void setClientLoans(Set<ClientLoan> clientLoans) {
        this.clientLoans = clientLoans;
    }

    public void addClientLoans(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        clientLoans.add(clientLoan);
    }


}

