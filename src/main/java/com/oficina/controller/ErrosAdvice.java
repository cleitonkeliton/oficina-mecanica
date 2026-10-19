package com.oficina.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Erros raros que acontecem quando duas pessoas salvam a mesma coisa ao mesmo tempo.
 * Em vez da página de erro, volta para a tela com um aviso.
 */
@ControllerAdvice
public class ErrosAdvice {

    private static final Pattern ORDEM = Pattern.compile("^(/recepcao/ordens/\\d+)(/.*)?$");

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    public String alteradaAoMesmoTempo(HttpServletRequest request) {
        return voltarComErro(request, "Outra pessoa alterou estes dados ao mesmo tempo. Confira como ficou e tente de novo.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String registroRepetido(HttpServletRequest request) {
        return voltarComErro(request, "Não foi possível salvar: já existe um cadastro com estes dados (CPF, placa ou login repetido).");
    }

    private String voltarComErro(HttpServletRequest request, String mensagem) {
        RequestContextUtils.getOutputFlashMap(request).put("erro", mensagem);
        return "redirect:" + destino(request);
    }

    /** Volta para a ordem que estava aberta ou para a página de onde o formulário veio (só do próprio site). */
    private static String destino(HttpServletRequest request) {
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        Matcher ordem = ORDEM.matcher(caminho);
        if (ordem.matches()) {
            return ordem.group(1);
        }
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                if (request.getServerName().equalsIgnoreCase(uri.getHost()) && uri.getRawPath() != null
                        && uri.getRawPath().startsWith(request.getContextPath() + "/")) {
                    String path = uri.getRawPath().substring(request.getContextPath().length());
                    return path + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
                }
            } catch (IllegalArgumentException ignorado) {
                // Referer malformado: cai no padrão abaixo
            }
        }
        return "/";
    }
}
