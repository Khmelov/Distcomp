package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // ИЗМЕНЕНИЕ: Включаем сессии для поддержки stateful тестов
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Всегда создавать сессии
                        .sessionFixation().migrateSession() // Защита от фиксации сессии
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            String errorMessage = "Authentication required";

                            String authHeader = request.getHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                errorMessage = "JWT token is invalid or expired";
                            }

                            response.getWriter().write(String.format(
                                    "{\"errorCode\":40101,\"errorMessage\":\"%s\",\"timestamp\":\"%s\"}",
                                    errorMessage,
                                    java.time.LocalDateTime.now()
                            ));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(String.format(
                                    "{\"errorCode\":40301,\"errorMessage\":\"Access denied: Insufficient privileges\",\"timestamp\":\"%s\"}",
                                    java.time.LocalDateTime.now()
                            ));
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Публичные endpoints v1.0
                        .requestMatchers("/api/v1.0/**").permitAll()

                        // Публичные endpoints v2.0 (регистрация и логин)
                        .requestMatchers(HttpMethod.POST, "/api/v2.0/editors").permitAll()
                        .requestMatchers("/api/v2.0/login").permitAll()

                        // Test endpoints
                        .requestMatchers("/api/v2.0/test/**").permitAll()

                        // Информационные endpoints
                        .requestMatchers("/", "/api/**").permitAll()

                        // Все остальные v2.0 endpoints требуют аутентификации
                        .requestMatchers("/api/v2.0/**").authenticated()

                        .anyRequest().permitAll()
                )
                // ИЗМЕНЕНИЕ: Добавляем formLogin для поддержки сессионной аутентификации
                .formLogin(form -> form
                        .loginProcessingUrl("/api/v2.0/session-login") // Отдельный endpoint для сессионного логина
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // Успешный логин - сессия создается автоматически
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(String.format(
                                    "{\"status\": \"authenticated\", \"username\": \"%s\", \"sessionId\": \"%s\"}",
                                    authentication.getName(),
                                    request.getSession().getId()
                            ));
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(String.format(
                                    "{\"errorCode\":40102,\"errorMessage\":\"Login failed: %s\",\"timestamp\":\"%s\"}",
                                    exception.getMessage(),
                                    java.time.LocalDateTime.now()
                            ));
                        })
                        .permitAll()
                )
                // ИЗМЕНЕНИЕ: Добавляем logout для сессий
                .logout(logout -> logout
                        .logoutUrl("/api/v2.0/session-logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\": \"logged out\"}");
                        })
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}