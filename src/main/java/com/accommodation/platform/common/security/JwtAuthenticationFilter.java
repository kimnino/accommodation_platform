package com.accommodation.platform.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.parseToken(token);
            String role = jwtTokenProvider.getRole(claims);
            Long partnerId = jwtTokenProvider.getPartnerId(claims);
            Long memberId = jwtTokenProvider.getMemberId(claims);

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            UserContext userContext = new UserContext(memberId, role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userContext, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpServletRequest wrappedRequest = wrapRequestWithHeaders(request, role, partnerId, memberId);
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private HttpServletRequest wrapRequestWithHeaders(
            HttpServletRequest request,
            String role,
            Long partnerId,
            Long memberId
    ) {
        Map<String, String> additionalHeaders = new HashMap<>();

        if ("PARTNER".equals(role) && partnerId != null) {
            additionalHeaders.put("X-Partner-Id", String.valueOf(partnerId));
        }
        if ("CUSTOMER".equals(role) && memberId != null) {
            additionalHeaders.put("X-Member-Id", String.valueOf(memberId));
        }

        if (additionalHeaders.isEmpty()) {
            return request;
        }

        return new HeaderInjectingRequestWrapper(request, additionalHeaders);
    }

    private static class HeaderInjectingRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> additionalHeaders;

        HeaderInjectingRequestWrapper(HttpServletRequest request, Map<String, String> additionalHeaders) {
            super(request);
            this.additionalHeaders = additionalHeaders;
        }

        @Override
        public String getHeader(String name) {
            String value = additionalHeaders.get(name);
            if (value != null) {
                return value;
            }
            return super.getHeader(name);
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            String value = additionalHeaders.get(name);
            if (value != null) {
                return Collections.enumeration(Collections.singletonList(value));
            }
            return super.getHeaders(name);
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(additionalHeaders.keySet());
            return Collections.enumeration(names);
        }
    }
}
