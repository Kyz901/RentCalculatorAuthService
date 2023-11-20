package com.authservice.exceptions;

public class NoSuchProductException extends RentCalculatorException {

    public NoSuchProductException(String errorMessage) {
        super(errorMessage);
    }
}
