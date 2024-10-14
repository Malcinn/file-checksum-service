package com.company.filechecksumservice.interfaces.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class FileSecurityConfiguration {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        http.cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((authorize) ->
                        authorize.pathMatchers("/").permitAll()
                                .pathMatchers("/h2-console").permitAll()
                                .anyExchange().authenticated()
                ).httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        UserDetails api_consumer = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("API_CONSUMER")
                .build();
        return new MapReactiveUserDetailsService(api_consumer);
    }
}
