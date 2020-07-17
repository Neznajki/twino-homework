package com.twino.homework.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RequestHelperTest {
    @Mock
    RequestHelper requestHelper;

    public static Object[][] dataProviderForGetCurrentRequestTest() {
        ServletRequestAttributes validAttribute = Mockito.mock(ServletRequestAttributes.class);
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        ReflectionTestUtils.setField(validAttribute, "request", requestMock);

        return new Object[][]{
            {validAttribute, requestMock},
            {Mockito.mock(RequestAttributes.class), null},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForGetCurrentRequestTest")
    public void getCurrentRequestTest(RequestAttributes attributesMock, HttpServletRequest requestMock) {
        when(requestHelper.getCurrentRequest()).thenCallRealMethod();
        when(requestHelper.getRequestAttribute()).thenReturn(attributesMock);

        if (requestMock == null) {
            Logger logger = Mockito.mock(Logger.class);
            ReflectionTestUtils.setField(requestHelper, "logger", logger);

            try {
                requestHelper.getCurrentRequest();
            } catch (RuntimeException e) {
                verify(logger, times(1)).error("could not find request");
                assertEquals("this is not http request !!", e.getMessage());
            }
        } else {
            assertEquals(requestMock, requestHelper.getCurrentRequest());
        }
    }

    @Test
    public void getRequestAttributeTest() {
        when(requestHelper.getRequestAttribute()).thenCallRealMethod();

        assertEquals(RequestContextHolder.getRequestAttributes(), requestHelper.getRequestAttribute());
    }
}
