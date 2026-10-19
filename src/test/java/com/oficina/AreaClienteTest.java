package com.oficina;

import com.oficina.api.model.Cliente;
import com.oficina.api.model.OrdemServico;
import com.oficina.api.model.Servico;
import com.oficina.api.model.Veiculo;
import com.oficina.api.repository.ClienteRepository;
import com.oficina.api.repository.OrdemServicoRepository;
import com.oficina.api.repository.ServicoRepository;
import com.oficina.api.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AreaClienteTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private OrdemServicoRepository ordemRepository;

    @BeforeEach
    void limpar() {
        ordemRepository.deleteAll();
        veiculoRepository.deleteAll();
        clienteRepository.deleteAll();
        servicoRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void adminCadastraClienteComCarro() throws Exception {
        mvc.perform(post("/admin/clientes").with(csrf())
                        .param("nome", "Maria Souza")
                        .param("telefone", "(11) 98888-7777")
                        .param("placa", "abc-1d23")
                        .param("marca", "Fiat")
                        .param("modelo", "Uno"))
                .andExpect(redirectedUrl("/admin/clientes"));

        Cliente cliente = clienteRepository.findAll().get(0);
        assertThat(cliente.getCodigoAcesso()).hasSize(8);
        Veiculo veiculo = veiculoRepository.findByClienteId(cliente.getId()).get(0);
        assertThat(veiculo.getPlaca()).isEqualTo("ABC1D23");

        // Placa repetida é recusada
        mvc.perform(post("/admin/clientes").with(csrf())
                        .param("nome", "Outro")
                        .param("placa", "ABC1D23"))
                .andExpect(flash().attribute("erro", "Já existe um veículo com essa placa."));

        mvc.perform(get("/admin/clientes"))
                .andExpect(content().string(containsString("Maria Souza")))
                .andExpect(content().string(containsString(cliente.getCodigoAcesso())))
                .andExpect(content().string(containsString("https://wa.me/5511988887777")));
    }

    @Test
    void clienteEntraEVeSituacaoEServicosFeitos() throws Exception {
        Cliente cliente = clienteRepository.save(new Cliente("João", null, "11977776666"));
        Veiculo carro = veiculoRepository.save(new Veiculo("VW", "Gol", "XYZ9A87", cliente));

        Servico oleo = new Servico();
        oleo.setNome("Troca de óleo");
        oleo.setPreco(150.0);
        oleo = servicoRepository.save(oleo);
        Servico freio = new Servico();
        freio.setNome("Pastilhas de freio");
        freio.setPreco(200.0);
        freio = servicoRepository.save(freio);

        OrdemServico antiga = new OrdemServico();
        antiga.setCliente(cliente);
        antiga.setVeiculo(carro);
        antiga.setServicos(List.of(oleo));
        antiga.setStatus("Entregue");
        ordemRepository.save(antiga);

        OrdemServico atual = new OrdemServico();
        atual.setCliente(cliente);
        atual.setVeiculo(carro);
        atual.setServicos(List.of(freio));
        atual.setStatus("Em serviço");
        atual.setObservacoes("Aguardando peça");
        ordemRepository.save(atual);

        // Sem login vê a tela de entrada
        mvc.perform(get("/cliente")).andExpect(content().string(containsString("Código de acesso")));

        // Código errado
        mvc.perform(post("/cliente/entrar").with(csrf()).param("placa", "XYZ9A87").param("codigo", "ERRADO12"))
                .andExpect(flash().attributeExists("erro"));

        // Placa de outro cliente com o código certo também não entra
        mvc.perform(post("/cliente/entrar").with(csrf()).param("placa", "AAA0000").param("codigo", cliente.getCodigoAcesso()))
                .andExpect(flash().attributeExists("erro"));

        MockHttpSession sessao = new MockHttpSession();
        mvc.perform(post("/cliente/entrar").with(csrf()).session(sessao)
                        .param("placa", "xyz-9a87")
                        .param("codigo", cliente.getCodigoAcesso().toLowerCase()))
                .andExpect(redirectedUrl("/cliente"))
                .andExpect(flash().attributeCount(0));

        mvc.perform(get("/cliente").session(sessao))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Olá, João")))
                .andExpect(content().string(containsString("Em serviço")))
                .andExpect(content().string(containsString("Aguardando peça")))
                .andExpect(content().string(containsString("Pastilhas de freio")))
                .andExpect(content().string(containsString("Troca de óleo")))
                .andExpect(content().string(containsString("R$ 150,00")));

        mvc.perform(post("/cliente/sair").with(csrf()).session(sessao));
        mvc.perform(get("/cliente").session(sessao))
                .andExpect(content().string(not(containsString("Olá, João"))));
    }

    @Test
    void linkDeAcessoEntraDireto() throws Exception {
        Cliente cliente = clienteRepository.save(new Cliente("Ana", null, null));
        veiculoRepository.save(new Veiculo("Ford", "Ka", "KKK1A11", cliente));

        MockHttpSession sessao = new MockHttpSession();
        mvc.perform(get("/cliente/entrar").session(sessao)
                        .param("placa", "KKK1A11").param("codigo", cliente.getCodigoAcesso()))
                .andExpect(redirectedUrl("/cliente"));
        mvc.perform(get("/cliente").session(sessao))
                .andExpect(content().string(containsString("Olá, Ana")));
    }

    @Test
    @WithMockUser
    void adminCriaEAtualizaOrdem() throws Exception {
        Cliente cliente = clienteRepository.save(new Cliente("Pedro", null, null));
        Veiculo carro = veiculoRepository.save(new Veiculo("GM", "Onix", "ONX2B22", cliente));
        Servico alinhamento = new Servico();
        alinhamento.setNome("Alinhamento");
        alinhamento.setPreco(80.0);
        alinhamento = servicoRepository.save(alinhamento);

        mvc.perform(post("/admin/ordens").with(csrf())
                        .param("veiculoId", carro.getId().toString())
                        .param("servicoIds", alinhamento.getId().toString())
                        .param("status", "Em orçamento"))
                .andExpect(redirectedUrl("/admin/ordens"));

        OrdemServico ordem = ordemRepository.findAll().get(0);
        assertThat(ordem.getCliente().getId()).isEqualTo(cliente.getId());
        assertThat(ordem.getValorTotal()).isEqualByComparingTo("80");

        mvc.perform(post("/admin/ordens").with(csrf())
                        .param("id", ordem.getId().toString())
                        .param("veiculoId", carro.getId().toString())
                        .param("status", "Pronto para retirada"))
                .andExpect(redirectedUrl("/admin/ordens"));
        assertThat(ordemRepository.findById(ordem.getId()).orElseThrow().getStatus()).isEqualTo("Pronto para retirada");

        mvc.perform(get("/admin/ordens"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ONX2B22")));
    }

    @Test
    void dadosDeClientesNaApiExigemLogin() throws Exception {
        mvc.perform(get("/api/clientes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/ordens-servico")).andExpect(status().isUnauthorized());
    }
}
