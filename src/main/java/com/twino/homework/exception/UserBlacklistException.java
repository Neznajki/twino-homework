package com.twino.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UserBlacklistException extends Exception{
    public UserBlacklistException(String message) {
        super(message);
    }
}
