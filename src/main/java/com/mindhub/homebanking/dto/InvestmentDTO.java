package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Investment;

public class InvestmentDTO {

    private long id;
    private double apr, minAmount;
    private String name;
    private int lockUp;
    private boolean flexible;
    private String macroName;

    public InvestmentDTO() {
    }

    public InvestmentDTO(Investment investment) {
        this.id = investment.getId();
        this.apr = investment.getApr();
        this.minAmount = investment.getMinAmount();
        this.name = investment.getName();
        this.lockUp = investment.getLockUp();
        this.flexible = investment.isFlexible();
        this.macroName = investment.getMacroName();

    }

    public long getId() {
        return id;
    }

    public double getApr() {
        return apr;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public String getName() {
        return name;
    }

    public int getLockUp() {
        return lockUp;
    }

    public boolean isFlexible() {
        return flexible;
    }

    public String getMacroName() { return macroName; }
}