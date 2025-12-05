package com.coursework.jsontool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Додаємо сюди всі наші HTML сторінки, щоб браузер міг їх завантажити
                        // А вже JS всередині них перевірить, чи ми залогінені
                        .requestMatchers("/", "/index.html", "/dashboard.html", "/editor.html", "/admin.html",
                                "/js/**", "/css/**", "/api/auth/**").permitAll()

                        .anyRequest().authenticated()
                )
                // ОСЬ ТУТ ЗМІНИ:
                .httpBasic(basic -> basic
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Коли юзер не авторизований, ми просто повертаємо код 401.
                            // Ми НЕ додаємо заголовок "WWW-Authenticate", тому вікно не вилізе.
                            response.setStatus(401); // або просто 401
                            response.getWriter().write("Unauthorized");
                        })
                );

        return http.build();
    }

}