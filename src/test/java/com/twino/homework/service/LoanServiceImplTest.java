package com.twino.homework.service;

import com.twino.homework.common.DateMatcher;
import com.twino.homework.common.staticAccess.EntityGenerator;
import com.twino.homework.db.entity.LoanEntity;
import com.twino.homework.db.entity.UserEntity;
import com.twino.homework.db.repository.LoanRepository;
import com.twino.homework.exception.LoanLimitReachedException;
import com.twino.homework.exception.UserBlacklistException;
import com.twino.homework.web.request.AddLoanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoanServiceImplTest {
    @Mock
    LoanServiceImpl loanService;
    @Mock
    UserServiceImpl userService;
    @Mock
    LoanRepository loanRepository;
    @Mock
    CountryServiceImpl countryService;
    @Mock
    RepeatServiceImpl repeatService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(loanService, "userService", userService);
        ReflectionTestUtils.setField(loanService, "loanRepository", loanRepository);
        ReflectionTestUtils.setField(loanService, "countryService", countryService);
        ReflectionTestUtils.setField(loanService, "repeatService", repeatService);
    }


    public static Object[][] dataProviderForTestCreateLoanTest() {
        return new Object[][]{
            {new AddLoanRequest((float)-0.5, 1, "test  name", "test surname"), true, "GB", true},
            {new AddLoanRequest((float)-0.5, 1, "test  name", "test surname"), true, "GB", false},
            {new AddLoanRequest((float)-0.5, 1, "test  name", "test surname"), false, "GB", true},
            {new AddLoanRequest((float)-0.5, 1, "test  name", "test surname"), false, "GB", false},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForTestCreateLoanTest")
    public void createLoanTest(
        AddLoanRequest request,
        Boolean isBlacklist,
        String countryCode,
        Boolean allowedDueLimitation
    ) throws UserBlacklistException, LoanLimitReachedException, IOException {
        when(loanService.createLoan(request)).thenCallRealMethod();
        DateMatcher dateMatcher = new DateMatcher();
        UserEntity userMock = EntityGenerator.getTestUserEntity();

        when(userService.getUserEntity(request.getName(), request.getSurname())).thenReturn(userMock);
        when(userService.isUserBlacklisted(userMock)).thenReturn(isBlacklist);

        if (isBlacklist) {
            testBlacklistPart(request, userMock);
            return;
        }

        when(repeatService.isRepeatRequestAllowed(countryCode)).thenReturn(allowedDueLimitation);
        when(countryService.getCurrentRequestAgentId()).thenReturn(countryCode);

        if (! allowedDueLimitation) {
            limitReachedTest(request, countryCode);
            return;
        }

        LoanEntity savedLoan = loanService.createLoan(request);

        verify(loanRepository, times(1)).save(savedLoan);

        dateMatcher.wasNow(savedLoan.getCreated());
        assertEquals(savedLoan.getUserByUserId(), userMock);
        assertEquals(savedLoan.getAmount(), (double)request.getLoanAmount());
        assertEquals(savedLoan.getTermDays(), request.getTermDays());
        assertEquals(savedLoan.getCountryIsoCode(), countryCode);
    }

    protected void limitReachedTest(
        AddLoanRequest request,
        String countryCode
    ) throws IOException, UserBlacklistException {
        try {
            loanService.createLoan(request);
        } catch (LoanLimitReachedException e) {
            assertEquals(String.format(
                "loan limit reached for %s max allowed limit is %d in %d seconds",
                countryCode,
                RepeatServiceImpl.maxRepeats,
                RepeatServiceImpl.intervalSeconds
            ), e.getMessage());
        }

        verify(loanRepository, never()).save(any());
    }

    protected void testBlacklistPart(
        AddLoanRequest request,
        UserEntity userMock
    ) throws IOException, LoanLimitReachedException {
        try {
            loanService.createLoan(request);
        } catch (UserBlacklistException e) {
            assertEquals(
                String.format(
                    "user %s %s with uuid %s found in blacklist",
                    userMock.getName(),
                    userMock.getSurname(),
                    userMock.getUniqueId()
                ), e.getMessage()
            );
        }

        verify(loanRepository, never()).save(any());
    }
}
