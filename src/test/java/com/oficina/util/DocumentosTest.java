package com.oficina.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentosTest {

    @Test
    void aceitaCpfValidoComOuSemPontuacao() {
        assertTrue(Documentos.cpfValido("529.982.247-25"));
        assertTrue(Documentos.cpfValido("52998224725"));
    }

    @Test
    void recusaCpfInvalido() {
        assertFalse(Documentos.cpfValido("529.982.247-26"));
        assertFalse(Documentos.cpfValido("111.111.111-11"));
        assertFalse(Documentos.cpfValido("123"));
        assertFalse(Documentos.cpfValido(null));
    }

    @Test
    void formataCpf() {
        assertEquals("529.982.247-25", Documentos.formatarCpf("52998224725"));
    }

    @Test
    void normalizaEValidaPlacasNosDoisPadroes() {
        assertEquals("ABC1234", Documentos.normalizarPlaca("abc-1234"));
        assertEquals("ABC1D23", Documentos.normalizarPlaca(" abc 1d23 "));
        assertTrue(Documentos.placaValida("ABC-1234"));
        assertTrue(Documentos.placaValida("abc1d23"));
        assertFalse(Documentos.placaValida("AB1234"));
        assertFalse(Documentos.placaValida("ABCD123"));
        assertFalse(Documentos.placaValida(null));
    }

    @Test
    void formataPlaca() {
        assertEquals("ABC-1234", Documentos.formatarPlaca("abc1234"));
        assertEquals("ABC1D23", Documentos.formatarPlaca("abc1d23"));
    }
}
