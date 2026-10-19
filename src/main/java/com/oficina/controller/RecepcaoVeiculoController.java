package com.oficina.controller;

import com.oficina.form.VeiculoForm;
import com.oficina.model.Veiculo;
import com.oficina.service.ClienteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.RegraNegocioException;
import com.oficina.service.VeiculoService;
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

import static com.oficina.controller.RecepcaoClienteController.placaSegura;

/** Cadastro de veículos. */
@Controller
@RequestMapping("/recepcao/veiculos")
public class RecepcaoVeiculoController {

    private final VeiculoService veiculoService;
    private final ClienteService clienteService;
    private final OrdemServicoService ordemService;

    public RecepcaoVeiculoController(VeiculoService veiculoService,
                                     ClienteService clienteService,
                                     OrdemServicoService ordemService) {
        this.veiculoService = veiculoService;
        this.clienteService = clienteService;
        this.ordemService = ordemService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("veiculos", veiculoService.listar(q));
        model.addAttribute("q", q);
        return "recepcao/veiculos";
    }

    /**
     * Novo veículo. Pode vir com o dono já escolhido (clienteId) e com a placa digitada na entrada;
     * com voltar=entrada, depois de salvar volta para a entrada do carro.
     */
    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long clienteId,
                       @RequestParam(required = false) String placa,
                       @RequestParam(required = false) String voltar, Model model) {
        VeiculoForm form = new VeiculoForm();
        form.setClienteId(clienteId);
        form.setPlaca(placaSegura(placa));
        model.addAttribute("veiculoForm", form);
        model.addAttribute("voltar", voltar);
        prepararFormulario(model);
        return "recepcao/veiculo-form";
    }

    /** Edição do veículo. Com clienteId (vindo do cadastro de um cliente novo), já deixa o novo dono escolhido. */
    @GetMapping("/{id}")
    public String editar(@PathVariable Long id, @RequestParam(required = false) Long clienteId, Model model) {
        Veiculo veiculo = veiculoService.buscar(id);
        VeiculoForm form = VeiculoForm.de(veiculo);
        if (clienteId != null && !clienteId.equals(form.getClienteId())) {
            form.setClienteId(clienteId);
            model.addAttribute("aviso", "Novo dono selecionado. Clique em Salvar veículo para confirmar a troca.");
        }
        model.addAttribute("veiculoForm", form);
        prepararEdicao(model, veiculo);
        prepararFormulario(model);
        return "recepcao/veiculo-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("veiculoForm") VeiculoForm form, BindingResult resultado,
                         @RequestParam(required = false) String voltar,
                         Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Veiculo veiculo = veiculoService.salvar(form);
                redirect.addFlashAttribute("sucesso", "Veículo " + veiculo.getPlacaFormatada() + " salvo.");
                if ("entrada".equals(voltar)) {
                    return "redirect:/recepcao/entrada?placa=" + veiculo.getPlaca();
                }
                return "redirect:/recepcao/veiculos/" + veiculo.getId();
            } catch (RegraNegocioException e) {
                Formularios.mostrarErro(e, resultado, model);
            }
        }
        model.addAttribute("voltar", voltar);
        if (form.getId() != null) {
            prepararEdicao(model, veiculoService.buscar(form.getId()));
        }
        prepararFormulario(model);
        return "recepcao/veiculo-form";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            veiculoService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Veículo excluído.");
            return "redirect:/recepcao/veiculos";
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/recepcao/veiculos/" + id;
        }
    }

    /** Dados do veículo como estão salvos (o formulário pode ter sido alterado e ter erro). */
    private void prepararEdicao(Model model, Veiculo veiculo) {
        model.addAttribute("veiculoSalvo", veiculo);
        model.addAttribute("ordens", ordemService.listarDoVeiculo(veiculo.getId()));
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listar(null));
    }
}
