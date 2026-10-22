package com.oficina.util;

public final class Textos {

    private Textos() {}

    /** Tira espaços das pontas; texto vazio vira null (assim o banco não guarda ""). */
    public static String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String t = valor.trim();
        return t.isEmpty() ? null : t;
    }
}
