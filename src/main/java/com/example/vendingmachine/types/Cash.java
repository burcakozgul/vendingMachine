package com.example.vendingmachine.types;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Cash")
public class Cash extends Payment {
    private Double amount;
    private final boolean isCoin;

    public Cash(boolean isCoin, Double amount) {
        super("Cash", amount);
        this.isCoin = isCoin;
        this.amount = amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
