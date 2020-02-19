// package com.indievibe.demo;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @EnableWebSecurity
// @Configuration

// public class Controller extends WebSecurityConfigurerAdapter {

//     @GetMapping("/get")
//     public Stuff getStuff(@AuthenticationPrincipal OAuth2User principle) {
//         return new Stuff((String) principle.getAttribute("name"), 0);
//     }

//     @Override
//     protected void configure(HttpSecurity http) throws Exception {
//         http
//             .authorizeRequests()
//             .anyRequest().authenticated()
//         .and()
//             .oauth2Login().defaultSuccessUrl("/get")
//         .and()
//             .logout().logoutSuccessUrl("/login");
//     }
// }
