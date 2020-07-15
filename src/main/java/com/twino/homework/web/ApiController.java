package com.twino.homework.web;


import com.twino.homework.db.entity.LoanEntity;
import com.twino.homework.exception.InvalidRequestLoanException;
import com.twino.homework.validator.LoanValidator;
import com.twino.homework.web.request.AddLoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    LoanValidator loanValidator;

    @Autowired
    public ApiController(LoanValidator loanValidator) {
        this.loanValidator = loanValidator;
    }

    @PutMapping(value = "loan/add", produces = {"application/json"})
    public LoanEntity addLoan(@RequestBody AddLoanRequest addLoanRequest) throws InvalidRequestLoanException {
        loanValidator.validate(addLoanRequest);

        return new LoanEntity();
    }
}
