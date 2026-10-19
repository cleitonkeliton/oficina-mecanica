package com.oficina.security;

import com.oficina.util.Documentos;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bloqueia por alguns minutos um login que errou a senha várias vezes seguidas,
 * para dificultar quem tenta adivinhar senhas.
 */
@Component
public class TentativasLogin {

    public static final int MAXIMO_ERROS = 5;
    public static final Duration TEMPO_BLOQUEIO = Duration.ofMinutes(15);
    private static final int LIMITE_REGISTROS = 10_000;

    private record Registro(int erros, Instant bloqueadoAte) {}

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    /** CPF com ou sem pontuação e maiúsculas/minúsculas contam como o mesmo login. */
    public static String chave(String login) {
        String l = login == null ? "" : login.trim();
        if (l.matches("[\\d.\\-\\s]+")) {
            return Documentos.somenteDigitos(l);
        }
        return l.toLowerCase(Locale.ROOT);
    }

    public boolean bloqueado(String login) {
        Registro r = registros.get(chave(login));
        return r != null && r.bloqueadoAte() != null && r.bloqueadoAte().isAfter(Instant.now());
    }

    public void registrarErro(String login) {
        if (registros.size() > LIMITE_REGISTROS) {
            Instant agora = Instant.now();
            registros.values().removeIf(r -> r.bloqueadoAte() == null || r.bloqueadoAte().isBefore(agora));
        }
        registros.compute(chave(login), (k, r) -> {
            if (r != null && r.bloqueadoAte() != null && r.bloqueadoAte().isBefore(Instant.now())) {
                r = null; // o bloqueio anterior já venceu: recomeça a contagem
            }
            int erros = (r == null ? 0 : r.erros()) + 1;
            return new Registro(erros, erros >= MAXIMO_ERROS ? Instant.now().plus(TEMPO_BLOQUEIO) : null);
        });
    }

    public void limpar(String login) {
        registros.remove(chave(login));
    }
}
