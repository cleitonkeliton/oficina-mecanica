package com.oficina.security;

import com.oficina.config.SecurityConfig;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import com.oficina.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * A cada requisição, confere se o usuário logado ainda pode entrar. Se o administrador desativou
 * o usuário, redefiniu a senha dele, ou a recepção removeu o acesso do cliente, a sessão aberta
 * cai na hora (e não só quando a pessoa sair).
 */
public class SessaoValidaFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public SessaoValidaFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioAutenticado logado && !aindaValido(logado)) {
            logoutHandler.logout(request, response, auth);
            String login = logado.getPerfil() == Perfil.CLIENTE ? SecurityConfig.LOGIN_CLIENTE : SecurityConfig.LOGIN_EQUIPE;
            response.sendRedirect(request.getContextPath() + login + "?encerrada");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean aindaValido(UsuarioAutenticado logado) {
        Optional<Usuario> atual = usuarioRepository.findById(logado.getId());
        return atual.isPresent()
                && atual.get().isAtivo()
                && atual.get().getPerfil() == logado.getPerfil()
                && atual.get().getSenhaHash().equals(logado.getPassword());
    }

    /** Arquivos estáticos não precisam da conferência (evita consultas ao banco à toa). */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        return uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/img/") || uri.equals("/favicon.ico");
    }
}
