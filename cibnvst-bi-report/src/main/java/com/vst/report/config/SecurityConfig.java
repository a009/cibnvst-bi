package com.vst.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(c -> c
                        .antMatchers("/jmreport/view/742916404839010304").hasAnyRole("VIEWER", "ADMIN")
                )
                .formLogin()
                .successForwardUrl("/jmreport/view/742916404839010304")
                .and()
                .csrf().disable()
                .build();
    }
}
