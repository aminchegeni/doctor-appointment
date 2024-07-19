package com.blubank.doctorappointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().authenticated()
        ).httpBasic()/*.disable()*/.and()
                .csrf().disable()
                .cors().disable()
                .headers().frameOptions().disable();
        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public UserDetailsService buildInMemoryUserDetailsService() {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails user = users
                .username("regular")
                .password("regular")
                .authorities("GUEST")
                .build();
        UserDetails doctor = users
                .username("doctor")
                .password("doctor")
                .authorities("DOCTOR")
                .build();
        UserDetails patient = users
                .username("patient")
                .password("patient")
                .authorities("PATIENT")
                .build();
        UserDetails admin = users
                .username("admin")
                .password("admin")
                .authorities("GUEST", "DOCTOR", "PATIENT")
                .build();
        return new InMemoryUserDetailsManager(user, doctor, patient, admin);
    }
}
