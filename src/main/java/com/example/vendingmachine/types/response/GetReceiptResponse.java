package com.example.vendingmachine.types.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetReceiptResponse {
    private String name;
    private Integer count;
    private String paymentType;
    private Double totalCost;
    private Double refund;
}
