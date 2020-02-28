package com.swp493.ivb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@EnableWebSecurity
@Configuration

public class IndieAuthConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    KeyConfig keyConfig;

    @Autowired
    IndieAuthenticationSucessHandler successHandler;

    @Value("${spring.security.oauth2.resourceserver.indie.introspection-uri}") String introspectionUri;
	@Value("${spring.security.oauth2.resourceserver.indie.introspection-client-id}") String clientId;
	@Value("${spring.security.oauth2.resourceserver.indie.introspection-client-secret}") String clientSecret;

    @Bean
	public PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/home").permitAll()
            .antMatchers("/admin/**").hasAnyAuthority("admin")
            .anyRequest().permitAll()
            .and().oauth2Login().successHandler(successHandler)
            .and().oauth2ResourceServer()
            .opaqueToken()
            .introspectionUri(introspectionUri)
            .introspectionClientCredentials(clientId, clientSecret);
    }

    

    

}