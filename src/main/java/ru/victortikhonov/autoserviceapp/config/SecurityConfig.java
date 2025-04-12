package ru.victortikhonov.autoserviceapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Цепочка фильтров безопасности
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Настройка разрешений на доступ к URL-адресам
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()

                        .requestMatchers("/request/list", "/request/details", "/work-order/details").hasAnyRole("OPERATOR", "MECHANIC", "ADMIN")
                        .requestMatchers("/work-order/list").hasAnyRole("MECHANIC", "ADMIN")

                        // Страницы только для механика
                        .requestMatchers("/work-order/**").hasRole("MECHANIC")

                        // Страницы только для оператора
                        .requestMatchers("/request/**").hasRole("OPERATOR")

                        .requestMatchers("/my-profile/**", "/service/list", "/auto-good/list")
                        .hasAnyRole("ADMIN", "OPERATOR", "MECHANIC")

                        // Страницы только для админастратора
                        .requestMatchers("/employee/**", "/service/**", "/auto-good/**", "/report/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                // Настройка формы входа
                .formLogin(form -> form
                        .loginPage("/login")
                        // Перенаправление после успешного входа
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/login-success");
                        })
                        .permitAll()
                )
                // Настройка выхода из системы
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        // Перенаправление после выхода
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error"));

        return http.build();
    }


    // Бин для кодирования паролей
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
