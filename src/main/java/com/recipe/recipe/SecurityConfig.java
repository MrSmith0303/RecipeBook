package com.recipe.recipe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/auth/recipes/**").permitAll() // Engedélyezzük a /auth/recipes végpontokat
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/index.html") // Opcionális: saját login oldal megadása
                .permitAll());
        return http.build();
    }
}