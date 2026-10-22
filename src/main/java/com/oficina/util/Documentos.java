package com.oficina.util;

import java.util.Locale;

/** Funções para CPF e placa: limpar o que foi digitado, validar e formatar para exibição. */
public final class Documentos {

    private Documentos() {}

    /** "123.456.789-09" vira "12345678909". */
    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }
        return digitoCpf(d, 9) == d.charAt(9) - '0' && digitoCpf(d, 10) == d.charAt(10) - '0';
    }

    /** Calcula o dígito verificador usando as primeiras "tamanho" posições. */
    private static int digitoCpf(String d, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (d.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    public static String formatarCpf(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11) {
            return cpf;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    /** "abc-1d23" vira "ABC1D23". */
    public static String normalizarPlaca(String placa) {
        return placa == null ? "" : placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    /** Aceita o padrão antigo (ABC1234) e o Mercosul (ABC1D23). */
    public static boolean placaValida(String placa) {
        return normalizarPlaca(placa).matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}");
    }

    /** Padrão antigo ganha traço (ABC-1234); Mercosul fica sem (ABC1D23). */
    public static String formatarPlaca(String placa) {
        String p = normalizarPlaca(placa);
        if (p.matches("[A-Z]{3}[0-9]{4}")) {
            return p.substring(0, 3) + "-" + p.substring(3);
        }
        return p;
    }
}
