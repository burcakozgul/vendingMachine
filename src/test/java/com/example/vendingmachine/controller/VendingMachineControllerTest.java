package com.example.vendingmachine.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.service.VendingMachineService;
import com.example.vendingmachine.types.Cash;
import com.example.vendingmachine.types.CreditCard;
import com.example.vendingmachine.types.Temperature;
import com.example.vendingmachine.types.request.InitMachineRequest;
import com.example.vendingmachine.types.request.SelectProductRequest;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(VendingMachineController.class)
public class VendingMachineControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VendingMachineService vendingMachineService;

    Gson gson = new Gson();

    @Test
    public void initMachine_Test() throws Exception {
        Product product = new Product("Coffee", Temperature.HOT, 8, 16.75);
        Product product2 = new Product("Metro", 8, 5.75);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/vendingMachine/v1/init")
                .content(gson.toJson(new InitMachineRequest(productList)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        assertEquals("Success", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void getSlots_Test() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/vendingMachine/v1/slot")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        assertEquals("{}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void selectProduct_Test() throws Exception {
        SelectProductRequest request = new SelectProductRequest(1L, 0);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/vendingMachine/v1/product")
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    }

    @Test
    public void selectCount_Test() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/vendingMachine/v1/count/{id}?count=2", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        assertEquals("Success", mvcResult.getResponse().getContentAsString());

    }
    @Test
    public void refundMoney_Test() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/vendingMachine/v1/refund/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    }

    @Test
    public void getReceipt_Test() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/vendingMachine/v1/receipt/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    }

}
