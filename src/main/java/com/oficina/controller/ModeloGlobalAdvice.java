package com.oficina.controller;

import com.oficina.model.ConfiguracaoSite;
import com.oficina.service.ConfiguracaoSiteService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

/** Deixa as informações do site (nome, contatos...) disponíveis em todas as páginas. */
@ControllerAdvice
public class ModeloGlobalAdvice {

    private final ConfiguracaoSiteService configuracaoService;

    public ModeloGlobalAdvice(ConfiguracaoSiteService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @ModelAttribute("site")
    public ConfiguracaoSite site() {
        return configuracaoService.obter();
    }

    @ModelAttribute("anoAtual")
    public int anoAtual() {
        return Year.now().getValue();
    }
}
