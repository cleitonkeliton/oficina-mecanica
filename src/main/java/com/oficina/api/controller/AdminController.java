package com.oficina.api.controller;

import com.oficina.api.model.InformacaoOficina;
import com.oficina.api.model.Servico;
import com.oficina.api.service.InformacaoOficinaService;
import com.oficina.api.service.ServicoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Área administrativa: edita o conteúdo da página principal
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InformacaoOficinaService informacaoService;
    private final ServicoService servicoService;

    public AdminController(InformacaoOficinaService informacaoService, ServicoService servicoService) {
        this.informacaoService = informacaoService;
        this.servicoService = servicoService;
    }

    @GetMapping
    public String painel(Model model) {
        if (!model.containsAttribute("servico")) {
            model.addAttribute("servico", new Servico());
        }
        model.addAttribute("info", informacaoService.obter());
        model.addAttribute("servicos", servicoService.listarTodos());
        return "admin";
    }

    @PostMapping("/informacoes")
    public String salvarInformacoes(@ModelAttribute InformacaoOficina info, RedirectAttributes redirect) {
        informacaoService.salvar(info);
        redirect.addFlashAttribute("mensagem", "Informações da oficina atualizadas.");
        return "redirect:/admin";
    }

    @GetMapping("/servicos/{id}/editar")
    public String editarServico(@PathVariable Long id, RedirectAttributes redirect) {
        return servicoService.buscarPorId(id)
                .map(s -> {
                    redirect.addFlashAttribute("servico", s);
                    return "redirect:/admin#form-servico";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("erro", "Serviço não encontrado.");
                    return "redirect:/admin";
                });
    }

    @PostMapping("/servicos")
    public String salvarServico(@ModelAttribute Servico servico, RedirectAttributes redirect) {
        if (servico.getNome() == null || servico.getNome().isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome do serviço.");
            redirect.addFlashAttribute("servico", servico);
            return "redirect:/admin#form-servico";
        }
        // Checkbox desmarcado não é enviado pelo navegador
        if (servico.getAtivo() == null) {
            servico.setAtivo(false);
        }
        boolean novo = servico.getId() == null;
        servicoService.salvar(servico);
        redirect.addFlashAttribute("mensagem", novo ? "Serviço adicionado." : "Serviço atualizado.");
        return "redirect:/admin";
    }

    @PostMapping("/servicos/{id}/excluir")
    public String excluirServico(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            servicoService.deletar(id);
            redirect.addFlashAttribute("mensagem", "Serviço excluído.");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro",
                    "Este serviço está em ordens de serviço e não pode ser excluído. Desmarque \"Exibir no site\" para escondê-lo.");
        }
        return "redirect:/admin";
    }
}
