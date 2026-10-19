package com.oficina.api.service;

import com.oficina.api.dto.ClienteForm;
import com.oficina.api.model.Cliente;
import com.oficina.api.model.Veiculo;
import com.oficina.api.repository.ClienteRepository;
import com.oficina.api.repository.VeiculoRepository;
import com.oficina.util.Codigos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final VeiculoRepository veiculoRepository;

    public ClienteService(ClienteRepository repository, VeiculoRepository veiculoRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    // Lista para a área administrativa; clientes antigos ganham o código de acesso aqui
    @Transactional
    public List<Cliente> listarComCodigo() {
        List<Cliente> clientes = repository.findAllByOrderByNomeAsc();
        clientes.stream().filter(c -> c.getCodigoAcesso() == null).forEach(c -> {
            c.setCodigoAcesso(Codigos.gerar(8));
            repository.save(c);
        });
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public boolean placaCadastrada(String placa) {
        return veiculoRepository.existsByPlacaIgnoreCase(Codigos.normalizarPlaca(placa));
    }

    // Cadastro novo (cliente + veículo) ou edição dos dados do cliente
    @Transactional
    public Cliente salvarDoFormulario(ClienteForm form) {
        Cliente cliente = form.getId() == null ? new Cliente() : repository.findById(form.getId()).orElseThrow();
        cliente.setNome(form.getNome());
        cliente.setTelefone(form.getTelefone());
        cliente.setEmail(form.getEmail());
        cliente.setCpf(form.getCpf());
        cliente = repository.save(cliente);

        if (form.getId() == null) {
            adicionarVeiculo(cliente, form.getMarca(), form.getModelo(), form.getPlaca());
        }
        return cliente;
    }

    public Veiculo adicionarVeiculo(Cliente cliente, String marca, String modelo, String placa) {
        return veiculoRepository.save(new Veiculo(marca, modelo, Codigos.normalizarPlaca(placa), cliente));
    }

    public List<Veiculo> veiculosDo(Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }

    // Login do cliente: o código de acesso precisa ser dele e a placa de um dos seus veículos
    public Optional<Cliente> autenticar(String placa, String codigo) {
        if (placa == null || codigo == null) return Optional.empty();
        String placaNormalizada = Codigos.normalizarPlaca(placa);
        return repository.findByCodigoAcesso(codigo.trim().toUpperCase(Locale.ROOT))
                .filter(c -> veiculoRepository.findByClienteId(c.getId()).stream()
                        .anyMatch(v -> Codigos.normalizarPlaca(v.getPlaca()).equals(placaNormalizada)));
    }
}
