package com.accommodation.platform.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: integrate JWT authentication filter
    // Channel-based URL access control (to be enforced once JWT is wired):
    //   /api/v1/admin/**       → ADMIN role required
    //   /api/v1/extranet/**    → PARTNER role required
    //   /api/v1/reservations/**→ CUSTOMER role required
    //   /api/v1/accommodations/**→ permitAll (public search)
    //   /api/v1/supplier/**    → ADMIN role required

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
