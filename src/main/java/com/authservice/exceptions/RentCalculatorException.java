package com.authservice.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RentCalculatorException extends RuntimeException {

    public RentCalculatorException(final String message) {
        super(message);
    }

    public RentCalculatorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
