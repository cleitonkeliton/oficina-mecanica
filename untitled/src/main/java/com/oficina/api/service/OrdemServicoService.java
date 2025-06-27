package com.oficina.api.service;

import com.oficina.api.model.OrdemServico;
import com.oficina.api.model.Servico;
import com.oficina.api.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;

    public OrdemServicoService(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public OrdemServico salvar(OrdemServico ordem) {
        BigDecimal total = ordem.getServicos().stream()
                .map(Servico::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ordem.setValorTotal(total);
        return repository.save(ordem);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
