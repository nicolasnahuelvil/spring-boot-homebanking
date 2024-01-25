package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    @OneToMany(mappedBy="investment", fetch=FetchType.EAGER)
    private final Set<ClientInvestment> clientInvestments = new HashSet<>();
    private String name, macroName;
    private double apr, minAmount;
    private int lockUp;
    private boolean flexible;

    public Investment() {
    }

    public Investment(String name, String macroName, double apr, double minAmount, int lockUp, boolean flexible) {
        this.name = name;
        this.macroName = macroName;
        this.apr = apr;
        this.minAmount = minAmount;
        this.lockUp = lockUp;
        this.flexible = flexible;
    }

    public Set<ClientInvestment> getAccountInversions() {
        return clientInvestments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getApr() {
        return apr;
    }

    public void setApr(double apr) {
        this.apr = apr;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public int getLockUp() {
        return lockUp;
    }

    public void setLockUp(int lockUp) {
        this.lockUp = lockUp;
    }

    public boolean isFlexible() {
        return flexible;
    }

    public void setFlexible(boolean flexible) {
        this.flexible = flexible;
    }

    public Long getId() {
        return id;
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
    }
}