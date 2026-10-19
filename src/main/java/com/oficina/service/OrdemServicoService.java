package com.oficina.service;

import com.oficina.form.ItemForm;
import com.oficina.form.NovaOrdemForm;
import com.oficina.form.OrdemDadosForm;
import com.oficina.model.HistoricoStatus;
import com.oficina.model.ItemOrdemServico;
import com.oficina.model.OrdemServico;
import com.oficina.model.Servico;
import com.oficina.model.StatusServico;
import com.oficina.model.Veiculo;
import com.oficina.repository.OrdemServicoRepository;
import com.oficina.util.Documentos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.oficina.util.Textos.limpar;

@Service
public class OrdemServicoService {

    /** Situações em que o carro já saiu da oficina. */
    public static final Set<StatusServico> FINALIZADOS = EnumSet.of(StatusServico.ENTREGUE, StatusServico.CANCELADO);

    private final OrdemServicoRepository repository;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;

    public OrdemServicoService(OrdemServicoRepository repository,
                               VeiculoService veiculoService,
                               ServicoService servicoService) {
        this.repository = repository;
        this.veiculoService = veiculoService;
        this.servicoService = servicoService;
    }

    /** Carros que estão na oficina agora (ainda não entregues nem cancelados), do mais antigo ao mais novo. */
    @Transactional(readOnly = true)
    public List<OrdemServico> listarNaOficina() {
        return repository.findByStatusNotInOrderByDataEntradaAsc(FINALIZADOS);
    }

