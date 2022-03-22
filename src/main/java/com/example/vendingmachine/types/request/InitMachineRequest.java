package com.example.vendingmachine.types.request;

import java.util.List;
import com.example.vendingmachine.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitMachineRequest {
    private List<Product> productList;
}
