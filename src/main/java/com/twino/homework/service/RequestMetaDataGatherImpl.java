package com.twino.homework.service;

import com.twino.homework.helper.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class RequestMetaDataGatherImpl {
    private final String[] IP_HEADER_CANDIDATES = {
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
    };
    RequestHelper requestHelper;

    @Autowired
    public RequestMetaDataGatherImpl(RequestHelper requestHelper) {
        this.requestHelper = requestHelper;
    }

    public String getClientIp() {
        HttpServletRequest request = requestHelper.getCurrentRequest();
        for (String header: IP_HEADER_CANDIDATES) {
            String foundIp = getHeaderIp(request, header);

            if (foundIp != null) {
                return foundIp;
            }
        }

        return request.getRemoteAddr();
    }

    protected String getHeaderIp(HttpServletRequest request, String header) {
        String ipList = request.getHeader(header);
        if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
            return ipList.split(",")[0];
        }

        return null;
    }
}
