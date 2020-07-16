package com.twino.homework.web;


import com.twino.homework.exception.InvalidRequestLoanException;
import com.twino.homework.exception.LoanLimitReachedException;
import com.twino.homework.exception.UserBlacklistException;
import com.twino.homework.exception.UserNotFoundException;
import com.twino.homework.service.LoanServiceImpl;
import com.twino.homework.service.UserServiceImpl;
import com.twino.homework.validator.LoanValidator;
import com.twino.homework.web.request.AddLoanRequest;
import com.twino.homework.web.response.LoanResponseData;
import com.twino.homework.web.response.UserResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ApiController {
    LoanValidator loanValidator;
    UserServiceImpl userService;
    LoanServiceImpl loanService;

    @Autowired
    public ApiController(
        LoanValidator loanValidator,
        UserServiceImpl userService,
        LoanServiceImpl loanService
    ) {
        this.loanValidator = loanValidator;
        this.userService = userService;
        this.loanService = loanService;
    }

    @PutMapping(value = "loan/add", produces = {"application/json"})
    public LoanResponseData addLoan(@RequestBody AddLoanRequest addLoanRequest) throws InvalidRequestLoanException, IOException, UserBlacklistException, LoanLimitReachedException {
        loanValidator.validate(addLoanRequest);

        return new LoanResponseData(loanService.createLoan(addLoanRequest));
    }

    @RequestMapping(value = "blacklist/add/{uuid}", produces = {"application/json"})
    public UserResponseData addBlacklist(@PathVariable("uuid") String uuid) throws UserNotFoundException {
        return new UserResponseData(userService.addToBlacklistByUuid(uuid));
    }
}
