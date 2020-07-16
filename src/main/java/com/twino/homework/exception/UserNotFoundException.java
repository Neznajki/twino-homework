package com.twino.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String uuid) {
        super(String.format("user with uuid %s not found", uuid));
    }
}
