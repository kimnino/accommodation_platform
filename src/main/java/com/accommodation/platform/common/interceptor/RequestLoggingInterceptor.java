package com.accommodation.platform.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String ATTR_START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTR_START_TIME, System.currentTimeMillis());
        log.info("[{}] {} {}", MDC.get("traceId"), request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(ATTR_START_TIME);
        long elapsed = startTime != null ? System.currentTimeMillis() - startTime : -1;
        log.info("[{}] {} {} → {}ms status={}",
                MDC.get("traceId"),
                request.getMethod(),
                request.getRequestURI(),
                elapsed,
                response.getStatus());
    }
}
