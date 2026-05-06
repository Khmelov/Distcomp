package com.example.distcomp.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val creatorUserDetailsService: CreatorUserDetailsService,
    private val jsonAuthenticationEntryPoint: JsonAuthenticationEntryPoint,
    private val jsonAccessDeniedHandler: JsonAccessDeniedHandler
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(passwordEncoder: PasswordEncoder): AuthenticationProvider =
        DaoAuthenticationProvider().apply {
            setUserDetailsService(creatorUserDetailsService)
            setPasswordEncoder(passwordEncoder)
        }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationProvider: AuthenticationProvider
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint(jsonAuthenticationEntryPoint)
                it.accessDeniedHandler(jsonAccessDeniedHandler)
            }
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1.0/**").permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/v2.0/creators", "/api/v2.0/login").permitAll()
                it.requestMatchers("/api/v2.0/**").authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
