package com.twino.homework.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.twino.homework.common.staticAccess.EntityGenerator;
import com.twino.homework.db.entity.LoanEntity;
import com.twino.homework.db.entity.UserEntity;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
public class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    LoanValidator loanValidator;
    @MockBean
    UserServiceImpl userService;
    @MockBean
    LoanServiceImpl loanService;

    @Test
    @WithMockUser("apiUser")
    public void addLoanTest() throws Exception {
        AddLoanRequest request = new AddLoanRequest((float) 33.12, 22, "test name", "test surname");
        ObjectMapper mapper = getObjectMapper();

        LoanEntity loanEntity = EntityGenerator.getTestLoanEntity();
        when(loanService.createLoan(request)).thenReturn(loanEntity);

        this.mockMvc.perform(
            put("/loan/add")
                .contentType(
                    new MediaType(
                        MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(),
                        StandardCharsets.UTF_8
                    )
                ).content(mapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(new LoanResponseData(loanEntity))));
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        return mapper;
    }

    @Test
    public void addLoanNoAccessTest() throws Exception {
        this.mockMvc.perform(
            put("/loan/add")
                .contentType(
                    new MediaType(
                        MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(),
                        StandardCharsets.UTF_8
                    )
                ).content("anything")
        )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    public static Object[][] dataProviderForAddLoanExceptionsTest() {
        return new Object[][]{
            {/* Exception */new InvalidRequestLoanException("test")},
            {/* Exception */new IOException("test")},
            {/* Exception */new UserBlacklistException("test")},
            {/* Exception */new LoanLimitReachedException("test")},

            //IOException, UserBlacklistException, LoanLimitReachedException
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForAddLoanExceptionsTest")
    @WithMockUser("apiUser")
    public void addLoanExceptionsTest(Exception exception) throws Exception {
        AddLoanRequest request = Mockito.mock(AddLoanRequest.class);
        if (exception instanceof InvalidRequestLoanException) {
            doThrow(exception).when(loanValidator).validate(request);
        } else {
            when(loanService.createLoan(request)).thenThrow(exception);
        }

        this.mockMvc.perform(
            put("/loan/add")
                .contentType(
                    new MediaType(
                        MediaType.APPLICATION_JSON.getType(),
                        MediaType.APPLICATION_JSON.getSubtype(),
                        StandardCharsets.UTF_8
                    )
                ).content("anything")
        )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    public static Object[][] dataProviderForAddBlacklistTest() {
        return new Object[][]{
            {/* Exception */null},
            {/* Exception */new UserNotFoundException("test")},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForAddBlacklistTest")
    @WithMockUser("apiUser")
    public void addBlacklistTest(Exception e) throws Exception {
        UserEntity userEntity = EntityGenerator.getTestUserEntity();
        UserResponseData userResponseData = new UserResponseData(userEntity);
        String uuidMock = "test uuid";

        if (e == null) {
            when(userService.addToBlacklistByUuid(uuidMock)).thenReturn(userEntity);
        } else {
            when(userService.addToBlacklistByUuid(uuidMock)).thenThrow(e);
        }

        ResultActions resultActions =
            this.mockMvc.perform(put(String.format("/blacklist/add/%s", uuidMock))).andDo(print());

        if (e == null) {
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(getObjectMapper().writeValueAsString(userResponseData)));
        } else {
            resultActions.andExpect(status().isUnprocessableEntity());
        }
    }
}
