package com.twino.homework.service;

import com.twino.homework.external.api.CountryByIpGather;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class CountryServiceImpl {
    public static final String DEFAULT_COUNTRY_CODE = "LV";
    CountryByIpGather countryByIpGather;
    RequestMetaDataGatherImpl requestMetaDataGather;
    HashMap<String, String> lazyCache = new HashMap<>();//could have some kind of cleanup

    public CountryServiceImpl(CountryByIpGather countryByIpGather, RequestMetaDataGatherImpl requestMetaDataGather) {
        this.countryByIpGather = countryByIpGather;
        this.requestMetaDataGather = requestMetaDataGather;

        lazyCache.put("127.0.0.1", DEFAULT_COUNTRY_CODE);
        lazyCache.put("0:0:0:0:0:0:0:1", DEFAULT_COUNTRY_CODE);
    }

    public String getCurrentRequestAgentId() throws IOException {
        String ipAddress = requestMetaDataGather.getClientIp();
        if (lazyCache.containsKey(ipAddress)) {
            return lazyCache.get(ipAddress);
        }

        String countryCode = countryByIpGather.getCountryCode(ipAddress);

        if (countryCode == null) {
            return DEFAULT_COUNTRY_CODE;// retry on fails
        }

        lazyCache.put(ipAddress, countryCode);
        return countryCode;
    }
}
