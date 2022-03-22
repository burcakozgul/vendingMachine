package com.example.vendingmachine.types;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("CreditCard")
public class CreditCard extends Payment {
    private final String cardNo;
    private final String cardHolder;
    private final String cardCvv;
    private final boolean isContactless;
    private Double amount;

    public CreditCard(boolean isContactless, Double amount, String cardNo, String cardHolder, String cardCvv) {
        super("CreditCard", amount);
        this.isContactless = isContactless;
        this.amount = amount;
        this.cardNo = cardNo;
        this.cardHolder = cardHolder;
        this.cardCvv = cardCvv;
    }

    @Override
    public Double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
