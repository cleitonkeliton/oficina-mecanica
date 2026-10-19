package com.oficina.service;

import com.oficina.form.NovaOrdemForm;
import com.oficina.model.Cliente;
import com.oficina.model.OrdemServico;
import com.oficina.model.Servico;
import com.oficina.model.StatusServico;
import com.oficina.model.Veiculo;
import com.oficina.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrdemServicoServiceTest {

    private OrdemServicoRepository repository;
    private VeiculoService veiculoService;
    private ServicoService servicoService;
    private OrdemServicoService service;

    private Veiculo veiculo;

    @BeforeEach
    void preparar() {
        repository = mock(OrdemServicoRepository.class);
        veiculoService = mock(VeiculoService.class);
        servicoService = mock(ServicoService.class);
        service = new OrdemServicoService(repository, veiculoService, servicoService);

        Cliente cliente = new Cliente("Maria Souza", "52998224725", "(11) 99999-0000");
        cliente.setId(1L);
        veiculo = new Veiculo("ABC1D23", "Chevrolet", "Onix", 2020, "Prata", cliente);
        veiculo.setId(10L);
        when(veiculoService.buscar(10L)).thenReturn(veiculo);
        when(repository.save(any(OrdemServico.class))).thenAnswer(i -> i.getArgument(0));
    }

    private Servico servico(long id, String nome, String preco) {
        Servico s = new Servico(nome, null, new BigDecimal(preco), false, null);
        s.setId(id);
        when(servicoService.buscar(id)).thenReturn(s);
        return s;
    }

    private OrdemServico ordemCom(StatusServico status) {
        OrdemServico o = new OrdemServico();
        o.setId(5L);
        o.setVeiculo(veiculo);
        o.setCliente(veiculo.getCliente());
        o.setStatus(status);
        when(repository.findById(5L)).thenReturn(Optional.of(o));
        return o;
    }

    @Test
    void abrirRegistraEntradaComServicosEHistorico() {
        servico(1L, "Troca de óleo", "150.00");
        servico(2L, "Alinhamento", "120.00");
        NovaOrdemForm form = new NovaOrdemForm();
        form.setVeiculoId(10L);
        form.setServicoIds(List.of(1L, 2L, 2L));
        form.setDescricaoProblema("  barulho no freio  ");

        OrdemServico ordem = service.abrir(form, "Recepção");

        assertEquals(StatusServico.AGUARDANDO_AVALIACAO, ordem.getStatus());
        assertSame(veiculo.getCliente(), ordem.getCliente());
        assertEquals(2, ordem.getItens().size(), "serviço repetido entra uma vez só");
        assertEquals(new BigDecimal("270.00"), ordem.getValorTotal());
        assertEquals("barulho no freio", ordem.getDescricaoProblema());
        assertEquals(1, ordem.getHistorico().size());
        assertEquals("Recepção", ordem.getHistorico().get(0).getResponsavel());
    }

    @Test
    void naoAbreSegundaOrdemParaCarroQueJaEstaNaOficina() {
        OrdemServico aberta = new OrdemServico();
        aberta.setId(99L);
        aberta.setStatus(StatusServico.EM_SERVICO);
        when(repository.findByVeiculoIdOrderByDataEntradaDesc(10L)).thenReturn(List.of(aberta));
        NovaOrdemForm form = new NovaOrdemForm();
        form.setVeiculoId(10L);

        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.abrir(form, "Recepção"));
        assertTrue(e.getMessage().contains("OS 000099"));
        verify(repository, never()).save(any());
    }

    @Test
    void prontoEEntregueRegistramDatas() {
        OrdemServico ordem = ordemCom(StatusServico.EM_SERVICO);

        service.atualizarStatus(5L, StatusServico.PRONTO, null, "Ana");
        assertNotNull(ordem.getDataConclusao());
        assertNull(ordem.getDataEntrega());

        service.atualizarStatus(5L, StatusServico.ENTREGUE, "Retirado pelo dono", "Ana");
        assertNotNull(ordem.getDataEntrega());
        assertEquals(StatusServico.ENTREGUE, ordem.getStatus());
        assertEquals(2, ordem.getHistorico().size());
        assertEquals("Retirado pelo dono", ordem.getHistorico().get(1).getObservacao());
    }

    @Test
    void voltarParaManutencaoLimpaDatasDeConclusao() {
        OrdemServico ordem = ordemCom(StatusServico.EM_SERVICO);
        service.atualizarStatus(5L, StatusServico.PRONTO, null, "Ana");

        service.atualizarStatus(5L, StatusServico.EM_SERVICO, "Faltou um ajuste", "Ana");

        assertNull(ordem.getDataConclusao());
        assertNull(ordem.getDataEntrega());
    }

    @Test
    void mesmaSituacaoSoComObservacao() {
        OrdemServico ordem = ordemCom(StatusServico.AGUARDANDO_PECAS);

        assertThrows(RegraNegocioException.class,
                () -> service.atualizarStatus(5L, StatusServico.AGUARDANDO_PECAS, "  ", "Ana"));

        service.atualizarStatus(5L, StatusServico.AGUARDANDO_PECAS, "A peça chega amanhã", "Ana");
        assertEquals(1, ordem.getHistorico().size());
    }

    @Test
    void ordemFinalizadaNaoPodeSerEditada() {
        ordemCom(StatusServico.ENTREGUE);
        servico(1L, "Troca de óleo", "150.00");

        assertThrows(RegraNegocioException.class, () -> service.adicionarServico(5L, 1L));
    }

    @Test
    void naoReabreSeOCarroJaTemOutraOrdemAberta() {
        ordemCom(StatusServico.ENTREGUE);
        OrdemServico outra = new OrdemServico();
        outra.setId(6L);
        outra.setStatus(StatusServico.EM_SERVICO);
        when(repository.findByVeiculoIdOrderByDataEntradaDesc(10L)).thenReturn(List.of(outra));

        assertThrows(RegraNegocioException.class,
                () -> service.atualizarStatus(5L, StatusServico.EM_SERVICO, null, "Ana"));
    }

    @Test
    void clienteSoVeAsProprias() {
        ordemCom(StatusServico.EM_SERVICO);

        assertNotNull(service.buscarDoCliente(5L, 1L));
        assertThrows(NaoEncontradoException.class, () -> service.buscarDoCliente(5L, 2L));
        assertThrows(NaoEncontradoException.class, () -> service.buscarDoCliente(5L, null));
    }
}
