package com.twino.homework.service;

import com.twino.homework.helper.RequestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RequestMetaDataGatherImplTest {
    @Mock
    RequestMetaDataGatherImpl requestMetaDataGather;
    @Mock
    RequestHelper requestHelper;

    @Test
    public void headerConstTest() {
        assertArrayEquals(new String[]{
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        }, (String[])ReflectionTestUtils.getField(new RequestMetaDataGatherImpl(
            requestHelper
        ), "IP_HEADER_CANDIDATES"));
    }

    public static Object[][] dataProviderForGetClientIpTest() {
        return new Object[][]{
            {/* IP_HEADER_CANDIDATES */new String[]{"a","d","b","g"},/* remoteAddr */"LL",/* expectedResultCandidate */ "b"},
            {/* IP_HEADER_CANDIDATES */new String[]{"a","b"},/* remoteAddr */"LL",/* expectedResultCandidate */ "a"},
            {/* IP_HEADER_CANDIDATES */new String[]{"a","b"},/* remoteAddr */"LL",/* expectedResultCandidate */ null},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForGetClientIpTest")
    public void getClientIpTest(String[] IP_HEADER_CANDIDATES, String remoteAddr, String expectedResultCandidate) {
        String expectingResponse = "TT";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        if (expectedResultCandidate == null) {
            expectingResponse = remoteAddr;
        }

        ReflectionTestUtils.setField(requestMetaDataGather, "IP_HEADER_CANDIDATES", IP_HEADER_CANDIDATES);
        ReflectionTestUtils.setField(requestMetaDataGather, "requestHelper", requestHelper);

        when(requestMetaDataGather.getClientIp()).thenCallRealMethod();
        when(requestHelper.getCurrentRequest()).thenReturn(request);

        for (String header: IP_HEADER_CANDIDATES) {
            when(requestMetaDataGather.getHeaderIp(request, header)).thenReturn(header.equals(expectedResultCandidate) ? expectingResponse: null);
        }

        if (expectedResultCandidate == null) {
            when(request.getRemoteAddr()).thenReturn(remoteAddr);
        }

        String response = requestMetaDataGather.getClientIp();
        assertEquals(expectingResponse, response);

        if (expectedResultCandidate != null) {
            verify(request, never()).getRemoteAddr();
        }
    }

    public static Object[][] dataProviderForGetHeaderIpTest() {
        return new Object[][]{
            {/* name */"aa",/* value */"unknown",/* response */ null},
            {/* name */"aa",/* value */"",/* response */ null},
            {/* name */"aa",/* value */null,/* response */ null},
            {/* name */"aa",/* value */"127.0.0.1",/* response */ "127.0.0.1"},
            {/* name */"aa",/* value */"127.0.0.1,should not be handled",/* response */ "127.0.0.1"},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForGetHeaderIpTest")
    public void getHeaderIpTest(String name, String value, String response) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(requestMetaDataGather.getHeaderIp(request, name)).thenCallRealMethod();
        when(request.getHeader(name)).thenReturn(value);

        assertEquals(response, requestMetaDataGather.getHeaderIp(request, name));
    }
}
