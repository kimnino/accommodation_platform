package com.accommodation.platform.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class MdcLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String requestId = resolveRequestId((HttpServletRequest) request, traceId);

        try {
            MDC.put("traceId", traceId);
            MDC.put("requestId", requestId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request, String fallback) {
        String xRequestId = request.getHeader("X-Request-ID");
        return (xRequestId != null && !xRequestId.isBlank()) ? xRequestId : fallback;
    }
}
