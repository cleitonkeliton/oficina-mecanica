package com.oficina.controller;

import com.oficina.form.SenhaForm;
import com.oficina.model.Usuario;
import com.oficina.security.UsuarioAutenticado;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Troca de senha, para qualquer pessoa logada. */
@Controller
@RequestMapping("/conta")
public class ContaController {

    private final UsuarioService usuarioService;
    private final SecurityContextRepository contextoRepository = new HttpSessionSecurityContextRepository();

    public ContaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/senha")
    public String formulario(Model model) {
        model.addAttribute("senhaForm", new SenhaForm());
        return "conta/senha";
    }

    @PostMapping("/senha")
    public String alterar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                          @Valid @ModelAttribute("senhaForm") SenhaForm form, BindingResult resultado,
                          Model model, RedirectAttributes redirect,
                          HttpServletRequest request, HttpServletResponse response) {
        if (resultado.hasErrors()) {
            return "conta/senha";
        }
        Usuario atualizado;
        try {
            atualizado = usuarioService.alterarPropriaSenha(usuario.getId(), form.getSenhaAtual(), form.getNovaSenha(), form.getConfirmacao());
        } catch (RegraNegocioException e) {
            Formularios.mostrarErro(e, resultado, model);
            return "conta/senha";
        }
        manterLogado(atualizado, request, response);
        redirect.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        return "redirect:/conta/senha";
    }

    /**
     * A sessão guarda a senha criptografada para perceber quando ela é trocada por outra pessoa
     * (ver SessaoValidaFilter). Quando é a própria pessoa que troca, atualizamos a sessão para ela
     * continuar logada.
     */
    private void manterLogado(Usuario usuario, HttpServletRequest request, HttpServletResponse response) {
        UsuarioAutenticado novo = new UsuarioAutenticado(usuario);
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(novo, null, novo.getAuthorities()));
        SecurityContextHolder.setContext(contexto);
        contextoRepository.saveContext(contexto, request, response);
    }
}
