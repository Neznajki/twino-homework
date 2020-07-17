package com.twino.homework.validator;

import com.twino.homework.exception.InvalidRequestLoanException;
import com.twino.homework.web.request.AddLoanRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoanValidatorTest {
    @Mock
    LoanValidator loanValidator;

    public static Object[][] dataProviderForValidateTest() {
        return new Object[][]{
            {new AddLoanRequest((float)1, 1, "test name", "test surname"), null},
            {new AddLoanRequest(null, null, null, null), "name is mandatory"},
            {new AddLoanRequest(null, null, "test  name", null), "surname is mandatory"},
            {new AddLoanRequest(null, null, "test  name", "test surname"), "loanAmount is mandatory"},
            {new AddLoanRequest((float)11.4, null, "test  name", "test surname"), "term is mandatory"},
            {new AddLoanRequest((float)11.4, 0, "test  name", "test surname"), "term should be 1 or more"},
            {new AddLoanRequest((float)11.4, -1, "test  name", "test surname"), "term should be 1 or more"},
            {new AddLoanRequest((float)0.5, 1, "test  name", "test surname"), "loanAmount should be 1 or more"},
            {new AddLoanRequest((float)-0.5, 1, "test  name", "test surname"), "loanAmount should be 1 or more"},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForValidateTest")
    public void validate(AddLoanRequest request, String message) throws InvalidRequestLoanException {
        doCallRealMethod().when(loanValidator).validate(request);
        doNothing().when(loanValidator).validateString("name", request.getName());
        doNothing().when(loanValidator).validateString("surname", request.getSurname());

        if (message != null) {
            try {
                loanValidator.validate(request);
            } catch (InvalidRequestLoanException e) {
                assertEquals(message, e.getMessage());
            }
//            assertThrows(InvalidRequestLoanException.class, () -> loanValidator.validate(request), message);//message is not validating
        } else {
            loanValidator.validate(request);

            verify(loanValidator, times(1)).validateString("name", request.getName());
            verify(loanValidator, times(1)).validateString("surname", request.getSurname());
        }
    }

    public static Object[][] dataProviderForValidateStringTest() {
        return new Object[][]{
            {"tt", "%s value should be 3 or more symbols (%s)"},
            {"very long test message aspdkasdkoapsodjkaopskdoakpsdop aksdopk asopdk aoksdopka opskdop askdop aksopdk opaskdop aksopd kaopsdk opas", "%s value should be less than 64 (%s)"},
            {"unsuported t 3", "%s must contain only english alphabetic symbols and spaces (%s)"},
            {"unsuported t_", "%s must contain only english alphabetic symbols and spaces (%s)"},
            {"unsuported-t", "%s must contain only english alphabetic symbols and spaces (%s)"},
            {"ābele", "%s must contain only english alphabetic symbols and spaces (%s)"},
            {"works", null},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForValidateStringTest")
    public void validateStringTest(String value, String unformattedMessage) throws InvalidRequestLoanException {
        String nameMock = "unit test name";
        doCallRealMethod().when(loanValidator).validateString(nameMock, value);

        try {
            loanValidator.validateString(nameMock, value);
        } catch (InvalidRequestLoanException e) {
            assertEquals(String.format(unformattedMessage, nameMock, value), e.getMessage());
        }
    }
}
