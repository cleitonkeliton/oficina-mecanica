package com.oficina.service;

import com.oficina.form.UsuarioForm;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import com.oficina.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Administradores e recepcionistas (clientes ficam no cadastro de clientes). */
    @Transactional(readOnly = true)
    public List<Usuario> listarEquipe() {
        return repository.findByPerfilInOrderByNomeAsc(List.of(Perfil.ADMIN, Perfil.RECEPCAO));
    }

    @Transactional
    public Usuario criarEquipe(UsuarioForm form) {
        if (form.getPerfil() == Perfil.CLIENTE) {
            throw new RegraNegocioException("perfil", "Clientes recebem acesso pelo cadastro de clientes");
        }
        String login = form.getLogin().trim().toLowerCase(Locale.ROOT);
        if (!login.matches("[a-z0-9._-]{3,60}")) {
            throw new RegraNegocioException("login", "Use só letras sem acento, números, ponto, hífen ou sublinhado");
        }
        if (login.matches("\\d+")) {
            throw new RegraNegocioException("login", "O login da equipe não pode ter só números (fica parecido com CPF)");
        }
        if (repository.existsByLogin(login)) {
            throw new RegraNegocioException("login", "Este login já está em uso");
        }
        Usuario usuario = new Usuario(login, passwordEncoder.encode(form.getSenha()), form.getNome().trim(), form.getPerfil());
        return repository.save(usuario);
    }

    @Transactional
    public void alternarAtivo(Long id, Long idLogado) {
        Usuario usuario = buscarEquipe(id);
        if (usuario.getId().equals(idLogado)) {
            throw new RegraNegocioException("Você não pode desativar o seu próprio usuário.");
        }
        if (usuario.isAtivo() && usuario.getPerfil() == Perfil.ADMIN
                && repository.countByPerfilAndAtivoTrue(Perfil.ADMIN) <= 1) {
            throw new RegraNegocioException("É preciso ter pelo menos um administrador ativo.");
        }
        usuario.setAtivo(!usuario.isAtivo());
    }

    @Transactional
    public void redefinirSenha(Long id, String novaSenha) {
        validarSenha(novaSenha);
        Usuario usuario = buscarEquipe(id);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
    }

    /** Qualquer usuário logado troca a própria senha informando a atual. */
    @Transactional
    public void alterarPropriaSenha(Long idLogado, String senhaAtual, String novaSenha, String confirmacao) {
        Usuario usuario = repository.findById(idLogado)
                .orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new RegraNegocioException("senhaAtual", "Senha atual incorreta");
        }
        if (!novaSenha.equals(confirmacao)) {
            throw new RegraNegocioException("confirmacao", "A confirmação não confere com a nova senha");
        }
        validarSenha(novaSenha);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
    }

    /** Cria o usuário inicial, se ainda não existir ninguém com aquele perfil. Retorna true se criou. */
    @Transactional
    public boolean criarInicialSeNaoExistir(Perfil perfil, String login, String senha, String nome) {
        if (repository.existsByPerfil(perfil) || repository.existsByLogin(login)) {
            return false;
        }
        repository.save(new Usuario(login, passwordEncoder.encode(senha), nome, perfil));
        return true;
    }

    private Usuario buscarEquipe(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
        if (usuario.getPerfil() == Perfil.CLIENTE) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
        return usuario;
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6 || senha.length() > 60) {
            throw new RegraNegocioException("novaSenha", "A senha deve ter de 6 a 60 caracteres");
        }
    }
}
