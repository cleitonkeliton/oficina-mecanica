package com.oficina.controller;

import com.oficina.form.ClienteForm;
import com.oficina.model.Cliente;
import com.oficina.service.ClienteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.VeiculoService;
import com.oficina.util.Documentos;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cadastro de clientes (donos dos carros) e do acesso deles à área de acompanhamento. */
@Controller
@RequestMapping("/recepcao/clientes")
public class RecepcaoClienteController {

    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final OrdemServicoService ordemService;

    public RecepcaoClienteController(ClienteService clienteService,
                                     VeiculoService veiculoService,
                                     OrdemServicoService ordemService) {
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.ordemService = ordemService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clientes", clienteService.listar(q));
        model.addAttribute("q", q);
        return "recepcao/clientes";
    }

    /**
     * Novo cliente. Quando vem do cadastro de veículo (voltar=veiculo), depois de salvar
     * volta para lá com o cliente já selecionado.
     */
    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) String voltar,
                       @RequestParam(required = false) String placa, Model model) {
        model.addAttribute("clienteForm", new ClienteForm());
        model.addAttribute("voltar", voltar);
        model.addAttribute("placa", placaSegura(placa));
        return "recepcao/cliente-form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscar(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("veiculos", veiculoService.listarDoCliente(id));
        model.addAttribute("ordens", ordemService.listarDoCliente(id));
        model.addAttribute("temAcesso", clienteService.possuiAcesso(id));
        return "recepcao/cliente";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("clienteForm", ClienteForm.de(clienteService.buscar(id)));
        model.addAttribute("temAcesso", clienteService.possuiAcesso(id));
        return "recepcao/cliente-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("clienteForm") ClienteForm form, BindingResult resultado,
                         @RequestParam(required = false) String voltar,
                         @RequestParam(required = false) String placa,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                boolean novo = form.getId() == null;
                Cliente cliente = clienteService.salvar(form);
                redirect.addFlashAttribute("sucesso", "Cliente " + cliente.getNome() + " salvo."
                        + (form.getSenhaAcesso() != null && !form.getSenhaAcesso().isBlank()
                        ? " Acesso ao site: login " + cliente.getCpfFormatado() + " com a senha informada." : ""));
                if (novo && "veiculo".equals(voltar)) {
                    String p = placaSegura(placa);
                    return "redirect:/recepcao/veiculos/novo?voltar=entrada&clienteId=" + cliente.getId()
                            + (p != null ? "&placa=" + p : "");
                }
                return "redirect:/recepcao/clientes/" + cliente.getId();
            } catch (RegraNegocioException e) {
                Formularios.mostrarErro(e, resultado, model);
            }
        }
        model.addAttribute("voltar", voltar);
        model.addAttribute("placa", placaSegura(placa));
        if (form.getId() != null) {
            model.addAttribute("temAcesso", clienteService.possuiAcesso(form.getId()));
        }
        return "recepcao/cliente-form";
    }

    @PostMapping("/{id}/acesso")
    public String definirAcesso(@PathVariable Long id, @RequestParam String senha, RedirectAttributes redirect) {
        Cliente cliente = clienteService.buscar(id);
        try {
            clienteService.definirAcesso(cliente, senha);
            redirect.addFlashAttribute("sucesso", "Acesso liberado. O cliente entra com o CPF "
                    + cliente.getCpfFormatado() + " e a senha informada.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recepcao/clientes/" + id;
    }

    @PostMapping("/{id}/acesso/remover")
    public String removerAcesso(@PathVariable Long id, RedirectAttributes redirect) {
        clienteService.removerAcesso(id);
        redirect.addFlashAttribute("sucesso", "Acesso do cliente ao site removido.");
        return "redirect:/recepcao/clientes/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            clienteService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Cliente excluído.");
            return "redirect:/recepcao/clientes";
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/recepcao/clientes/" + id;
        }
    }

    /** Só repassa a placa adiante se for válida (evita colocar qualquer texto no endereço). */
    static String placaSegura(String placa) {
        return Documentos.placaValida(placa) ? Documentos.normalizarPlaca(placa) : null;
    }
}
