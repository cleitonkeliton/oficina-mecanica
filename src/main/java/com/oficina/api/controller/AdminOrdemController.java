package com.oficina.api.controller;

import com.oficina.api.dto.OrdemForm;
import com.oficina.api.model.OrdemServico;
import com.oficina.api.service.OrdemServicoService;
import com.oficina.api.service.ServicoService;
import com.oficina.api.service.VeiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Área administrativa: ordens de serviço que o cliente acompanha na área do cliente
@Controller
@RequestMapping("/admin/ordens")
public class AdminOrdemController {

    private final OrdemServicoService ordemService;
    private final ServicoService servicoService;
    private final VeiculoService veiculoService;

    public AdminOrdemController(OrdemServicoService ordemService, ServicoService servicoService,
                                VeiculoService veiculoService) {
        this.ordemService = ordemService;
        this.servicoService = servicoService;
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("ordem")) {
            model.addAttribute("ordem", new OrdemForm());
        }
        model.addAttribute("ordens", ordemService.listarRecentes());
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("veiculos", veiculoService.listarTodos());
        model.addAttribute("statusOpcoes", OrdemServico.STATUS);
        return "admin-ordens";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, RedirectAttributes redirect) {
        return ordemService.buscarPorId(id)
                .map(o -> {
                    redirect.addFlashAttribute("ordem", OrdemForm.de(o));
                    return "redirect:/admin/ordens#form-ordem";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("erro", "Ordem de serviço não encontrada.");
                    return "redirect:/admin/ordens";
                });
    }

    @PostMapping
    public String salvar(@ModelAttribute("ordem") OrdemForm form, RedirectAttributes redirect) {
        if (form.getVeiculoId() == null || veiculoService.buscarPorId(form.getVeiculoId()).isEmpty()) {
            redirect.addFlashAttribute("erro", "Escolha o veículo. Cadastre o cliente e o carro em Clientes antes.");
            redirect.addFlashAttribute("ordem", form);
            return "redirect:/admin/ordens#form-ordem";
        }
        boolean nova = form.getId() == null;
        ordemService.salvarDoFormulario(form);
        redirect.addFlashAttribute("mensagem", nova ? "Ordem de serviço criada." : "Ordem de serviço atualizada.");
        return "redirect:/admin/ordens";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        ordemService.deletar(id);
        redirect.addFlashAttribute("mensagem", "Ordem de serviço excluída.");
        return "redirect:/admin/ordens";
    }
}
