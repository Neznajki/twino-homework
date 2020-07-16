package com.twino.homework.external.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twino.homework.external.api.response.CountryByIpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CountryByIpGatherTest {
    @Mock
    CountryByIpGather countryByIpGather;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    Logger logger;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void getCountryCodeTest(Boolean successResponse) throws IOException {
        ReflectionTestUtils.setField(countryByIpGather, "objectMapper", objectMapper);
        URL urlMock = new URL("http://unit test url");

        CountryByIpResponse responseMock = new CountryByIpResponse();
        responseMock.countryCode = "test country code";
        String ipMock = "test ip";
        when(countryByIpGather.getCountryCode(ipMock)).thenCallRealMethod();
        when(countryByIpGather.getUrlForIp(ipMock)).thenReturn(urlMock);
        when(objectMapper.readValue(urlMock, CountryByIpResponse.class)).thenReturn(responseMock);
        when(countryByIpGather.isApiResponseCountryCodeValid(ipMock, responseMock)).thenReturn(successResponse);

        String response = countryByIpGather.getCountryCode(ipMock);

        if (successResponse) {
            assertEquals(responseMock.countryCode, response);
        } else {
            assertNull(response);
        }
    }

    public static Object[][] dataProviderForIsApiResponseCountryCodeValidTest() {
        return new Object[][]{
            {/* status */"success",/* countryCode */"LL", /* result */true},
            {/* status */"fail",/* countryCode */"LL", /* result */false},
            {/* status */"fail",/* countryCode */"Some Country", /* result */false},
            {/* status */"success",/* countryCode */"Some Country", /* result */false},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForIsApiResponseCountryCodeValidTest")
    public void isApiResponseCountryCodeValidTest(String status, String countryCode, boolean result) {
        ReflectionTestUtils.setField(countryByIpGather, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(countryByIpGather, "logger", logger);
        CountryByIpResponse response = new CountryByIpResponse();
        String ipMock = "test ip";

        response.status = status;
        response.countryCode = countryCode;

        when(countryByIpGather.isApiResponseCountryCodeValid(ipMock, response)).thenCallRealMethod();

        assertEquals(result, countryByIpGather.isApiResponseCountryCodeValid(ipMock, response));

        if (! result) {
            verify(logger).warn(anyString());
        }
    }

    @Test
    public void isApiResponseCountryCodeValidExceptionTest() throws JsonProcessingException {
        ReflectionTestUtils.setField(countryByIpGather, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(countryByIpGather, "logger", logger);
        CountryByIpResponse response = new CountryByIpResponse();
        String ipMock = "test ip";

        response.status = "test";
        response.countryCode = "gg";

        when(countryByIpGather.isApiResponseCountryCodeValid(ipMock, response)).thenCallRealMethod();
        when(objectMapper.writeValueAsString(response)).thenThrow(Mockito.mock(JsonParseException.class));

        assertFalse(countryByIpGather.isApiResponseCountryCodeValid(ipMock, response));

        verify(logger).error(
            String.format("error in response serialization for IP %s", ipMock)
        );
    }

    @Test
    public void getUrlForIpTest() throws MalformedURLException {
        String ipMock = "test ip";

        when(countryByIpGather.getUrlForIp(ipMock)).thenCallRealMethod();

        assertEquals(new URL(CountryByIpGather.HTTP_IP_API_COM_JSON.concat(ipMock)), countryByIpGather.getUrlForIp(ipMock));
    }
}
