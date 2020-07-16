package com.twino.homework.validator;

import com.twino.homework.exception.InvalidRequestLoanException;
import com.twino.homework.web.request.AddLoanRequest;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {

    public void validate(AddLoanRequest addLoanRequest) throws InvalidRequestLoanException {
        if (addLoanRequest.getName() == null) {
            throw new InvalidRequestLoanException("name is mandatory");
        }

        if (addLoanRequest.getSurname() == null) {
            throw new InvalidRequestLoanException("surname is mandatory");
        }

        if (addLoanRequest.getLoanAmount() == null) {
            throw new InvalidRequestLoanException("loanAmount is mandatory");
        }

        if (addLoanRequest.getTermDays() == null) {
            throw new InvalidRequestLoanException("term is mandatory");
        }

        if (addLoanRequest.getLoanAmount() < 1) {
            throw new InvalidRequestLoanException("loanAmount should be 1 or more");
        }

        if (addLoanRequest.getTermDays() < 1) {
            throw new InvalidRequestLoanException("term should be 1 or more");
        }

        this.validateString("name", addLoanRequest.getName());
        this.validateString("surname", addLoanRequest.getSurname());
    }

    protected void validateString(String name, String value) throws InvalidRequestLoanException {
        if (value.length() < 3) {
            throw new InvalidRequestLoanException(String.format("%s value should be 3 or more symbols (%s)", name, value));
        }
        if (value.length() > 64) {
            throw new InvalidRequestLoanException(String.format("%s value should be less than 64 (%s)", name, value));
        }

        if (! value.matches("[a-zA-Z ]+")) {
            throw new InvalidRequestLoanException(
                    String.format("%s must contain only english alphabetic symbols and spaces (%s)", name, value)
            );
        }
    }
}
