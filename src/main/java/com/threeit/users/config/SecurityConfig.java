package com.threeit.users.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the Spring Security filter chain for the application. This configuration includes:
     * - Permitting access to specific public endpoints such as login, sign-up, Swagger documentation, H2 console, and actuator endpoints.
     * - Requiring authentication for all other endpoints.
     * - Disabling Cross-Site Request Forgery (CSRF) protection.
     * - Configuring headers to allow frames from the same origin (useful for H2 console).
     * - Adding a custom JWT authentication filter for processing JWT tokens before the standard authentication filter.
     *
     * @param http the HttpSecurity object used to configure security settings for HTTP requests
     * @return a configured {@code SecurityFilterChain} instance
     * @throws Exception if there's an issue in configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    /**
     * Provides a BCryptPasswordEncoder bean to be used for password encoding in the application.
     *
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
