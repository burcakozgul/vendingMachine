package com.example.vendingmachine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({VendingMachineException.class})
    public ResponseEntity<Object> handleApiException(final VendingMachineException ex) {
        final ApiError apiError;

        apiError = createApiError(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    private ApiError createApiError(String message) {
        return new ApiError(message);
    }
}
