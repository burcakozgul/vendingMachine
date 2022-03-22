package com.example.vendingmachine.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paymentType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCard.class, name = "CreditCard"),
    @JsonSubTypes.Type(value = Cash.class, name = "Cash")
})
public abstract class Payment {
    private String paymentType;
    private Double amount;

    public Payment(String paymentType, Double amount) {
        this.paymentType = paymentType;
        this.amount = amount;
    }
}
