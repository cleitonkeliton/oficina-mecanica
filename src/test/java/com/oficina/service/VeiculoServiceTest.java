package com.oficina.service;

import com.oficina.form.VeiculoForm;
import com.oficina.model.Cliente;
import com.oficina.model.Veiculo;
import com.oficina.repository.OrdemServicoRepository;
import com.oficina.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    private VeiculoRepository repository;
    private OrdemServicoRepository ordemRepository;
    private ClienteService clienteService;
    private VeiculoService service;

    @BeforeEach
    void preparar() {
        repository = mock(VeiculoRepository.class);
        ordemRepository = mock(OrdemServicoRepository.class);
        clienteService = mock(ClienteService.class);
        service = new VeiculoService(repository, ordemRepository, clienteService);

        Cliente dono = new Cliente("Maria", "52998224725", null);
        dono.setId(1L);
        Cliente novoDono = new Cliente("João", "11144477735", null);
        novoDono.setId(2L);
        Veiculo veiculo = new Veiculo("ABC1D23", "Fiat", "Uno", 2010, null, dono);
        veiculo.setId(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(veiculo));
        when(clienteService.buscar(1L)).thenReturn(dono);
        when(clienteService.buscar(2L)).thenReturn(novoDono);
        when(repository.save(any(Veiculo.class))).thenAnswer(i -> i.getArgument(0));
    }

    private VeiculoForm form(long clienteId) {
        VeiculoForm f = new VeiculoForm();
        f.setId(10L);
        f.setClienteId(clienteId);
        f.setPlaca("abc-1d23");
        f.setMarca("Fiat");
        f.setModelo("Uno");
        return f;
    }

    @Test
    void naoTrocaODonoComOCarroNaOficina() {
        when(ordemRepository.existsByVeiculoIdAndStatusNotIn(eq(10L), any())).thenReturn(true);

        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.salvar(form(2L)));
        assertEquals("clienteId", e.getCampo());
    }

    @Test
    void trocaODonoQuandoOCarroNaoEstaNaOficina() {
        when(ordemRepository.existsByVeiculoIdAndStatusNotIn(anyLong(), any())).thenReturn(false);

        Veiculo salvo = service.salvar(form(2L));

        assertEquals(2L, salvo.getCliente().getId());
        assertEquals("ABC1D23", salvo.getPlaca());
    }

    @Test
    void placaInvalidaEhRecusada() {
        VeiculoForm f = form(1L);
        f.setPlaca("12345");

        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.salvar(f));
        assertEquals("placa", e.getCampo());
    }
}
