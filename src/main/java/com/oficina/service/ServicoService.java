package com.oficina.service;

import com.oficina.model.Servico;
import com.oficina.repository.ItemOrdemServicoRepository;
import com.oficina.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.oficina.util.Textos.limpar;

@Service
public class ServicoService {

    private final ServicoRepository repository;
    private final ItemOrdemServicoRepository itemRepository;

    public ServicoService(ServicoRepository repository, ItemOrdemServicoRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    /** Serviços exibidos no site e oferecidos na entrada do carro. */
    @Transactional(readOnly = true)
    public List<Servico> listarAtivos() {
        return repository.findByAtivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return repository.findAllByOrderByAtivoDescNomeAsc();
    }

    @Transactional(readOnly = true)
    public Servico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Serviço não encontrado"));
    }

    @Transactional
    public Servico salvar(Servico dados) {
        Servico servico = dados.getId() == null ? new Servico() : buscar(dados.getId());
        servico.setNome(limpar(dados.getNome()));
        servico.setDescricao(limpar(dados.getDescricao()));
        servico.setPreco(dados.getPreco());
        servico.setPrecoAPartirDe(dados.isPrecoAPartirDe());
        servico.setTempoEstimado(limpar(dados.getTempoEstimado()));
        servico.setAtivo(dados.isAtivo());
        return repository.save(servico);
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Servico servico = buscar(id);
        servico.setAtivo(!servico.isAtivo());
    }

    /**
     * Exclui o serviço. Se ele já foi usado em alguma ordem, só desativa (some do site),
     * para não apagar o histórico. Retorna true se excluiu de verdade.
     */
    @Transactional
    public boolean excluir(Long id) {
        Servico servico = buscar(id);
        if (itemRepository.existsByServicoId(id)) {
            servico.setAtivo(false);
            return false;
        }
        repository.delete(servico);
        return true;
    }

    @Transactional
    public void criarPadraoSeVazio() {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Servico("Troca de óleo e filtro", "Óleo do motor e filtro de óleo, com verificação dos níveis.", new BigDecimal("150.00"), true, "40 minutos"),
                new Servico("Alinhamento e balanceamento", "Alinhamento da direção e balanceamento das quatro rodas.", new BigDecimal("120.00"), false, "1 hora"),
                new Servico("Revisão completa", "Checagem de motor, freios, suspensão, arrefecimento e parte elétrica.", new BigDecimal("350.00"), true, "1 dia"),
                new Servico("Freios", "Troca de pastilhas e verificação de discos e fluido.", new BigDecimal("180.00"), true, "2 horas"),
                new Servico("Diagnóstico por scanner", "Leitura eletrônica de falhas da injeção e dos módulos do carro.", new BigDecimal("90.00"), false, "30 minutos"),
                new Servico("Suspensão", "Amortecedores, molas, bandejas e buchas.", new BigDecimal("250.00"), true, "1 dia"),
                new Servico("Ar-condicionado", "Higienização, troca do filtro de cabine e recarga de gás.", new BigDecimal("220.00"), true, "3 horas"),
                new Servico("Elétrica automotiva", "Bateria, alternador, motor de partida, luzes e chicotes.", new BigDecimal("80.00"), true, "Sob avaliação")
        ));
    }
}
