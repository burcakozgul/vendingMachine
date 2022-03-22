package com.example.vendingmachine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
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
import com.example.vendingmachine.types.Cash;
import com.example.vendingmachine.types.CreditCard;
import com.example.vendingmachine.types.OrderStatus;
import com.example.vendingmachine.types.Temperature;
import com.example.vendingmachine.types.request.InitMachineRequest;
import com.example.vendingmachine.types.request.SelectProductRequest;
import com.example.vendingmachine.types.response.GetReceiptResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VendingMachineServiceTest {

    @InjectMocks
    VendingMachineService vendingMachineService;

    @Mock
    ProductRepository productRepository;

    @Mock
    SlotRepository slotRepository;

    @Mock
    OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test initMachine.")
    void initMachine_test() {
        Product product = new Product("M&M", 8, 16.75);
        Product product2 = new Product("Coffee", Temperature.HOT, 8, 5.75);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);
        InitMachineRequest request = new InitMachineRequest(productList);
        vendingMachineService.initMachine(request);

        verify(productRepository, times(2)).save(any());

    }

    @Test
    @DisplayName("Test getSlots.")
    void getSlots_test() {
        Slot slot = new Slot(1L, new Product("M&M", 8, 16.75));
        Slot slot2 = new Slot(2L, new Product("Coffee", Temperature.HOT, 8, 5.7));
        List<Slot> slots = new ArrayList<>();
        slots.add(slot);
        slots.add(slot2);
        doReturn(slots).when(slotRepository).findAll();
        Map<Long, String> slotsMap = vendingMachineService.getSlots();

        assertEquals(2, slotsMap.values().size());
    }

    @Test
    @DisplayName("Test selectProduct. When Product does not exist then throw exception")
    void selectProduct_When_ProductNotExist_Then_Throw_Exception() {
        String expected = "Product does not exist";
        Optional<Slot> slot = Optional.of(new Slot(1L, new Product("M&M", 8, 16.75)));
        SelectProductRequest request = new SelectProductRequest(1L, 0);
        Optional<Product> product = Optional.empty();
        doReturn(slot).when(slotRepository).findById(1L);
        doReturn(product).when(productRepository).findById(any());
        VendingMachineException exception = assertThrows(VendingMachineException.class, () -> vendingMachineService.selectProduct(request));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectProduct. When Product does not hot then throw exception")
    void selectProduct_When_ProductNotHot_Then_Throw_Exception() {
        String expected = "You cannot choose sugar";
        Optional<Slot> slot = Optional.of(new Slot(1L, new Product("M&M", 8, 16.75)));
        SelectProductRequest request = new SelectProductRequest(1L, 1);
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(slot).when(slotRepository).findById(1L);
        doReturn(product).when(productRepository).findById(any());
        VendingMachineException exception = assertThrows(VendingMachineException.class, () -> vendingMachineService.selectProduct(request));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectProduct")
    void selectProduct_test() {
        Optional<Slot> slot = Optional.of(new Slot(1L, new Product("M&M", 8, 16.75)));
        SelectProductRequest request = new SelectProductRequest(1L, 0);
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(slot).when(slotRepository).findById(1L);
        doReturn(product).when(productRepository).findById(any());
        vendingMachineService.selectProduct(request);

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test selectCount. When order status is not available then throw exception.")
    void selectCount_When_OrderStatusNotAvail_Then_Throw_Exception() {
        String expected = "Order status is not available";
        Optional<Order> order = Optional.of(new Order(1L, 1L, null, null, null, null, null));
        doReturn(order).when(orderRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class, () -> vendingMachineService.selectCount(1L, 2));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectCount. When count is negative then throw exception.")
    void selectCount_When_CountIsNegative_Then_Throw_Exception() {
        String expected = "Count can not be negative";
        Optional<Order> order = Optional.of(new Order(1L, 1L, null, null, null, null, OrderStatus.PRODUCT_SELECTED));
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class, () -> vendingMachineService.selectCount(1L, -2));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectCount. When stock is not available then throw exception.")
    void selectCount_When_StockIsNotAvailable_Then_Throw_Exception() {
        String expected = "Stock is not available";
        Optional<Order> order = Optional.of(new Order(1L, 1L, null, null, null, null, OrderStatus.PRODUCT_SELECTED));
        Optional<Product> product = Optional.of(new Product("M&M", 1, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class, () -> vendingMachineService.selectCount(1L, 2));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectCount")
    void selectCount_Test() {
        Optional<Order> order = Optional.of(new Order(1L, 1L, null, null, null, null, OrderStatus.PRODUCT_SELECTED));
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(1L);
        vendingMachineService.selectCount(1L, 2);

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test selectPaymentType. When order status is not available then throw exception.")
    void selectPaymentType_When_OrderStatusNotAvail_Then_Throw_Exception() {
        String expected = "Order status is not available";
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, null, null, null, null));
        doReturn(order).when(orderRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class,
            () -> vendingMachineService.selectPaymentType(1L, new CreditCard(true, 50.0, "456723456464564", "Burcak", "234")));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectPaymentType. When insufficient amount then throw exception.")
    void selectPaymentType_When_InsufficientAmount_Then_Throw_Exception() {
        String expected = "Insufficient amount";
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, null, null, null, OrderStatus.COUNT_SELECTED));
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class,
            () -> vendingMachineService.selectPaymentType(1L, new Cash(false, 5.0)));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test selectPaymentType.")
    void selectPaymentType_Test() {
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, null, null, null, OrderStatus.COUNT_SELECTED));
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(1L);
        vendingMachineService.selectPaymentType(1L, new Cash(false, 35.0));

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test refundMoney. When order status is not available then throw exception.")
    void refundMoney_When_OrderStatusNotAvail_Then_Throw_Exception() {
        String expected = "Order status is not available";
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, null, null, null, null));
        doReturn(order).when(orderRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class,
            () -> vendingMachineService.refundMoney(1L));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test refundMoney.")
    void refundMoney_Test() {
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, "CreditCard", 16.0, 4.0, OrderStatus.PAYMENT_TYPE_SELECTED));
        doReturn(order).when(orderRepository).findById(1L);
        Double refundAmount = vendingMachineService.refundMoney(1L);

        assertEquals(4.0, refundAmount);
    }

    @Test
    @DisplayName("Test getReceipt. When order status is not available then throw exception.")
    void getReceipt_When_OrderStatusNotAvail_Then_Throw_Exception() {
        String expected = "Order status is not available";
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, null, null, null, null));
        doReturn(order).when(orderRepository).findById(1L);
        VendingMachineException exception = assertThrows(VendingMachineException.class,
            () -> vendingMachineService.getReceipt(1L));

        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Test getReceipt.")
    void getReceipt_Test() {
        GetReceiptResponse expectedResponse = new GetReceiptResponse("M&M",2,"CreditCard",16.0,4.0);
        Optional<Order> order = Optional.of(new Order(1L, 1L, 2, "CreditCard", 16.0, 4.0, OrderStatus.REFUNDED));
        Optional<Product> product = Optional.of(new Product("M&M", 8, 16.75));
        doReturn(order).when(orderRepository).findById(1L);
        doReturn(product).when(productRepository).findById(any());
        GetReceiptResponse response = vendingMachineService.getReceipt(1L);

        assertEquals(expectedResponse.getName(), response.getName());
    }


}
