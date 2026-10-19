package com.oficina.controller;

import com.oficina.config.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /** Login da equipe (administrador e recepção). */
    @GetMapping(SecurityConfig.LOGIN_EQUIPE)
    public String loginEquipe(Authentication authentication) {
        if (logado(authentication)) {
            return "redirect:" + SecurityConfig.areaInicial(authentication);
        }
        return "login";
    }

    /** Login do dono do carro para acompanhar a situação. */
    @GetMapping(SecurityConfig.LOGIN_CLIENTE)
    public String loginCliente(Authentication authentication) {
        if (logado(authentication)) {
            return "redirect:" + SecurityConfig.areaInicial(authentication);
        }
        return "area-cliente/login";
    }

    private static boolean logado(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
