package com.adashkevich.rest.lab.security;

import com.adashkevich.rest.lab.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1.0/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v2.0/login", "/api/v2.0/editors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2.0/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .requestMatchers("/api/v2.0/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> writeError(response, 401, "Authentication required", "40100"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> writeError(response, 403, "Access denied", "40300"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void writeError(HttpServletResponse response, int status, String message, String code) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        OBJECT_MAPPER.writeValue(response.getWriter(), new ErrorResponse(message, code));
    }
}
