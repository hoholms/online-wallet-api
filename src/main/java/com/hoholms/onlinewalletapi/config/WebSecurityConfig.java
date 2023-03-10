package com.hoholms.onlinewalletapi.config;

import com.hoholms.onlinewalletapi.exception.MyAccessDeniedHandler;
import com.hoholms.onlinewalletapi.exception.MyAuthenticationEntryPoint;
import com.hoholms.onlinewalletapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@AllArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling().accessDeniedHandler(new MyAccessDeniedHandler()).authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .and()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/login**", "/logout**", "/register**", "/activate**").permitAll()
                        .requestMatchers("/users/**", "/categories/edit**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .rememberMe()
                .and()
                .logout()
                .logoutUrl("/login?logout")
                .logoutSuccessUrl("/login");

        return http.build();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    static class PWEncoder {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
