package com.oficina.api.controller;

import com.oficina.api.service.InformacaoOficinaService;
import com.oficina.api.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Páginas públicas do site da oficina
@Controller
public class SiteController {

    private final InformacaoOficinaService informacaoService;
    private final ServicoService servicoService;

    public SiteController(InformacaoOficinaService informacaoService, ServicoService servicoService) {
        this.informacaoService = informacaoService;
        this.servicoService = servicoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("info", informacaoService.obter());
        model.addAttribute("servicos", servicoService.listarVisiveis());
        return "index";
    }

    // Endereço antigo/alternativo da área do cliente
    @GetMapping("/acompanhar")
    public String acompanhar() {
        return "redirect:/cliente";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("info", informacaoService.obter());
        return "login";
    }
}
