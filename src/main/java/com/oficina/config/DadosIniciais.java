package com.oficina.config;

import com.oficina.model.Perfil;
import com.oficina.service.ConfiguracaoSiteService;
import com.oficina.service.ServicoService;
import com.oficina.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Na primeira vez que o sistema sobe com o banco vazio, cria o texto padrão do site,
 * alguns serviços de exemplo e os usuários iniciais (admin e recepção).
 */
@Component
public class DadosIniciais implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final ConfiguracaoSiteService configuracaoService;
    private final ServicoService servicoService;
    private final UsuarioService usuarioService;

    @Value("${oficina.seed.admin.login:admin}")
    private String adminLogin;
    @Value("${oficina.seed.admin.senha:admin123}")
    private String adminSenha;
    @Value("${oficina.seed.recepcao.login:recepcao}")
    private String recepcaoLogin;
    @Value("${oficina.seed.recepcao.senha:recepcao123}")
    private String recepcaoSenha;

    public DadosIniciais(ConfiguracaoSiteService configuracaoService,
                         ServicoService servicoService,
                         UsuarioService usuarioService) {
        this.configuracaoService = configuracaoService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        configuracaoService.criarPadraoSeNaoExistir();
        int antigos = servicoService.ajustarServicosAntigos();
        if (antigos > 0) {
            log.info("{} serviço(s) da versão antiga ajustado(s) para aparecer no site.", antigos);
        }
        servicoService.criarPadraoSeVazio();

        if (usuarioService.criarInicialSeNaoExistir(Perfil.ADMIN, adminLogin, adminSenha, "Administrador")) {
            log.warn("Usuário administrador criado: login '{}'. Troque a senha padrão pelo link Trocar senha no topo da página.", adminLogin);
        }
        if (usuarioService.criarInicialSeNaoExistir(Perfil.RECEPCAO, recepcaoLogin, recepcaoSenha, "Recepção")) {
            log.warn("Usuário da recepção criado: login '{}'. Troque a senha padrão depois de entrar.", recepcaoLogin);
        }
    }
}
