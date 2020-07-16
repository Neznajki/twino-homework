package com.twino.homework.helper;

import com.twino.homework.external.api.CountryByIpGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestHelper {
    Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    public HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes)requestAttributes).getRequest();
        }

        logger.error("could not find request");

        throw new RuntimeException("this is not http request !!");
    }
}
