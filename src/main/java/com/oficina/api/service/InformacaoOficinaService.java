package com.oficina.api.service;

import com.oficina.api.model.InformacaoOficina;
import com.oficina.api.repository.InformacaoOficinaRepository;
import org.springframework.stereotype.Service;

@Service
public class InformacaoOficinaService {

    private final InformacaoOficinaRepository repository;

    public InformacaoOficinaService(InformacaoOficinaRepository repository) {
        this.repository = repository;
    }

    // Retorna as informações salvas ou um padrão para a primeira execução
    public InformacaoOficina obter() {
        return repository.findById(InformacaoOficina.ID_UNICO).orElseGet(this::padrao);
    }

    public InformacaoOficina salvar(InformacaoOficina info) {
        info.setId(InformacaoOficina.ID_UNICO);
        return repository.save(info);
    }

    private InformacaoOficina padrao() {
        InformacaoOficina info = new InformacaoOficina();
        info.setNome("Oficina Mecânica");
        info.setSlogan("Cuidando do seu veículo com confiança");
        info.setSobre("Edite estas informações na área administrativa.");
        info.setHorarioFuncionamento("Segunda a sexta, 8h às 18h. Sábado, 8h às 12h.");
        return info;
    }
}
