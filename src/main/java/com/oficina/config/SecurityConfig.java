package com.oficina.config;

import com.oficina.model.Perfil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Quem pode acessar cada parte do sistema:
 * <ul>
 *   <li>/ (site), páginas de login e arquivos estáticos: todo mundo</li>
 *   <li>/admin/**: só administrador</li>
 *   <li>/recepcao/**: recepção e administrador</li>
 *   <li>/area-cliente/**: só o dono do carro</li>
 * </ul>
 * Equipe e clientes usam o mesmo processamento de login (POST /login); a página de login
 * do cliente envia o campo "origem=cliente" para que os erros voltem para ela.
 */
@Configuration
public class SecurityConfig {

    public static final String LOGIN_EQUIPE = "/login";
    public static final String LOGIN_CLIENTE = "/area-cliente/login";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers(LOGIN_EQUIPE, LOGIN_CLIENTE).permitAll()
                        .requestMatchers("/admin/**").hasRole(Perfil.ADMIN.name())
                        .requestMatchers("/recepcao/**").hasAnyRole(Perfil.ADMIN.name(), Perfil.RECEPCAO.name())
                        .requestMatchers("/area-cliente/**").hasRole(Perfil.CLIENTE.name())
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage(LOGIN_EQUIPE)
                        .loginProcessingUrl(LOGIN_EQUIPE)
                        .usernameParameter("login")
                        .passwordParameter("senha")
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect(request.getContextPath() + areaInicial(authentication)))
                        .failureHandler((request, response, exception) ->
                                response.sendRedirect(request.getContextPath() + paginaLogin(request) + "?erro"))
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String destino = authentication != null && temPerfil(authentication, Perfil.CLIENTE)
                                    ? LOGIN_CLIENTE : LOGIN_EQUIPE;
                            response.sendRedirect(request.getContextPath() + destino + "?saiu");
                        }))
                .exceptionHandling(ex -> ex
                        // Quem tenta abrir a área do cliente sem login vai para o login do cliente
                        .authenticationEntryPoint((request, response, authException) -> {
                            String uri = request.getRequestURI().substring(request.getContextPath().length());
                            String destino = uri.startsWith("/area-cliente") ? LOGIN_CLIENTE : LOGIN_EQUIPE;
                            response.sendRedirect(request.getContextPath() + destino);
                        }));
        return http.build();
    }

    /** Página para onde cada perfil vai depois de entrar. */
    public static String areaInicial(Authentication authentication) {
        if (temPerfil(authentication, Perfil.ADMIN)) {
            return "/admin";
        }
        if (temPerfil(authentication, Perfil.RECEPCAO)) {
            return "/recepcao";
        }
        return "/area-cliente";
    }

    private static boolean temPerfil(Authentication authentication, Perfil perfil) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> perfil.getAuthority().equals(a.getAuthority()));
    }

    private static String paginaLogin(HttpServletRequest request) {
        return "cliente".equals(request.getParameter("origem")) ? LOGIN_CLIENTE : LOGIN_EQUIPE;
    }
}
