package com.oficina.service;

import com.oficina.form.UsuarioForm;
import com.oficina.model.Perfil;
import com.oficina.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UsuarioServiceTest {

    private UsuarioService service;

    @BeforeEach
    void preparar() {
        service = new UsuarioService(mock(UsuarioRepository.class), new BCryptPasswordEncoder(4));
    }

    private UsuarioForm form(String login, String senha) {
        UsuarioForm f = new UsuarioForm();
        f.setNome("Teste");
        f.setLogin(login);
        f.setSenha(senha);
        f.setPerfil(Perfil.RECEPCAO);
        return f;
    }

    @Test
    void loginDaEquipeNaoPodeParecerCpf() {
        assertThrows(RegraNegocioException.class, () -> service.criarEquipe(form("52998224725", "segredo1")));
        assertThrows(RegraNegocioException.class, () -> service.criarEquipe(form("529.982.247-25", "segredo1")));
    }

    @Test
    void senhaComMaisDe72BytesEhRecusadaSemErro500() {
        String senha = "ç".repeat(40); // 40 caracteres, 80 bytes
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.criarEquipe(form("ana", senha)));
        assertEquals("senha", e.getCampo());
    }

    @Test
    void naoCriaClientePelaTelaDaEquipe() {
        UsuarioForm f = form("cliente1", "segredo1");
        f.setPerfil(Perfil.CLIENTE);
        assertThrows(RegraNegocioException.class, () -> service.criarEquipe(f));
    }
}
