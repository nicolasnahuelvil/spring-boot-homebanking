package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.ClientInvestment;
import com.mindhub.homebanking.models.Investment;

import java.time.LocalDate;

public class ClientInvestmentDTO {

    private Long id;
    private Investment investment;
    private double initialAmount;
    private LocalDate initialDate;
    private double compoundAmount;
    private double proyection;
    private String macroName;
    private boolean withdrawn = false;
    private LocalDate finalDate = null;


    public ClientInvestmentDTO() {
    }

    public ClientInvestmentDTO(ClientInvestment clientInvestment) {
        this.id = clientInvestment.getId();
        this.investment = clientInvestment.getInvestment();
        this.initialAmount = clientInvestment.getInitialAmount();
        this.initialDate = clientInvestment.getInitialDate();
        this.compoundAmount = Math.round(clientInvestment.compoundAmountToday());
        this.proyection = Math.round(clientInvestment.compoundAmountProjection(90));
        this.macroName = investment.getMacroName() + " " + investment.getName() ;
        this.withdrawn = clientInvestment.isWithdrawn();
        this.finalDate = clientInvestment.getFinalDate();
    }

    public Long getId() {
        return id;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public double getCompoundAmount() {
        return compoundAmount;
    }

    public double getProyection() {
        return proyection;
    }

    public void setProyection(double proyection) {
        this.proyection = proyection;
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
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