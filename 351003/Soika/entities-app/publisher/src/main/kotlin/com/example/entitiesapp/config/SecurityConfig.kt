package com.example.entitiesapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.Filter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.AuthenticationException

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(private val jwtRequestFilter: JwtRequestFilter) {

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1.0/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/v2.0/writers").permitAll()
                auth.requestMatchers("/api/v2.0/login").permitAll()

                auth.requestMatchers(HttpMethod.GET, "/api/v2.0/writers").hasRole("ADMIN")

                auth.requestMatchers("/api/v2.0/**").authenticated()
            }
            .addFilterBefore(jwtRequestFilter as Filter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { _, response, authException ->
                    response.status = 401
                    response.contentType = "application/json"
                    response.writer.write("{\"errorMessage\":\"${authException.message}\", \"errorCode\":40100}")
                }
                exceptions.accessDeniedHandler { _, response, accessDeniedException ->
                    response.status = 403
                    response.contentType = "application/json"
                    response.writer.write("{\"errorMessage\":\"${accessDeniedException.message}\", \"errorCode\":40300}")
                }
            }
        return http.build()
    }
}