package com.oficina.api.controller;

import com.oficina.api.model.Cliente;
import com.oficina.api.model.Veiculo;
import com.oficina.api.model.Servico;
import com.oficina.api.model.OrdemServico;
import com.oficina.api.repository.ClienteRepository;
import com.oficina.api.repository.VeiculoRepository;
import com.oficina.api.repository.ServicoRepository;
import com.oficina.api.repository.OrdemServicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PainelController {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public PainelController(ClienteRepository clienteRepository,
                            VeiculoRepository veiculoRepository,
                            ServicoRepository servicoRepository,
                            OrdemServicoRepository ordemServicoRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @GetMapping("/painel")
    public String painel(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("veiculos", veiculoRepository.findAll());
        model.addAttribute("servicos", servicoRepository.findAll());
        model.addAttribute("ordens", ordemServicoRepository.findAll());
        return "painel"; // painel.html em templates
    }
}
