package com.oficina.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**", "/painel").authenticated()
                        // Dados de clientes, veículos e ordens (inclui os códigos de acompanhamento) só com login
                        .requestMatchers("/api/clientes/**", "/api/veiculos/**", "/api/ordens-servico/**").authenticated()
                        // Alterar serviços pela API também exige login, senão o site poderia ser editado sem senha
                        .requestMatchers(HttpMethod.POST, "/api/servicos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/servicos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/servicos/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .httpBasic(Customizer.withDefaults())
                // A API REST é usada fora do navegador, sem token CSRF
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Usuário administrador definido em application.properties (oficina.admin.usuario / oficina.admin.senha)
    @Bean
    public UserDetailsService userDetailsService(@Value("${oficina.admin.usuario}") String usuario,
                                                 @Value("${oficina.admin.senha}") String senha,
                                                 PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(User.withUsername(usuario)
                .password(encoder.encode(senha))
                .roles("ADMIN")
                .build());
    }
}
