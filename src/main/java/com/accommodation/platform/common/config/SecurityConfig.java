package com.accommodation.platform.common.config;

import com.accommodation.platform.common.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/accommodations/**").permitAll()
                .requestMatchers("/api/v1/customer/accommodations/**").permitAll()
                .requestMatchers("/api/v1/tags/**").permitAll()
                .requestMatchers("/api/v1/regions/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/extranet/**").hasRole("PARTNER")
                .requestMatchers("/api/v1/customer/reservations/**").hasRole("CUSTOMER")
                .requestMatchers("/api/v1/reservations/**").hasRole("CUSTOMER")
                .requestMatchers("/api/v1/supplier/**").hasRole("ADMIN")
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/static/**", "/*.html").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"status\":\"ERROR\",\"data\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"field_errors\":null},\"timestamp\":\"" + Instant.now() + "\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"status\":\"ERROR\",\"data\":null,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\",\"field_errors\":null},\"timestamp\":\"" + Instant.now() + "\"}"
                    );
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
