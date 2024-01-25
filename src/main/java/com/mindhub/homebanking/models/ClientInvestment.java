package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;


@Entity
public class ClientInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="investment_id")
    private Investment investment;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    private double initialAmount;
    private LocalDate initialDate;
    private boolean withdrawn = false;
    private LocalDate finalDate = null;

    public ClientInvestment() {
    }

    public ClientInvestment(Investment investment, double initialAmount, LocalDate initialDate, Client client) {
        this.investment = investment;
        this.initialAmount = initialAmount;
        this.initialDate = initialDate;
        this.client = client;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public double compoundAmountToday() {
        Long diasTranscurridos =     Duration.between(this.initialDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        double aprPorcentual = (this.investment.getApr()/100);
        double compoundAmount = this.initialAmount;
        for (int i = 0; i < diasTranscurridos; i++){
            compoundAmount *= (1+aprPorcentual);
        }
        return compoundAmount;
    }

    public double compoundAmountProjection(int dias) {
        Long diasTranscurridos = Duration.between(this.initialDate.atStartOfDay(), this.initialDate.plusDays(dias).atStartOfDay()).toDays();
        double aprPorcentual = (this.investment.getApr()/100);
        double compoundAmount = this.initialAmount;
        for (int i = 0; i < diasTranscurridos; i++){
            compoundAmount *= (1+aprPorcentual);
        }
        return compoundAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment inversionOption) {
        this.investment = inversionOption;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }
}