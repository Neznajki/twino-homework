package com.twino.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestLoanException extends Exception {
    public InvalidRequestLoanException(String message) {
        super(message);
    }
}