    /**
     * Pesquisa por placa, nome do cliente, modelo ou número da OS, opcionalmente filtrando a situação.
     * Sem filtro nenhum, mostra as 100 ordens mais recentes.
     */
    @Transactional(readOnly = true)
    public List<OrdemServico> pesquisar(StatusServico status, String termo) {
        String t = termo == null ? "" : termo.trim();
        if (status == null && t.isEmpty()) {
            return repository.findTop100ByOrderByDataEntradaDesc();
        }
        String digitosNumero = t.replaceFirst("(?i)^os\\s*", "");
        Long numero = digitosNumero.matches("\\d{1,18}") ? Long.valueOf(digitosNumero) : null;
        return repository.pesquisar(status, t, Documentos.normalizarPlaca(t), numero);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarDoCliente(Long clienteId) {
        return repository.findByClienteIdOrderByDataEntradaDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarDoVeiculo(Long veiculoId) {
        return repository.findByVeiculoIdOrderByDataEntradaDesc(veiculoId);
    }

    @Transactional(readOnly = true)
    public OrdemServico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ordem de serviço não encontrada"));
    }

    /** Busca garantindo que a ordem é do cliente logado (um cliente nunca vê o carro de outro). */
    @Transactional(readOnly = true)
    public OrdemServico buscarDoCliente(Long id, Long clienteId) {
        OrdemServico ordem = buscar(id);
        if (clienteId == null || !clienteId.equals(ordem.getCliente().getId())) {
            throw new NaoEncontradoException("Ordem de serviço não encontrada");
        }
        return ordem;
    }

    /** Ordem aberta (carro ainda na oficina) do veículo, se houver. */
    @Transactional(readOnly = true)
    public OrdemServico buscarAbertaDoVeiculo(Long veiculoId) {
        return repository.findByVeiculoIdOrderByDataEntradaDesc(veiculoId).stream()
                .filter(o -> !o.getStatus().isFinalizado())
                .findFirst()
                .orElse(null);
    }

    /** Quantidade de ordens em cada situação, na ordem do enum. */
    @Transactional(readOnly = true)
    public Map<StatusServico, Long> contagemPorStatus() {
        Map<StatusServico, Long> contagem = new EnumMap<>(StatusServico.class);
        for (StatusServico s : StatusServico.values()) {
            contagem.put(s, repository.countByStatus(s));
        }
        return contagem;
    }

    /** Registra a entrada do carro na oficina. */
    @Transactional
    public OrdemServico abrir(NovaOrdemForm form, String responsavel) {
        Veiculo veiculo = veiculoService.buscar(form.getVeiculoId());
        OrdemServico aberta = buscarAbertaDoVeiculo(veiculo.getId());
        if (aberta != null) {
            throw new RegraNegocioException("Este veículo já está na oficina (" + aberta.getNumero() + ").");
        }

        OrdemServico ordem = new OrdemServico();
        ordem.setVeiculo(veiculo);
        ordem.setCliente(veiculo.getCliente());
        ordem.setQuilometragem(form.getQuilometragem());
        ordem.setDescricaoProblema(limpar(form.getDescricaoProblema()));
        ordem.setObservacoes(limpar(form.getObservacoes()));
        ordem.setPrevisaoEntrega(form.getPrevisaoEntrega());
        ordem.setDataEntrada(LocalDateTime.now());
        if (form.getServicoIds() != null) {
            form.getServicoIds().stream().distinct().forEach(servicoId ->
                    ordem.adicionarItem(ItemOrdemServico.doCatalogo(servicoService.buscar(servicoId))));
        }
        ordem.registrarHistorico(new HistoricoStatus(StatusServico.AGUARDANDO_AVALIACAO,
                "Veículo recebido na oficina.", responsavel));
        return repository.save(ordem);
    }

    /**
     * Muda a situação do carro e registra no histórico (o cliente vê a observação).
     * Também dá para manter a mesma situação e só deixar um recado novo.
     */
    @Transactional
    public OrdemServico atualizarStatus(Long id, StatusServico novo, String observacao, String responsavel) {
        if (novo == null) {
            throw new RegraNegocioException("Selecione a nova situação");
        }
        OrdemServico ordem = buscar(id);
        String obs = limpar(observacao);
        if (obs != null && obs.length() > 1000) {
            throw new RegraNegocioException("A observação pode ter no máximo 1000 caracteres");
        }
        if (novo == ordem.getStatus() && obs == null) {
            throw new RegraNegocioException("O carro já está nesta situação. Escreva uma observação para registrar uma novidade.");
        }
        // Reabrir uma ordem finalizada só se o carro não tiver outra ordem aberta
        if (ordem.getStatus().isFinalizado() && !novo.isFinalizado()) {
            OrdemServico aberta = buscarAbertaDoVeiculo(ordem.getVeiculo().getId());
            if (aberta != null && !aberta.getId().equals(ordem.getId())) {
                throw new RegraNegocioException("Este veículo já tem outra ordem aberta (" + aberta.getNumero() + ").");
            }
        }

        LocalDateTime agora = LocalDateTime.now();
        switch (novo) {
            case ENTREGUE -> {
                if (ordem.getDataConclusao() == null) ordem.setDataConclusao(agora);
                ordem.setDataEntrega(agora);
            }
            case PRONTO -> {
                if (ordem.getDataConclusao() == null) ordem.setDataConclusao(agora);
                ordem.setDataEntrega(null);
            }
            case CANCELADO -> ordem.setDataEntrega(null);
            default -> {
                ordem.setDataConclusao(null);
                ordem.setDataEntrega(null);
            }
        }
        ordem.setStatus(novo);
        ordem.registrarHistorico(new HistoricoStatus(novo, obs, responsavel));
        return ordem;
    }

    @Transactional
    public OrdemServico atualizarDados(Long id, OrdemDadosForm form) {
        OrdemServico ordem = buscarEditavel(id);
        ordem.setQuilometragem(form.getQuilometragem());
        ordem.setDescricaoProblema(limpar(form.getDescricaoProblema()));
        ordem.setObservacoes(limpar(form.getObservacoes()));
        ordem.setPrevisaoEntrega(form.getPrevisaoEntrega());
        return ordem;
    }

    /** Lança um serviço do catálogo com o preço atual dele. */
    @Transactional
    public OrdemServico adicionarServico(Long id, Long servicoId) {
        OrdemServico ordem = buscarEditavel(id);
        Servico servico = servicoService.buscar(servicoId);
        ordem.adicionarItem(ItemOrdemServico.doCatalogo(servico));
        return ordem;
    }

    /** Lança uma peça ou serviço avulso. */
    @Transactional
    public OrdemServico adicionarItem(Long id, ItemForm form) {
        OrdemServico ordem = buscarEditavel(id);
        ordem.adicionarItem(new ItemOrdemServico(null, limpar(form.getDescricao()),
                form.getQuantidade(), form.getValorUnitario()));
        return ordem;
    }

    @Transactional
    public OrdemServico removerItem(Long id, Long itemId) {
        OrdemServico ordem = buscarEditavel(id);
        ItemOrdemServico item = ordem.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NaoEncontradoException("Item não encontrado"));
        ordem.removerItem(item);
        return ordem;
    }

    /** Ordem entregue ou cancelada fica travada; para mexer, reabra mudando a situação. */
    private OrdemServico buscarEditavel(Long id) {
        OrdemServico ordem = buscar(id);
        if (ordem.getStatus().isFinalizado()) {
            throw new RegraNegocioException("Esta ordem está " + ordem.getStatus().getDescricao().toLowerCase()
                    + " e não pode ser alterada. Para mexer nela, mude a situação primeiro.");
        }
        return ordem;
    }
}
