package com.oficina;

import com.oficina.api.model.Servico;
import com.oficina.api.repository.InformacaoOficinaRepository;
import com.oficina.api.repository.OrdemServicoRepository;
import com.oficina.api.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
// Arquivo de propriedades com prioridade sobre o application.properties da raiz (que aponta para o MySQL)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class SiteAdminTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private InformacaoOficinaRepository informacaoRepository;

    @Autowired
    private OrdemServicoRepository ordemRepository;

    @BeforeEach
    void limpar() {
        ordemRepository.deleteAll();
        servicoRepository.deleteAll();
        informacaoRepository.deleteAll();
    }

    @Test
    void paginaPrincipalMostraServicosVisiveis() throws Exception {
        Servico visivel = new Servico();
        visivel.setNome("Troca de óleo");
        visivel.setPreco(120.5);
        servicoRepository.save(visivel);

        Servico oculto = new Servico();
        oculto.setNome("Serviço escondido");
        oculto.setAtivo(false);
        servicoRepository.save(oculto);

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Troca de óleo")))
                .andExpect(content().string(containsString("R$ 120,50")))
                .andExpect(content().string(not(containsString("Serviço escondido"))));
    }

    @Test
    void adminExigeLogin() throws Exception {
        // Navegador (Accept: text/html) vai para a tela de login
        mvc.perform(get("/admin").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginComSenhaConfigurada() throws Exception {
        mvc.perform(formLogin("/login").user("admin").password("senha-teste"))
                .andExpect(redirectedUrl("/admin"));
        mvc.perform(formLogin("/login").user("admin").password("errada"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser
    void adminAdicionaServicoEAtualizaInformacoes() throws Exception {
        mvc.perform(post("/admin/servicos").with(csrf())
                        .param("nome", "Alinhamento")
                        .param("preco", "80")
                        .param("descricao", "Alinhamento 3D")
                        .param("ativo", "true"))
                .andExpect(redirectedUrl("/admin"));

        mvc.perform(post("/admin/informacoes").with(csrf())
                        .param("nome", "Auto Center Valdevino")
                        .param("telefone", "(11) 3333-4444")
                        .param("whatsapp", "55 11 99999-8888"))
                .andExpect(redirectedUrl("/admin"));

        mvc.perform(get("/"))
                .andExpect(content().string(containsString("Alinhamento")))
                .andExpect(content().string(containsString("R$ 80,00")))
                .andExpect(content().string(containsString("Auto Center Valdevino")))
                .andExpect(content().string(containsString("https://wa.me/5511999998888")));

        mvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alinhamento")));
    }

    @Test
    @WithMockUser
    void adminEditaEExcluiServico() throws Exception {
        Servico s = new Servico();
        s.setNome("Balanceamento");
        s = servicoRepository.save(s);

        mvc.perform(post("/admin/servicos").with(csrf())
                        .param("id", s.getId().toString())
                        .param("nome", "Balanceamento das 4 rodas")
                        .param("_ativo", "on"))
                .andExpect(redirectedUrl("/admin"));

        Servico editado = servicoRepository.findById(s.getId()).orElseThrow();
        assertThat(editado.getNome()).isEqualTo("Balanceamento das 4 rodas");
        assertThat(editado.getAtivo()).isFalse();

        mvc.perform(post("/admin/servicos/" + s.getId() + "/excluir").with(csrf()))
                .andExpect(redirectedUrl("/admin"));
        assertThat(servicoRepository.findById(s.getId())).isEmpty();
    }

    @Test
    void apiDeServicosSoAlteraComLogin() throws Exception {
        mvc.perform(get("/api/servicos")).andExpect(status().isOk());
        mvc.perform(post("/api/servicos").contentType("application/json").content("{\"nome\":\"X\"}"))
                .andExpect(status().isUnauthorized());
    }
}
