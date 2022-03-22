package com.example.vendingmachine.repository;

import com.example.vendingmachine.model.Slot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends CrudRepository<Slot, Long> {
}
