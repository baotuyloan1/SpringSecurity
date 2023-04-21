package com.example.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebSecurityConfigurer {

  private static final String[] WHITE_LIST_URLS = {
    "/hello", "/register", "/resendVerifyToken", "/verfifyRegistration"
  };

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(11);
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers(WHITE_LIST_URLS)
        .permitAll();

    http.cors().and().csrf().disable().authorizeHttpRequests().anyRequest().permitAll();

    return http.build();
  }

  @Override
  public void init(SecurityBuilder builder) throws Exception {}

  @Override
  public void configure(SecurityBuilder builder) throws Exception {}
}
