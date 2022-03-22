package com.example.vendingmachine.controller;

import java.util.Map;
import com.example.vendingmachine.exception.VendingMachineException;
import com.example.vendingmachine.service.VendingMachineService;
import com.example.vendingmachine.types.Payment;
import com.example.vendingmachine.types.request.InitMachineRequest;
import com.example.vendingmachine.types.request.SelectProductRequest;
import com.example.vendingmachine.types.response.GetReceiptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendingMachine/v1")
public class VendingMachineController {

    @Autowired
    VendingMachineService vendingMachineService;

    @PostMapping("/init")
    public ResponseEntity<String> initMachine(@RequestBody InitMachineRequest products) {
        vendingMachineService.initMachine(products);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @GetMapping("/slot")
    public Map<Long, String> getSlots() {
        return vendingMachineService.getSlots();
    }

    @PostMapping("/product")
    public Long selectProduct(@RequestBody SelectProductRequest request) throws VendingMachineException {
        return vendingMachineService.selectProduct(request);
    }

    @PostMapping("/count/{id}")
    public ResponseEntity<String> selectCount(@PathVariable Long id, @RequestParam Integer count) {
        vendingMachineService.selectCount(id, count);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<String> selectPaymentType(@PathVariable Long id, @RequestBody Payment payment) {
        vendingMachineService.selectPaymentType(id, payment);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @GetMapping("/refund/{id}")
    public Double refundMoney(@PathVariable Long id) {
        return vendingMachineService.refundMoney(id);
    }

    @GetMapping("/receipt/{id}")
    public GetReceiptResponse getReceipt(@PathVariable Long id) {
        return vendingMachineService.getReceipt(id);
    }

}
