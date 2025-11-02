package com.techtorque.time_logging_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    // A more comprehensive whitelist for Swagger/OpenAPI, actuator, and public endpoints
    private static final String[] PUBLIC_WHITELIST = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/webjars/**",
        "/api-docs/**",
        "/actuator/**",
        "/health",
        "/favicon.ico",
        "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection for stateless APIs
            .csrf(csrf -> csrf.disable())

            // Set session management to STATELESS
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Explicitly disable form login and HTTP Basic authentication
            .formLogin(formLogin -> formLogin.disable())
            .httpBasic(httpBasic -> httpBasic.disable());

        // Configure authorization rules based on security setting
        if (securityEnabled) {
            // Production mode: require authentication except for public paths
            http.authorizeHttpRequests(authz -> authz
                .requestMatchers(PUBLIC_WHITELIST).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new GatewayHeaderFilter(), UsernamePasswordAuthenticationFilter.class);
        } else {
            // Development mode: allow all requests
            http.authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        }

        return http.build();
    }
}