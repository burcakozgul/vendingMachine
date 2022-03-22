package com.example.vendingmachine.exception;

import lombok.Data;

@Data
public class ApiError {
    private String message;

    public ApiError(String message) {
        this.message = message;
    }
}
