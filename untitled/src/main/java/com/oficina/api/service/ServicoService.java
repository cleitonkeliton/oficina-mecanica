package com.oficina.api.service;

import com.oficina.api.model.Servico;
import com.oficina.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public List<Servico> listarTodos() {
        return repository.findAll();
    }

    public Optional<Servico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Servico salvar(Servico servico) {
        return repository.save(servico);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
