package com.oficina.model;

public enum Perfil {

    ADMIN("Administrador"),
    RECEPCAO("Recepção"),
    CLIENTE("Cliente");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Nome usado pelo Spring Security (hasRole("ADMIN") procura "ROLE_ADMIN"). */
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
