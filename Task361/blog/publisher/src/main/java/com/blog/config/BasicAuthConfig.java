package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("basic-auth") // Включаем только при активном профиле basic-auth
public class BasicAuthConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        // Публичные endpoints для v1.0
                        .requestMatchers("/api/v1.0/**").permitAll()

                        // Базовые настройки для v2.0
                        .requestMatchers("/api/v2.0/editors").permitAll()
                        .requestMatchers("/api/v2.0/login").permitAll()

                        // Разделяем доступ по ролям
                        .requestMatchers("/api/v2.0/test/admin").hasRole("ADMIN")
                        .requestMatchers("/api/v2.0/test/customer").hasRole("CUSTOMER")
                        .requestMatchers("/api/v2.0/test/protected").hasAnyRole("ADMIN", "CUSTOMER")
                        .requestMatchers("/api/v2.0/test/public").permitAll()

                        // Для остальных v2.0 endpoints требуется аутентификация
                        .requestMatchers("/api/v2.0/**").authenticated()

                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> {}); // Включаем базовую аутентификацию

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails customer = User.builder()
                .username("customer")
                .password(encoder.encode("customer123"))
                .roles("CUSTOMER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user123"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, customer, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}