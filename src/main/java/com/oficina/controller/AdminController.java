package com.oficina.controller;

import com.oficina.model.ConfiguracaoSite;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusServico;
import com.oficina.service.ClienteService;
import com.oficina.service.ConfiguracaoSiteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/** Painel do administrador e edição das informações da tela principal. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrdemServicoService ordemService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;
    private final ConfiguracaoSiteService configuracaoService;

    public AdminController(OrdemServicoService ordemService,
                           ServicoService servicoService,
                           ClienteService clienteService,
                           ConfiguracaoSiteService configuracaoService) {
        this.ordemService = ordemService;
        this.servicoService = servicoService;
        this.clienteService = clienteService;
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    public String painel(Model model) {
        List<OrdemServico> naOficina = ordemService.listarNaOficina();
        model.addAttribute("ordens", naOficina);
        Map<StatusServico, Long> contagem = ordemService.contagemPorStatus();
        model.addAttribute("contagem", contagem);
        model.addAttribute("totalEntregues", contagem.get(StatusServico.ENTREGUE));
        model.addAttribute("totalNaOficina", naOficina.size());
        model.addAttribute("totalProntos", naOficina.stream().filter(o -> o.getStatus() == StatusServico.PRONTO).count());
        model.addAttribute("totalAtrasados", naOficina.stream().filter(OrdemServico::isAtrasada).count());
        model.addAttribute("totalServicos", servicoService.listarAtivos().size());
        model.addAttribute("totalClientes", clienteService.contar());
        model.addAttribute("statusList", StatusServico.values());
        return "admin/index";
    }

    @GetMapping("/site")
    public String site(Model model) {
        model.addAttribute("config", configuracaoService.obter());
        return "admin/site";
    }

    @PostMapping("/site")
    public String salvarSite(@Valid @ModelAttribute("config") ConfiguracaoSite config, BindingResult resultado,
                             RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return "admin/site";
        }
        configuracaoService.salvar(config);
        redirect.addFlashAttribute("sucesso", "Informações do site atualizadas.");
        return "redirect:/admin/site";
    }
}
