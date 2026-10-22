package com.oficina.service;

import com.oficina.model.ConfiguracaoSite;
import com.oficina.repository.ConfiguracaoSiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.oficina.util.Textos.limpar;

@Service
public class ConfiguracaoSiteService {

    private final ConfiguracaoSiteRepository repository;

    public ConfiguracaoSiteService(ConfiguracaoSiteRepository repository) {
        this.repository = repository;
    }

    /** Informações atuais do site (ou o texto padrão, se ainda não foram salvas). */
    @Transactional(readOnly = true)
    public ConfiguracaoSite obter() {
        return repository.findFirstByOrderByIdAsc().orElseGet(ConfiguracaoSiteService::padrao);
    }

    @Transactional
    public ConfiguracaoSite salvar(ConfiguracaoSite dados) {
        ConfiguracaoSite atual = repository.findFirstByOrderByIdAsc().orElseGet(ConfiguracaoSite::new);
        atual.setNomeOficina(limpar(dados.getNomeOficina()));
        atual.setSlogan(limpar(dados.getSlogan()));
        atual.setMensagemDestaque(limpar(dados.getMensagemDestaque()));
        atual.setSobre(limpar(dados.getSobre()));
        atual.setDiferenciais(limpar(dados.getDiferenciais()));
        atual.setHorarioFuncionamento(limpar(dados.getHorarioFuncionamento()));
        atual.setFormasPagamento(limpar(dados.getFormasPagamento()));
        atual.setEndereco(limpar(dados.getEndereco()));
        atual.setTelefone(limpar(dados.getTelefone()));
        atual.setWhatsapp(limpar(dados.getWhatsapp()));
        atual.setEmail(limpar(dados.getEmail()));
        atual.setInstagram(limpar(dados.getInstagram()));
        atual.setFacebook(limpar(dados.getFacebook()));
        atual.setLinkMapa(limpar(dados.getLinkMapa()));
        return repository.save(atual);
    }

    @Transactional
    public void criarPadraoSeNaoExistir() {
        if (repository.count() == 0) {
            repository.save(padrao());
        }
    }

    /** Texto inicial do site. O administrador troca tudo em "Informações do site". */
    public static ConfiguracaoSite padrao() {
        ConfiguracaoSite c = new ConfiguracaoSite();
        c.setNomeOficina("Oficina Valdevino");
        c.setSlogan("Mecânica de confiança para o seu carro");
        c.setMensagemDestaque("Orçamento sem compromisso e acompanhamento do seu carro pela internet.");
        c.setSobre("""
                Somos uma oficina mecânica com anos de experiência cuidando de carros nacionais e importados.
                Trabalhamos com transparência: você aprova o orçamento antes de qualquer serviço e acompanha a situação do seu carro pelo nosso site.""");
        c.setDiferenciais("""
                Orçamento sem compromisso
                Acompanhamento do carro pela internet
                Garantia de 90 dias nos serviços
                Peças de qualidade com nota fiscal
                Equipe experiente em carros nacionais e importados""");
        c.setHorarioFuncionamento("""
                Segunda a sexta: 8h às 18h
                Sábado: 8h às 12h
                Domingo e feriados: fechado""");
        c.setFormasPagamento("Dinheiro, Pix, cartão de débito e crédito");
        c.setEndereco("Rua Exemplo, 123 - Centro");
        c.setTelefone("(11) 3333-4444");
        c.setWhatsapp("(11) 99999-8888");
        c.setEmail("contato@oficina.com.br");
        return c;
    }
}
