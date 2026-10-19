package com.oficina.controller;

import com.oficina.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Tela principal (site da oficina). */
@Controller
public class HomeController {

    private final ServicoService servicoService;

    public HomeController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("servicos", servicoService.listarAtivos());
        return "index";
    }
}
