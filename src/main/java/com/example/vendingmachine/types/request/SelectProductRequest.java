package com.example.vendingmachine.types.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectProductRequest {
    private Long slotId;
    private int sugarCount;

}
