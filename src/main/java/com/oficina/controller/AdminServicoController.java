package com.oficina.controller;

import com.oficina.model.Servico;
import com.oficina.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Serviços e preços exibidos na tela principal. */
@Controller
@RequestMapping("/admin/servicos")
public class AdminServicoController {

    private final ServicoService servicoService;

    public AdminServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicos", servicoService.listarTodos());
        return "admin/servicos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new Servico());
        return "admin/servico-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("servico", servicoService.buscar(id));
        return "admin/servico-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("servico") Servico servico, BindingResult resultado,
                         RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return "admin/servico-form";
        }
        Servico salvo = servicoService.salvar(servico);
        redirect.addFlashAttribute("sucesso", "Serviço \"" + salvo.getNome() + "\" salvo.");
        return "redirect:/admin/servicos";
    }

    @PostMapping("/{id}/ativo")
    public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
        servicoService.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Visibilidade do serviço atualizada.");
        return "redirect:/admin/servicos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        if (servicoService.excluir(id)) {
            redirect.addFlashAttribute("sucesso", "Serviço excluído.");
        } else {
            redirect.addFlashAttribute("sucesso",
                    "Este serviço já foi usado em ordens de serviço, então foi apenas desativado (não aparece mais no site).");
        }
        return "redirect:/admin/servicos";
    }
}
