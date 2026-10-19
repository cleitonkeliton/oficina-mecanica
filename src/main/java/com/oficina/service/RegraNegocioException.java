package com.oficina.service;

/**
 * Erro de regra do sistema (CPF repetido, placa inválida...). A mensagem é mostrada para o usuário.
 * Quando "campo" vem preenchido, a tela mostra o erro ao lado daquele campo do formulário.
 */
public class RegraNegocioException extends RuntimeException {

    private final String campo;

    public RegraNegocioException(String mensagem) {
        this(null, mensagem);
    }

    public RegraNegocioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
