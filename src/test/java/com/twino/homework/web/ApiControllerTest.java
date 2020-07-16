package com.twino.homework.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twino.homework.exception.InvalidRequestLoanException;
import com.twino.homework.exception.LoanLimitReachedException;
import com.twino.homework.exception.UserBlacklistException;
import com.twino.homework.service.LoanServiceImpl;
import com.twino.homework.service.UserServiceImpl;
import com.twino.homework.validator.LoanValidator;
import com.twino.homework.web.request.AddLoanRequest;
import com.twino.homework.web.response.LoanResponseData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
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
    public void addLoanTest() throws Exception {
        AddLoanRequest request = new AddLoanRequest((float) 33.12, 22, "test name", "test surname");
        ObjectMapper mapper = new ObjectMapper();


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
            .andDo(print()).andExpect(status().isOk());
//            .andExpect(forwardedUrl("/WEB-INF/jsp/registration.jsp"))
//            .andExpect(model().attribute("userForm", equalTo(new User())));
    }
}
