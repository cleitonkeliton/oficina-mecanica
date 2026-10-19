package com.oficina.api.controller;

import com.oficina.api.model.Cliente;
import com.oficina.api.model.OrdemServico;
import com.oficina.api.service.ClienteService;
import com.oficina.api.service.InformacaoOficinaService;
import com.oficina.api.service.OrdemServicoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

// Área do cliente: entra com a placa e o código de acesso e vê a situação do carro e os serviços feitos
@Controller
@RequestMapping("/cliente")
public class AreaClienteController {

    private static final String SESSAO_CLIENTE = "clienteId";

    private final ClienteService clienteService;
    private final OrdemServicoService ordemService;
    private final InformacaoOficinaService informacaoService;

    public AreaClienteController(ClienteService clienteService, OrdemServicoService ordemService,
                                 InformacaoOficinaService informacaoService) {
        this.clienteService = clienteService;
        this.ordemService = ordemService;
        this.informacaoService = informacaoService;
    }

    @GetMapping
    public String painel(HttpSession session, Model model) {
        model.addAttribute("info", informacaoService.obter());
        model.addAttribute("etapas", OrdemServico.STATUS);

        Optional<Cliente> cliente = Optional.ofNullable((Long) session.getAttribute(SESSAO_CLIENTE))
                .flatMap(clienteService::buscarPorId);
        if (cliente.isEmpty()) {
            return "cliente-login";
        }

        List<OrdemServico> ordens = ordemService.listarDoCliente(cliente.get().getId());
        model.addAttribute("cliente", cliente.get());
        model.addAttribute("veiculos", clienteService.veiculosDo(cliente.get().getId()));
        model.addAttribute("emAndamento", ordens.stream().filter(o -> !o.isConcluida()).toList());
        model.addAttribute("historico", ordens.stream().filter(OrdemServico::isConcluida).toList());
        return "cliente-painel";
    }

    @PostMapping("/entrar")
    public String entrar(@RequestParam String placa, @RequestParam String codigo,
                         HttpServletRequest request, RedirectAttributes redirect) {
        return autenticar(placa, codigo, request, redirect);
    }

    // Link enviado pela oficina (ex.: pelo WhatsApp) já entra direto
    @GetMapping("/entrar")
    public String entrarPorLink(@RequestParam(required = false) String placa,
                                @RequestParam(required = false) String codigo,
                                HttpServletRequest request, RedirectAttributes redirect) {
        if (placa == null || codigo == null) {
            return "redirect:/cliente";
        }
        return autenticar(placa, codigo, request, redirect);
    }

    @PostMapping("/sair")
    public String sair(HttpSession session) {
        session.removeAttribute(SESSAO_CLIENTE);
        return "redirect:/cliente";
    }

    private String autenticar(String placa, String codigo, HttpServletRequest request, RedirectAttributes redirect) {
        Optional<Cliente> cliente = clienteService.autenticar(placa, codigo);
        if (cliente.isEmpty()) {
            redirect.addFlashAttribute("erro", "Placa ou código de acesso inválidos. Confira os dados ou fale com a oficina.");
            redirect.addFlashAttribute("placa", placa);
            return "redirect:/cliente";
        }
        request.getSession(true);
        request.changeSessionId(); // evita reaproveitar uma sessão criada antes do login
        request.getSession().setAttribute(SESSAO_CLIENTE, cliente.get().getId());
        return "redirect:/cliente";
    }
}
