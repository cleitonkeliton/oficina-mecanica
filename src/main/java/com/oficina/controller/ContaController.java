package com.oficina.controller;

import com.oficina.form.SenhaForm;
import com.oficina.security.UsuarioAutenticado;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                          Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return "conta/senha";
        }
        try {
            usuarioService.alterarPropriaSenha(usuario.getId(), form.getSenhaAtual(), form.getNovaSenha(), form.getConfirmacao());
        } catch (RegraNegocioException e) {
            Formularios.mostrarErro(e, resultado, model);
            return "conta/senha";
        }
        redirect.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        return "redirect:/conta/senha";
    }
}
