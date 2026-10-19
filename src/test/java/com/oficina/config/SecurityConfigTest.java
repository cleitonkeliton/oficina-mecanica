package com.oficina.config;

import com.oficina.service.ClienteService;
import com.oficina.service.ConfiguracaoSiteService;
import com.oficina.service.OrdemServicoService;
import com.oficina.service.ServicoService;
import com.oficina.service.UsuarioService;
import com.oficina.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Confere quem pode abrir cada área e para onde cada perfil vai depois do login. */
@WebMvcTest
@Import({SecurityConfig.class, SecurityConfigTest.Usuarios.class})
class SecurityConfigTest {

    @TestConfiguration
    static class Usuarios {
        @Bean
        UserDetailsService usuariosDeTeste(PasswordEncoder encoder) {
            return new InMemoryUserDetailsManager(
                    User.withUsername("admin").password(encoder.encode("senha123")).roles("ADMIN").build(),
                    User.withUsername("recepcao").password(encoder.encode("senha123")).roles("RECEPCAO").build(),
                    User.withUsername("52998224725").password(encoder.encode("senha123")).roles("CLIENTE").build());
        }
    }

    @Autowired
    private MockMvc mvc;

    @MockitoBean private ConfiguracaoSiteService configuracaoSiteService;
    @MockitoBean private ServicoService servicoService;
    @MockitoBean private ClienteService clienteService;
    @MockitoBean private VeiculoService veiculoService;
    @MockitoBean private OrdemServicoService ordemServicoService;
    @MockitoBean private UsuarioService usuarioService;

    @BeforeEach
    void preparar() {
        when(configuracaoSiteService.obter()).thenReturn(ConfiguracaoSiteService.padrao());
        when(servicoService.listarAtivos()).thenReturn(List.of());
    }

    @Test
    void siteELoginsSaoPublicos() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/area-cliente/login")).andExpect(status().isOk());
        mvc.perform(get("/css/style.css")).andExpect(status().isOk());
    }

    @Test
    void semLoginVaiParaALoginCerta() throws Exception {
        mvc.perform(get("/admin")).andExpect(redirectedUrl("/login"));
        mvc.perform(get("/recepcao/clientes")).andExpect(redirectedUrl("/login"));
        mvc.perform(get("/area-cliente")).andExpect(redirectedUrl("/area-cliente/login"));
    }

    @Test
    @WithMockUser(roles = "RECEPCAO")
    void recepcaoNaoAbreAdministracao() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
        mvc.perform(get("/admin/site")).andExpect(status().isForbidden());
        mvc.perform(get("/area-cliente")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoAbreAreasDaEquipe() throws Exception {
        mvc.perform(get("/recepcao")).andExpect(status().isForbidden());
        mvc.perform(get("/admin/servicos")).andExpect(status().isForbidden());
    }

    @Test
    void cadaPerfilEntraNaSuaArea() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login", "admin").param("senha", "senha123"))
                .andExpect(redirectedUrl("/admin"));
        mvc.perform(post("/login").with(csrf()).param("login", "recepcao").param("senha", "senha123"))
                .andExpect(redirectedUrl("/recepcao"));
        mvc.perform(post("/login").with(csrf()).param("login", "52998224725").param("senha", "senha123"))
                .andExpect(redirectedUrl("/area-cliente"));
    }

    @Test
    void erroDeLoginVoltaParaAPaginaDeOrigem() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login", "admin").param("senha", "errada"))
                .andExpect(redirectedUrl("/login?erro"));
        mvc.perform(post("/login").with(csrf()).param("login", "52998224725").param("senha", "errada")
                        .param("origem", "cliente"))
                .andExpect(redirectedUrl("/area-cliente/login?erro"));
    }

    @Test
    void loginSemTokenCsrfEhRecusado() throws Exception {
        mvc.perform(post("/login").param("login", "admin").param("senha", "senha123"))
                .andExpect(status().isForbidden());
    }
}
