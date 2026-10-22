package com.oficina.controller;

import com.oficina.form.ItemForm;
import com.oficina.form.NovaOrdemForm;
import com.oficina.form.OrdemDadosForm;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusServico;
import com.oficina.model.Veiculo;
import com.oficina.security.UsuarioAutenticado;
import com.oficina.service.ClienteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.ServicoService;
import com.oficina.service.VeiculoService;
import com.oficina.util.Documentos;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import java.util.List;

/**
 * Tela da recepção (também usada pelo administrador): carros na oficina,
 * entrada de veículos e controle da situação de cada ordem de serviço.
 */
@Controller
@RequestMapping("/recepcao")
public class RecepcaoController {

    private final OrdemServicoService ordemService;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;
    private final ClienteService clienteService;

    public RecepcaoController(OrdemServicoService ordemService,
                              VeiculoService veiculoService,
                              ServicoService servicoService,
                              ClienteService clienteService) {
        this.ordemService = ordemService;
        this.veiculoService = veiculoService;
        this.servicoService = servicoService;
        this.clienteService = clienteService;
    }

    @ModelAttribute("statusList")
    public StatusServico[] statusList() {
        return StatusServico.values();
    }

    /** Painel com os carros que estão na oficina agora. */
    @GetMapping
    public String painel(Model model) {
        List<OrdemServico> naOficina = ordemService.listarNaOficina();
        model.addAttribute("ordens", naOficina);
        model.addAttribute("contagem", ordemService.contagemPorStatus());
        model.addAttribute("totalAtrasados", naOficina.stream().filter(OrdemServico::isAtrasada).count());
        model.addAttribute("voltarPara", "painel");
        return "recepcao/index";
    }

