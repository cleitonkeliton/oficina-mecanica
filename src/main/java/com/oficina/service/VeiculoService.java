package com.oficina.service;

import com.oficina.form.VeiculoForm;
import com.oficina.model.Veiculo;
import com.oficina.repository.OrdemServicoRepository;
import com.oficina.repository.VeiculoRepository;
import com.oficina.util.Documentos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.oficina.util.Textos.limpar;

@Service
public class VeiculoService {

    private final VeiculoRepository repository;
    private final OrdemServicoRepository ordemRepository;
    private final ClienteService clienteService;

    public VeiculoService(VeiculoRepository repository,
                          OrdemServicoRepository ordemRepository,
                          ClienteService clienteService) {
        this.repository = repository;
        this.ordemRepository = ordemRepository;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    public List<Veiculo> listar(String termo) {
        String t = termo == null ? "" : termo.trim();
        if (t.isEmpty()) {
            return repository.findAllByOrderByPlacaAsc();
        }
        return repository.pesquisar(t, Documentos.normalizarPlaca(t));
    }

    @Transactional(readOnly = true)
    public List<Veiculo> listarDoCliente(Long clienteId) {
        return repository.findByClienteIdOrderByPlacaAsc(clienteId);
    }

    @Transactional(readOnly = true)
    public Veiculo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Veículo não encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return repository.findByPlaca(Documentos.normalizarPlaca(placa));
    }

    @Transactional
    public Veiculo salvar(VeiculoForm form) {
        String placa = Documentos.normalizarPlaca(form.getPlaca());
        if (!Documentos.placaValida(placa)) {
            throw new RegraNegocioException("placa", "Placa inválida. Use o formato ABC-1234 ou ABC1D23");
        }
        boolean placaRepetida = form.getId() == null
                ? repository.existsByPlaca(placa)
                : repository.existsByPlacaAndIdNot(placa, form.getId());
        if (placaRepetida) {
            throw new RegraNegocioException("placa", "Já existe um veículo com esta placa");
        }

        Veiculo veiculo = form.getId() == null ? new Veiculo() : buscar(form.getId());
        veiculo.setPlaca(placa);
        veiculo.setMarca(limpar(form.getMarca()));
        veiculo.setModelo(limpar(form.getModelo()));
        veiculo.setAno(form.getAno());
        veiculo.setCor(limpar(form.getCor()));
        veiculo.setCliente(clienteService.buscar(form.getClienteId()));
        return repository.save(veiculo);
    }

    @Transactional
    public void excluir(Long id) {
        Veiculo veiculo = buscar(id);
        if (ordemRepository.existsByVeiculoId(id)) {
            throw new RegraNegocioException("Este veículo tem ordens de serviço no histórico e não pode ser excluído.");
        }
        repository.delete(veiculo);
    }
}
