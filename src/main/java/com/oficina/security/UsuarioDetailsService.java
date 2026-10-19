package com.oficina.security;

import com.oficina.model.Usuario;
import com.oficina.repository.UsuarioRepository;
import com.oficina.util.Documentos;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final TentativasLogin tentativas;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository, TentativasLogin tentativas) {
        this.usuarioRepository = usuarioRepository;
        this.tentativas = tentativas;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String chave = login == null ? "" : login.trim();
        Optional<Usuario> usuario;
        if (chave.matches("[\\d.\\-\\s]+")) {
            // Parece CPF (o cliente pode digitar com pontos e traço): procura só pelos dígitos
            usuario = usuarioRepository.findByLogin(Documentos.somenteDigitos(chave));
        } else {
            usuario = usuarioRepository.findByLogin(chave);
        }
        return usuario
                .map(u -> new UsuarioAutenticado(u, tentativas.bloqueado(chave)))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
