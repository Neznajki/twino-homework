package com.twino.homework.external.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twino.homework.external.api.response.CountryByIpResponse;
import com.twino.homework.service.RequestMetaDataGatherImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class CountryByIpGather {
    public static final String HTTP_IP_API_COM_JSON = "http://ip-api.com/json/";
    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(CountryByIpGather.class);

    @Autowired
    public CountryByIpGather(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** see no reasons on increase complexity to logic by adding batch requests */
    public String getCountryCode(String ipAddress) throws IOException {
        URL url = getUrlForIp(ipAddress);
        CountryByIpResponse result = objectMapper.readValue(url, CountryByIpResponse.class);

        if (isApiResponseCountryCodeValid(ipAddress, result)) {
            return result.countryCode;
        }

        return null;
    }

    protected boolean isApiResponseCountryCodeValid(String ipAddress, CountryByIpResponse result) {
        try {
            if (result.status.equals("success")) {
                if (result.countryCode.matches("[A-Za-z]+")) {
                    return true;
                }

                logger.warn(String.format("something went wrong and country code is not valid for IP %s with response %s", ipAddress, objectMapper.writeValueAsString(result)));

                return false;
            }

            logger.warn(String.format("something went wrong and country code could not be detected for IP %s with response %s", ipAddress, objectMapper.writeValueAsString(result)));
        } catch (JsonProcessingException e) {
            logger.error(String.format("error in response serialization for IP %s", ipAddress));
        }

        return false;
    }

    protected URL getUrlForIp(String ipAddress) throws MalformedURLException {
        return new URL(HTTP_IP_API_COM_JSON.concat(ipAddress));
    }
}

