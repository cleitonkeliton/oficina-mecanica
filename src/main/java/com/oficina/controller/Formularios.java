package com.oficina.controller;

import com.oficina.service.RegraNegocioException;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/** Ajuda a mostrar um erro de regra (CPF repetido etc.) no formulário. */
final class Formularios {

    private Formularios() {}

    /** Coloca o erro ao lado do campo, se ele existir no formulário; senão, mostra no topo da página. */
    static void mostrarErro(RegraNegocioException e, BindingResult resultado, Model model) {
        Object alvo = resultado.getTarget();
        if (e.getCampo() != null && alvo != null && new BeanWrapperImpl(alvo).isReadableProperty(e.getCampo())) {
            resultado.rejectValue(e.getCampo(), "regra", e.getMessage());
        } else {
            model.addAttribute("erro", e.getMessage());
        }
    }
}
