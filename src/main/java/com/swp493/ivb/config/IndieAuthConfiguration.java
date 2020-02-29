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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@EnableWebSecurity
@Configuration

public class IndieAuthConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    KeyConfig keyConfig;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    IndieAuthenticationSucessHandler successHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Value("${spring.security.oauth2.resourceserver.indie.introspection-uri}") String introspectionUri;
	@Value("${spring.security.oauth2.resourceserver.indie.introspection-client-id}") String clientId;
	@Value("${spring.security.oauth2.resourceserver.indie.introspection-client-secret}") String clientSecret;

    @Bean
	public PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and().authorizeRequests()
            .antMatchers("/home","/login").permitAll()
            .antMatchers("/admin/**").hasAnyAuthority("admin")
            .anyRequest().authenticated()
            .and().oauth2Login().successHandler(successHandler);
    }

    

    

}