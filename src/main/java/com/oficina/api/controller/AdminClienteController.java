package com.oficina.api.controller;

import com.oficina.api.dto.ClienteForm;
import com.oficina.api.model.Veiculo;
import com.oficina.api.service.ClienteService;
import com.oficina.api.service.VeiculoService;
import com.oficina.util.Codigos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Área administrativa: cadastro de clientes com seus veículos e código de acesso à área do cliente
@Controller
@RequestMapping("/admin/clientes")
public class AdminClienteController {

    private final ClienteService clienteService;
    private final VeiculoService veiculoService;

    public AdminClienteController(ClienteService clienteService, VeiculoService veiculoService) {
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new ClienteForm());
        }
        Map<Long, List<Veiculo>> veiculosPorCliente = veiculoService.listarTodos().stream()
                .filter(v -> v.getCliente() != null)
                .collect(Collectors.groupingBy(v -> v.getCliente().getId()));
        model.addAttribute("clientes", clienteService.listarComCodigo());
        model.addAttribute("veiculosPorCliente", veiculosPorCliente);
        // Endereço completo do site, para montar o link enviado ao cliente
        model.addAttribute("baseUrl", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
        return "admin-clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, RedirectAttributes redirect) {
        return clienteService.buscarPorId(id)
                .map(c -> {
                    redirect.addFlashAttribute("cliente", ClienteForm.de(c));
                    return "redirect:/admin/clientes#form-cliente";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("erro", "Cliente não encontrado.");
                    return "redirect:/admin/clientes";
                });
    }

    @PostMapping
    public String salvar(@ModelAttribute("cliente") ClienteForm form, RedirectAttributes redirect) {
        boolean novo = form.getId() == null;
        String erro = null;
        if (form.getNome() == null || form.getNome().isBlank()) {
            erro = "Informe o nome do cliente.";
        } else if (novo && Codigos.normalizarPlaca(form.getPlaca()).isEmpty()) {
            erro = "Informe a placa do veículo.";
        } else if (novo && clienteService.placaCadastrada(form.getPlaca())) {
            erro = "Já existe um veículo com essa placa.";
        }
        if (erro != null) {
            redirect.addFlashAttribute("erro", erro);
            redirect.addFlashAttribute("cliente", form);
            return "redirect:/admin/clientes#form-cliente";
        }
        var cliente = clienteService.salvarDoFormulario(form);
        redirect.addFlashAttribute("mensagem", novo
                ? "Cliente cadastrado. Código de acesso: " + cliente.getCodigoAcesso()
                : "Cliente atualizado.");
        return "redirect:/admin/clientes";
    }

    @PostMapping("/{id}/veiculos")
    public String adicionarVeiculo(@PathVariable Long id,
                                   @RequestParam(required = false) String marca,
                                   @RequestParam(required = false) String modelo,
                                   @RequestParam String placa,
                                   RedirectAttributes redirect) {
        if (Codigos.normalizarPlaca(placa).isEmpty() || clienteService.placaCadastrada(placa)) {
            redirect.addFlashAttribute("erro", "Informe uma placa válida que ainda não esteja cadastrada.");
            return "redirect:/admin/clientes";
        }
        clienteService.buscarPorId(id).ifPresent(c -> clienteService.adicionarVeiculo(c, marca, modelo, placa));
        redirect.addFlashAttribute("mensagem", "Veículo adicionado.");
        return "redirect:/admin/clientes";
    }

    // Gera um código novo (por exemplo, se o cliente compartilhou o antigo)
    @PostMapping("/{id}/novo-codigo")
    public String novoCodigo(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.buscarPorId(id).ifPresent(c -> {
            c.setCodigoAcesso(Codigos.gerar(8));
            clienteService.salvar(c);
            redirect.addFlashAttribute("mensagem", "Novo código de " + c.getNome() + ": " + c.getCodigoAcesso());
        });
        return "redirect:/admin/clientes";
    }
}
