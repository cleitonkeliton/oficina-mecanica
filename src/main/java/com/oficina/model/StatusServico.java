package com.oficina.model;

/**
 * Situação do carro dentro da oficina.
 * A "etapa" alimenta a barra de progresso que o cliente vê (1 = recebido ... 5 = entregue).
 */
public enum StatusServico {

    AGUARDANDO_AVALIACAO("Aguardando avaliação", 1),
    EM_ORCAMENTO("Em orçamento", 2),
    AGUARDANDO_APROVACAO("Aguardando aprovação do cliente", 2),
    AGUARDANDO_PECAS("Aguardando peças", 3),
    EM_SERVICO("Em manutenção", 3),
    PRONTO("Pronto para retirada", 4),
    ENTREGUE("Entregue", 5),
    CANCELADO("Cancelado", 0);

    private final String descricao;
    private final int etapa;

    StatusServico(String descricao, int etapa) {
        this.descricao = descricao;
        this.etapa = etapa;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getEtapa() {
        return etapa;
    }

    /** Entregue ou cancelado: o carro não está mais na oficina. */
    public boolean isFinalizado() {
        return this == ENTREGUE || this == CANCELADO;
    }

    /** Classe CSS do selo colorido de cada situação. */
    public String getCss() {
        return "status-" + name().toLowerCase().replace('_', '-');
    }
}
