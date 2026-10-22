package com.oficina.controller;

import com.oficina.form.UsuarioForm;
import com.oficina.model.Perfil;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Usuários da equipe: administradores e recepcionistas. */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("perfis")
    public Perfil[] perfis() {
        return new Perfil[]{Perfil.RECEPCAO, Perfil.ADMIN};
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarEquipe());
        model.addAttribute("usuarioForm", new UsuarioForm());
        return "admin/usuarios";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("usuarioForm") UsuarioForm form, BindingResult resultado,
                        Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                usuarioService.criarEquipe(form);
                redirect.addFlashAttribute("sucesso", "Usuário \"" + form.getLogin().trim().toLowerCase() + "\" criado.");
                return "redirect:/admin/usuarios";
            } catch (RegraNegocioException e) {
                Formularios.mostrarErro(e, resultado, model);
            }
        }
        model.addAttribute("usuarios", usuarioService.listarEquipe());
        return "admin/usuarios";
    }

    @PostMapping("/{id}/ativo")
    public String alternarAtivo(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado logado,
                                RedirectAttributes redirect) {
        try {
            usuarioService.alternarAtivo(id, logado.getId());
            redirect.addFlashAttribute("sucesso", "Usuário atualizado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/senha")
    public String redefinirSenha(@PathVariable Long id, @RequestParam String novaSenha, RedirectAttributes redirect) {
        try {
            usuarioService.redefinirSenha(id, novaSenha);
            redirect.addFlashAttribute("sucesso", "Senha redefinida.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
