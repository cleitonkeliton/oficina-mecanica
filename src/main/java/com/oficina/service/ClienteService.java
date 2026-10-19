package com.oficina.service;

import com.oficina.form.ClienteForm;
import com.oficina.model.Cliente;
import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.OrdemServicoRepository;
import com.oficina.repository.UsuarioRepository;
import com.oficina.repository.VeiculoRepository;
import com.oficina.util.Documentos;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.oficina.util.Textos.limpar;

@Service
public class ClienteService {

    public static final int TAMANHO_MINIMO_SENHA = 6;

    private final ClienteRepository repository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository repository,
                          VeiculoRepository veiculoRepository,
                          OrdemServicoRepository ordemRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
        this.ordemRepository = ordemRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(String termo) {
        String t = termo == null ? "" : termo.trim();
        if (t.isEmpty()) {
            return repository.findAllByOrderByNomeAsc();
        }
        return repository.pesquisar(t, Documentos.somenteDigitos(t));
    }

    @Transactional(readOnly = true)
    public long contar() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
    }

    @Transactional
    public Cliente salvar(ClienteForm form) {
        String cpf = Documentos.somenteDigitos(form.getCpf());
        if (!Documentos.cpfValido(cpf)) {
            throw new RegraNegocioException("cpf", "CPF inválido");
        }
        boolean cpfRepetido = form.getId() == null
                ? repository.existsByCpf(cpf)
                : repository.existsByCpfAndIdNot(cpf, form.getId());
        if (cpfRepetido) {
            throw new RegraNegocioException("cpf", "Já existe um cliente com este CPF");
        }
        String senha = limpar(form.getSenhaAcesso());
        if (senha != null && senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("senhaAcesso",
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres");
        }

        Cliente cliente = form.getId() == null ? new Cliente() : buscar(form.getId());
        cliente.setNome(limpar(form.getNome()));
        cliente.setCpf(cpf);
        cliente.setTelefone(limpar(form.getTelefone()));
        cliente.setEmail(limpar(form.getEmail()));
        cliente = repository.save(cliente);

        // O login do cliente é o CPF: se o CPF ou o nome mudou, atualiza o acesso também
        Optional<Usuario> acesso = usuarioRepository.findByClienteId(cliente.getId());
        if (acesso.isPresent()) {
            Usuario usuario = acesso.get();
            if (!usuario.getLogin().equals(cpf)) {
                verificarLoginLivre(cpf);
                usuario.setLogin(cpf);
            }
            usuario.setNome(cliente.getNome());
        }
        if (senha != null) {
            definirAcesso(cliente, senha);
        }
        return cliente;
    }

    /** Cria (ou troca a senha do) acesso do cliente à área de acompanhamento. Login = CPF. */
    @Transactional
    public void definirAcesso(Cliente cliente, String senha) {
        if (senha == null || senha.trim().length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraNegocioException("senhaAcesso",
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres");
        }
        Usuario usuario = usuarioRepository.findByClienteId(cliente.getId()).orElseGet(() -> {
            verificarLoginLivre(cliente.getCpf());
            Usuario novo = new Usuario(cliente.getCpf(), null, cliente.getNome(), Perfil.CLIENTE);
            novo.setCliente(cliente);
            return novo;
        });
        usuario.setSenhaHash(passwordEncoder.encode(senha.trim()));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerAcesso(Long clienteId) {
        usuarioRepository.findByClienteId(clienteId).ifPresent(usuarioRepository::delete);
    }

    @Transactional(readOnly = true)
    public boolean possuiAcesso(Long clienteId) {
        return usuarioRepository.findByClienteId(clienteId).map(Usuario::isAtivo).orElse(false);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        if (veiculoRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Este cliente tem veículos cadastrados. Exclua ou transfira os veículos antes.");
        }
        if (ordemRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Este cliente tem ordens de serviço no histórico e não pode ser excluído.");
        }
        removerAcesso(id);
        repository.delete(cliente);
    }

    private void verificarLoginLivre(String login) {
        if (usuarioRepository.existsByLogin(login)) {
            throw new RegraNegocioException("cpf", "Já existe um usuário do sistema com este login (" + login + ")");
        }
    }
}
