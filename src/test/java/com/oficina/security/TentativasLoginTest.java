package com.oficina.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TentativasLoginTest {

    @Test
    void bloqueiaDepoisDeErrosSeguidosELiberaAoAcertar() {
        TentativasLogin t = new TentativasLogin();
        for (int i = 1; i < TentativasLogin.MAXIMO_ERROS; i++) {
            t.registrarErro("admin");
        }
        assertFalse(t.bloqueado("admin"));

        t.registrarErro("ADMIN");
        assertTrue(t.bloqueado("admin"), "maiúsculas contam como o mesmo login");

        t.limpar("admin");
        assertFalse(t.bloqueado("admin"));
    }

    @Test
    void cpfComOuSemPontuacaoEhOMesmoLogin() {
        TentativasLogin t = new TentativasLogin();
        for (int i = 0; i < TentativasLogin.MAXIMO_ERROS; i++) {
            t.registrarErro(i % 2 == 0 ? "529.982.247-25" : "52998224725");
        }
        assertTrue(t.bloqueado("52998224725"));
        assertFalse(t.bloqueado("11144477735"));
    }
}
