package com.oficina.controller;

import com.oficina.model.OrdemServico;
import com.oficina.security.UsuarioAutenticado;
import com.oficina.service.ClienteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.VeiculoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/** Área do dono do carro: acompanha a situação dos seus veículos na oficina. */
@Controller
@RequestMapping("/area-cliente")
public class AreaClienteController {

    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final OrdemServicoService ordemService;

    public AreaClienteController(ClienteService clienteService,
                                 VeiculoService veiculoService,
                                 OrdemServicoService ordemService) {
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.ordemService = ordemService;
    }

    @GetMapping
    public String inicio(@AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
        Long clienteId = usuario.getClienteId();
        List<OrdemServico> ordens = ordemService.listarDoCliente(clienteId);
        model.addAttribute("cliente", clienteService.buscar(clienteId));
        model.addAttribute("veiculos", veiculoService.listarDoCliente(clienteId));
        model.addAttribute("abertas", ordens.stream().filter(o -> !o.getStatus().isFinalizado()).toList());
        model.addAttribute("finalizadas", ordens.stream().filter(o -> o.getStatus().isFinalizado()).toList());
        return "area-cliente/index";
    }

    @GetMapping("/ordens/{id}")
    public String ordem(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
        model.addAttribute("ordem", ordemService.buscarDoCliente(id, usuario.getClienteId()));
        return "area-cliente/ordem";
    }
}
