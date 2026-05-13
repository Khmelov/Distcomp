package com.distcomp.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    response.sendError(401, "Unauthorized")
                }
                ex.accessDeniedHandler { _, response, _ ->
                    response.sendError(403, "Forbidden")
                }
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/error").permitAll()
                auth.requestMatchers("/api/v1.0/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/v2.0/login").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/v2.0/users").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/v2.0/users").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v2.0/users").hasRole("ADMIN")
                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}