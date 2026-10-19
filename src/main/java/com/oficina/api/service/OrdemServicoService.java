package com.oficina.api.service;

import com.oficina.api.dto.OrdemForm;
import com.oficina.api.model.OrdemServico;
import com.oficina.api.model.Servico; // usar a classe JPA
import com.oficina.api.model.Veiculo;
import com.oficina.api.repository.OrdemServicoRepository;
import com.oficina.api.repository.ServicoRepository;
import com.oficina.api.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;

    public OrdemServicoService(OrdemServicoRepository repository,
                               VeiculoRepository veiculoRepository,
                               ServicoRepository servicoRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    // Mais recentes primeiro, para a área administrativa
    public List<OrdemServico> listarRecentes() {
        return repository.findAllByOrderByIdDesc();
    }

    // Ordens do cliente (em andamento e histórico), mais recentes primeiro
    public List<OrdemServico> listarDoCliente(Long clienteId) {
        return repository.findByClienteIdOrderByIdDesc(clienteId);
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // MÉTODO SALVAR
    public OrdemServico salvar(OrdemServico ordem) {
        // Calcula o valor total somando todos os serviços
        List<Servico> servicos = ordem.getServicos() == null ? List.of() : ordem.getServicos();
        BigDecimal total = servicos.stream()
                .filter(servico -> servico.getPreco() != null)          // serviços "sob consulta" não somam
                .map(servico -> BigDecimal.valueOf(servico.getPreco())) // usar getPreco() da entidade JPA
                .reduce(BigDecimal.ZERO, BigDecimal::add);              // soma todos os preços

        ordem.setValorTotal(total);
        return repository.save(ordem);
    }

    // Cria ou atualiza a ordem a partir do formulário do admin; o cliente vem do veículo escolhido
    @Transactional
    public OrdemServico salvarDoFormulario(OrdemForm form) {
        OrdemServico ordem = form.getId() == null
                ? new OrdemServico()
                : repository.findById(form.getId()).orElseThrow();

        Veiculo veiculo = veiculoRepository.findById(form.getVeiculoId()).orElseThrow();
        ordem.setVeiculo(veiculo);
        ordem.setCliente(veiculo.getCliente());
        ordem.setServicos(form.getServicoIds() == null
                ? new ArrayList<>()
                : new ArrayList<>(servicoRepository.findAllById(form.getServicoIds())));
        ordem.setStatus(form.getStatus());
        ordem.setPrevisaoEntrega(form.getPrevisaoEntrega());
        ordem.setObservacoes(form.getObservacoes());
        return salvar(ordem);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
