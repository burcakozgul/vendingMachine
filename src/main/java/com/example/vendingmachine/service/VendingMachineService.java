package com.example.vendingmachine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.vendingmachine.exception.VendingMachineException;
import com.example.vendingmachine.model.Order;
import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.model.Slot;
import com.example.vendingmachine.repository.OrderRepository;
import com.example.vendingmachine.repository.ProductRepository;
import com.example.vendingmachine.repository.SlotRepository;
import com.example.vendingmachine.types.OrderStatus;
import com.example.vendingmachine.types.Payment;
import com.example.vendingmachine.types.Temperature;
import com.example.vendingmachine.types.request.InitMachineRequest;
import com.example.vendingmachine.types.request.SelectProductRequest;
import com.example.vendingmachine.types.response.GetReceiptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendingMachineService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    OrderRepository orderRepository;

    public void initMachine(InitMachineRequest products) {
        for (Product request : products.getProductList()) {
            Product product = new Product(request.getName(), request.getTemperature(), request.getStock(), request.getCost());
            Slot slot = new Slot(product);
            productRepository.save(product);
            slotRepository.save(slot);
        }
    }

    public Map<Long, String> getSlots() {
        Map<Long, String> slots = new HashMap<>();
        List<Slot> slotList = (List<Slot>) slotRepository.findAll();
        for (Slot slot : slotList) {
            slots.put(slot.getId(), slot.getProduct().getName());
        }
        return slots;
    }

    public Long selectProduct(SelectProductRequest request) throws VendingMachineException {
        Optional<Slot> slot = slotRepository.findById(request.getSlotId());
        Product product = productRepository.findById(slot.get().getProduct().getId()).orElseThrow(() -> new VendingMachineException("Product does not exist"));
        checkSugarCount(request, product);
        Order order = new Order();
        order.setProductId(product.getId());
        order.setStatus(OrderStatus.PRODUCT_SELECTED);
        orderRepository.save(order);
        return order.getId();
    }

    private void checkSugarCount(SelectProductRequest request, Product product) {
        if (!Temperature.HOT.equals(product.getTemperature()) && request.getSugarCount() > 0) {
            throw new VendingMachineException("You cannot choose sugar");
        }
    }


    public void selectCount(Long id, Integer count) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new VendingMachineException("Order does not exist"));
        checkOrderStatus(OrderStatus.PRODUCT_SELECTED, order);
        Product product = productRepository.findById(order.getProductId()).orElseThrow(() -> new VendingMachineException("Product does not exist"));
        checkCount(count);
        checkStock(count, product);
        order.setProductCount(count);
        order.setStatus(OrderStatus.COUNT_SELECTED);
        orderRepository.save(order);
    }

    private void checkCount(Integer count) {
        if (count < 0) {
            throw new VendingMachineException("Count can not be negative");
        }
    }

    private void checkStock(Integer count, Product product) {
        if (product.getStock() < count) {
            throw new VendingMachineException("Stock is not available");
        }
    }

    private void checkOrderStatus(OrderStatus status, Order order) {
        if (!status.equals(order.getStatus())) {
            throw new VendingMachineException("Order status is not available");
        }
    }

    public void selectPaymentType(Long id, Payment payment) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new VendingMachineException("Order does not exist"));
        checkOrderStatus(OrderStatus.COUNT_SELECTED, order);
        Product product = productRepository.findById(order.getProductId()).orElseThrow(() -> new VendingMachineException("Product does not exist"));
        double totalAmount = calculateTotalAmount(product.getCost(), order.getProductCount());
        if (payment.getAmount() < totalAmount) {
            throw new VendingMachineException("Insufficient amount");
        }
        product.setStock(product.getStock() - order.getProductCount());
        Double refund = payment.getAmount() - totalAmount;
        order.setPaymentType(payment.getPaymentType());
        order.setTotalCost(totalAmount);
        order.setRefundAmount(refund);
        order.setStatus(OrderStatus.PAYMENT_TYPE_SELECTED);
        orderRepository.save(order);
    }

    private double calculateTotalAmount(double cost, Integer productCount) {
        return (cost * productCount);
    }

    public Double refundMoney(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new VendingMachineException("Order does not exist"));
        checkOrderStatus(OrderStatus.PAYMENT_TYPE_SELECTED, order);
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
        return order.getRefundAmount();
    }

    public GetReceiptResponse getReceipt(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new VendingMachineException("Order does not exist"));
        checkOrderStatus(OrderStatus.REFUNDED, order);
        order.setStatus(OrderStatus.RECEIPTED);
        String productName = productRepository.findById(order.getProductId()).get().getName();
        return GetReceiptResponse.builder()
            .name(productName)
            .count(order.getProductCount())
            .paymentType(order.getPaymentType())
            .totalCost(order.getTotalCost())
            .refund(order.getRefundAmount())
            .build();
    }


}
