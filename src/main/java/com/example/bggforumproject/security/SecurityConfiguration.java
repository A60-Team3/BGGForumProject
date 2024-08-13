package com.example.bggforumproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.
                            requestMatchers("/auth/login", "/auth/login-error", "/auth/register", "/BGGForum/main", "/")
                            .permitAll()
                            .requestMatchers("/BGGForum/posts/most-commented", "/BGGForum/posts/most-recently-created")
                            .permitAll()
                            .requestMatchers("/resources/**", "/static/**", "/static/templates/**",
                                    "/css/**", "/images/**","/js/**")
                            .permitAll();
//                    auth.requestMatchers("/BGGForum/posts/tags/**").hasAnyRole("ADMIN", "MODERATOR");
//                    auth.requestMatchers("/BGGForum/users").hasAnyRole("ADMIN", "MODERATOR");
//                    auth.requestMatchers("/BGGForum/admin/**").hasAnyRole("ADMIN", "MODERATOR");
//                    auth.requestMatchers("/BGGForum/users/**").hasAnyRole("ADMIN", "MODERATOR", "USER");
//                    auth.requestMatchers("/BGGForum/admin/admin/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin ->
                        formLogin.loginPage("/auth/login")
                                .defaultSuccessUrl("/BGGForum/main", true)
                                .failureForwardUrl("/auth/login-error")

                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/BGGForum/main")
                );

        return http.build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**", "/images/**", "/js/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/images/", "classpath:/static/js/");
    }
}
