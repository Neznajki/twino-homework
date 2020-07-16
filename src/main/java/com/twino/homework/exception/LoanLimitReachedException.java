package com.twino.homework.exception;

import com.twino.homework.service.RepeatServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class LoanLimitReachedException extends Exception {
    public LoanLimitReachedException(String countryCode) {
        super(String.format(
            "loan limit reached for %s max allowed limit is %d in %d seconds",
            countryCode,
            RepeatServiceImpl.maxRepeats,
            RepeatServiceImpl.intervalSeconds
        ));
    }
}
