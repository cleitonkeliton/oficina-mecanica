package com.oficina.util;

import java.security.SecureRandom;
import java.util.Locale;

public final class Codigos {

    // Sem letras/números parecidos (0/O, 1/I) para facilitar a digitação pelo cliente
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Codigos() {}

    public static String gerar(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        return sb.toString();
    }

    // "abc-1d23" -> "ABC1D23"
    public static String normalizarPlaca(String placa) {
        return placa == null ? "" : placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }
}
