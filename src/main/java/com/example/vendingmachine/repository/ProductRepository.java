package com.example.vendingmachine.repository;

import com.example.vendingmachine.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findByName(String name);
}
