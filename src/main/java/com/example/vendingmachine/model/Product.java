package com.example.vendingmachine.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.example.vendingmachine.types.Temperature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Temperature temperature;
    private int stock;
    private double cost;
    @OneToOne(mappedBy = "product")
    private Slot slot;

    public Product(String name, Temperature temperature, int stock, double cost) {
        this.name = name;
        this.temperature = temperature;
        this.stock = stock;
        this.cost = cost;
    }

    public Product(String name, int stock, double cost) {
        this.name = name;
        this.stock = stock;
        this.cost = cost;
        this.slot = slot;
    }
}
