package com.twino.homework.service;

import com.twino.homework.external.api.CountryByIpGather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CountryServiceImplTest {
    public static final String DEFAULT_COUNTRY = "LV";
    @Mock
    CountryServiceImpl countryService;
    @Mock
    CountryByIpGather countryByIpGather;
    @Mock
    RequestMetaDataGatherImpl requestMetaDataGather;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(countryService, "countryByIpGather", countryByIpGather);
        ReflectionTestUtils.setField(countryService, "requestMetaDataGather", requestMetaDataGather);
    }

    @Test
    public void testDefaults() {
        assertEquals(DEFAULT_COUNTRY, CountryServiceImpl.DEFAULT_COUNTRY_CODE);

        CountryServiceImpl instance = new CountryServiceImpl(countryByIpGather, requestMetaDataGather);

        assertThat(instance.lazyCache)
            .hasSize(2)
            .containsEntry("127.0.0.1", DEFAULT_COUNTRY)
            .containsEntry("0:0:0:0:0:0:0:1", DEFAULT_COUNTRY);
    }

    public static Object[][] dataProviderForTestGetCurrentRequestAgentId() {
        return new Object[][]{
            {"ip mock", "result mock", true, false},
            {"ip mock", "result mock", false, true},
            {"ip mock", DEFAULT_COUNTRY, false, false},
        };
    }

    @ParameterizedTest
    @MethodSource(value = "dataProviderForTestGetCurrentRequestAgentId")
    public void testGetCurrentRequestAgentId(String ipMock, String result, Boolean lazyCache, Boolean countryFound) throws IOException {
        when(countryService.getCurrentRequestAgentId()).thenCallRealMethod();
        //noinspection unchecked
        countryService.lazyCache = Mockito.mock(HashMap.class);
        when(requestMetaDataGather.getClientIp()).thenReturn(ipMock);
        when(countryService.lazyCache.containsKey(ipMock)).thenReturn(lazyCache);

        if (lazyCache) {
            when(countryService.lazyCache.get(ipMock)).thenReturn(result);
        } else if (countryFound) {
            when(countryByIpGather.getCountryCode(ipMock)).thenReturn(result);
        } else {
            when(countryByIpGather.getCountryCode(ipMock)).thenReturn(null);
        }

        assertEquals(result, countryService.getCurrentRequestAgentId());

        //noinspection ResultOfMethodCallIgnored
        verify(countryService.lazyCache, times(1)).containsKey(ipMock);

        if (lazyCache) {
            verify(countryService.lazyCache, times(1)).get(ipMock);
            verify(countryService.lazyCache, never()).put(anyString(), anyString());
            verify(countryByIpGather, never()).getCountryCode(anyString());
        } else if (countryFound) {
            verify(countryService.lazyCache, never()).get(ipMock);
            verify(countryService.lazyCache, times(1)).put(ipMock, result);
            verify(countryByIpGather, times(1)).getCountryCode(ipMock);
        } else {
            verify(countryService.lazyCache, never()).get(ipMock);
            verify(countryService.lazyCache, never()).put(anyString(), anyString());
            verify(countryByIpGather, times(1)).getCountryCode(ipMock);
        }
    }
}
