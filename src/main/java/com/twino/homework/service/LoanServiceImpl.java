package com.twino.homework.service;

import com.twino.homework.db.entity.LoanEntity;
import com.twino.homework.db.entity.UserEntity;
import com.twino.homework.db.repository.LoanRepository;
import com.twino.homework.exception.LoanLimitReachedException;
import com.twino.homework.exception.UserBlacklistException;
import com.twino.homework.web.request.AddLoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class LoanServiceImpl {
    UserServiceImpl userService;
    LoanRepository loanRepository;
    CountryServiceImpl countryService;
    RepeatServiceImpl repeatService;

    @Autowired
    public LoanServiceImpl(
        UserServiceImpl userService,
        LoanRepository loanRepository,
        CountryServiceImpl countryService,
        RepeatServiceImpl repeatService
    ) {
        this.userService = userService;
        this.loanRepository = loanRepository;
        this.countryService = countryService;
        this.repeatService = repeatService;
    }

    public LoanEntity createLoan(AddLoanRequest request) throws IOException, UserBlacklistException, LoanLimitReachedException {
        UserEntity user = userService.getUserEntity(request.getName(), request.getSurname());

        if (userService.isUserBlacklisted(user)) {
            throw new UserBlacklistException(
                String.format(
                    "user %s %s with uuid %s found in blacklist",
                    user.getName(),
                    user.getSurname(),
                    user.getUniqueId()
                )
            );
        }

        String countryCode = countryService.getCurrentRequestAgentId();
        if (! repeatService.isRepeatRequestAllowed(countryCode)) {
            throw new LoanLimitReachedException(countryCode);
        }

        LoanEntity loan = new LoanEntity();

        loan.setUserByUserId(user);
        loan.setAmount(request.getLoanAmount());
        loan.setTermDays(request.getTermDays());
        loan.setCreated(new Timestamp(System.currentTimeMillis()));
        loan.setCountryIsoCode(countryCode);

//        try {
            loanRepository.save(loan);//for calls reduction in case of mysql fail logic will increase complexity
//        } catch (Exception e) {
            //risks to have n+1 payments instead of allowed n on rollback needs second state saving it will make logic a bit more complex I will skip this as this is not part of requirements.
//            repeatService.loanCreationError(countryCode);//not specified what to do in case loan is valid but there was error on db, for example connection timeout or deadlock or request refused permission denied or other possible mysql error
//            throw e;
//        }

        return loan;
    }
}