    @GetMapping("/ordens")
    public String ordens(@RequestParam(required = false) StatusServico status,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) boolean atrasados, Model model) {
        model.addAttribute("ordens", atrasados ? ordemService.listarAtrasadas() : ordemService.pesquisar(status, q));
        model.addAttribute("statusFiltro", atrasados ? null : status);
        model.addAttribute("q", atrasados ? null : q);
        model.addAttribute("atrasados", atrasados);
        return "recepcao/ordens";
    }

    /** Entrada de veículo: procura a placa e, se o carro já é cadastrado, abre a ordem de serviço. */
    @GetMapping("/entrada")
    public String entrada(@RequestParam(required = false) String placa, Model model) {
        model.addAttribute("placa", placa);
        model.addAttribute("buscou", false);
        if (placa != null && !placa.isBlank()) {
            if (!Documentos.placaValida(placa)) {
                model.addAttribute("erro", "Placa inválida. Use o formato ABC-1234 ou ABC1D23.");
            } else {
                Veiculo veiculo = veiculoService.buscarPorPlaca(placa).orElse(null);
                model.addAttribute("placaNormalizada", Documentos.normalizarPlaca(placa));
                model.addAttribute("buscou", true);
                if (veiculo != null) {
                    prepararEntrada(model, veiculo);
                    NovaOrdemForm form = new NovaOrdemForm();
                    form.setVeiculoId(veiculo.getId());
                    model.addAttribute("ordemForm", form);
                }
            }
        }
        return "recepcao/entrada";
    }

    @PostMapping("/ordens")
    public String abrir(@Valid @ModelAttribute("ordemForm") NovaOrdemForm form, BindingResult resultado,
                        @AuthenticationPrincipal UsuarioAutenticado usuario,
                        Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                OrdemServico ordem = ordemService.abrir(form, usuario.getNome());
                redirect.addFlashAttribute("sucesso", "Entrada registrada: " + ordem.getNumero() + ".");
                return "redirect:/recepcao/ordens/" + ordem.getId();
            } catch (RegraNegocioException e) {
                Formularios.mostrarErro(e, resultado, model);
            }
        }
        if (form.getVeiculoId() == null) {
            return "redirect:/recepcao/entrada";
        }
        Veiculo veiculo = veiculoService.buscar(form.getVeiculoId());
        model.addAttribute("placa", veiculo.getPlacaFormatada());
        model.addAttribute("placaNormalizada", veiculo.getPlaca());
        model.addAttribute("buscou", true);
        prepararEntrada(model, veiculo);
        return "recepcao/entrada";
    }

    @GetMapping("/ordens/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        prepararDetalhe(model, ordemService.buscar(id));
        return "recepcao/ordem";
    }

    @PostMapping("/ordens/{id}/status")
    public String atualizarStatus(@PathVariable Long id, @RequestParam StatusServico status,
                                  @RequestParam(required = false) String observacao,
                                  @RequestParam(required = false) String voltar,
                                  @AuthenticationPrincipal UsuarioAutenticado usuario,
                                  RedirectAttributes redirect) {
        try {
            OrdemServico ordem = ordemService.atualizarStatus(id, status, observacao, usuario.getNome());
            redirect.addFlashAttribute("sucesso",
                    ordem.getNumero() + " (" + ordem.getVeiculo().getPlacaFormatada() + "): " + status.getDescricao() + ".");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        if ("admin".equals(voltar)) {
            return "redirect:/admin";
        }
        return "painel".equals(voltar) ? "redirect:/recepcao" : "redirect:/recepcao/ordens/" + id;
    }

    @PostMapping("/ordens/{id}/dados")
    public String atualizarDados(@PathVariable Long id, @Valid @ModelAttribute("dadosForm") OrdemDadosForm form,
                                 BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            prepararDetalhe(model, ordemService.buscar(id));
            return "recepcao/ordem";
        }
        try {
            ordemService.atualizarDados(id, form);
            redirect.addFlashAttribute("sucesso", "Dados da ordem atualizados.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recepcao/ordens/" + id;
    }

    @PostMapping("/ordens/{id}/itens/servico")
    public String adicionarServico(@PathVariable Long id, @RequestParam(required = false) Long servicoId,
                                   RedirectAttributes redirect) {
        if (servicoId == null) {
            redirect.addFlashAttribute("erro", "Selecione um serviço.");
            return "redirect:/recepcao/ordens/" + id + "#itens";
        }
        try {
            ordemService.adicionarServico(id, servicoId);
            redirect.addFlashAttribute("sucesso", "Serviço adicionado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recepcao/ordens/" + id + "#itens";
    }

    @PostMapping("/ordens/{id}/itens")
    public String adicionarItem(@PathVariable Long id, @Valid @ModelAttribute("itemForm") ItemForm form,
                                BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            prepararDetalhe(model, ordemService.buscar(id));
            return "recepcao/ordem";
        }
        try {
            ordemService.adicionarItem(id, form);
            redirect.addFlashAttribute("sucesso", "Item adicionado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recepcao/ordens/" + id + "#itens";
    }

    @PostMapping("/ordens/{id}/itens/{itemId}/excluir")
    public String removerItem(@PathVariable Long id, @PathVariable Long itemId, RedirectAttributes redirect) {
        try {
            ordemService.removerItem(id, itemId);
            redirect.addFlashAttribute("sucesso", "Item removido.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recepcao/ordens/" + id + "#itens";
    }

    private void prepararEntrada(Model model, Veiculo veiculo) {
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("ordemAberta", ordemService.buscarAbertaDoVeiculo(veiculo.getId()));
        model.addAttribute("servicos", servicoService.listarAtivos());
        model.addAttribute("clienteTemAcesso", clienteService.possuiAcesso(veiculo.getCliente().getId()));
    }

    /** Coloca na página tudo o que o detalhe da ordem usa, sem apagar formulários com erro. */
    private void prepararDetalhe(Model model, OrdemServico ordem) {
        model.addAttribute("ordem", ordem);
        model.addAttribute("servicos", servicoService.listarAtivos());
        model.addAttribute("clienteTemAcesso", clienteService.possuiAcesso(ordem.getCliente().getId()));
        if (!model.containsAttribute("dadosForm")) {
            model.addAttribute("dadosForm", OrdemDadosForm.de(ordem));
        }
        if (!model.containsAttribute("itemForm")) {
            model.addAttribute("itemForm", new ItemForm());
        }
    }
}
