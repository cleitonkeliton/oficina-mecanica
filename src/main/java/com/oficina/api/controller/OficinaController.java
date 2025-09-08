package com.oficina.api.controller;

import com.oficina.api.model.Cliente;
import com.oficina.api.model.Veiculo;
import com.oficina.api.model.Servico;
import com.oficina.api.model.OrdemDeSv;
import com.oficina.api.repository.ClienteRepository;
import com.oficina.api.repository.VeiculoRepository;
import com.oficina.api.repository.ServicoRepository;
import com.oficina.api.repository.OrdemDeSvRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OficinaController {

    private final ClienteRepository clienteRepo;
    private final VeiculoRepository veiculoRepo;
    private final ServicoRepository servicoRepo;
    private final OrdemDeSvRepository ordemRepo;

    public OficinaController(ClienteRepository clienteRepo,
                             VeiculoRepository veiculoRepo,
                             ServicoRepository servicoRepo,
                             OrdemDeSvRepository ordemRepo) {
        this.clienteRepo = clienteRepo;
        this.veiculoRepo = veiculoRepo;
        this.servicoRepo = servicoRepo;
        this.ordemRepo = ordemRepo;
    }

    @GetMapping("/painel")
    public String painel(Model model) {
        List<Cliente> clientes = clienteRepo.findAll();
        List<Veiculo> veiculos = veiculoRepo.findAll();
        List<Servico> servicos = servicoRepo.findAll();
        List<OrdemDeSv> ordens = ordemRepo.findAll();

        model.addAttribute("clientes", clientes);
        model.addAttribute("veiculos", veiculos);
        model.addAttribute("servicos", servicos);
        model.addAttribute("ordens", ordens);

        return "painel"; // nome do HTML: painel.html
    }
}
