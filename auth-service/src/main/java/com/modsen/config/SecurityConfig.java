package com.modsen.config;

import com.modsen.controller.filter.ExceptionFilter;
import com.modsen.controller.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public static final String API_V_0_AUTH = "/api/v0/auth/**";
  private final JwtFilter jwtFilter;
  private final ExceptionFilter exceptionFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            handling ->
                handling.authenticationEntryPoint(
                    (req, resp, exception) -> exceptionFilter.sendException(resp, exception)))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(API_V_0_AUTH).permitAll().anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(exceptionFilter, JwtFilter.class)
        .build();
  }
}
