package com.swp493.ivb.config;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;



@EnableWebSecurity
@Configuration

public class IndieAuthConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    KeyConfig keyConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    IndieAuthenticationSucessHandler successHandler;

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(PasswordEncoder());
        return provider;
    }

    @Bean 
    public PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/home").permitAll()
            .antMatchers("/admin/**").hasAnyAuthority("admin")
            .anyRequest().permitAll()
            .and().oauth2Login().successHandler(successHandler)
            .and().formLogin().defaultSuccessUrl("/getUser")
            .and().httpBasic()
            .and().oauth2ResourceServer().jwt()
            .decoder(NimbusJwtDecoder.withPublicKey((RSAPublicKey)keyConfig.keyPair().getPublic()).build());
    }

    

}