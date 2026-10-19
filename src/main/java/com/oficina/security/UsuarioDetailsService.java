package com.oficina.security;

import com.oficina.repository.UsuarioRepository;
import com.oficina.util.Documentos;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String chave = login == null ? "" : login.trim();
        return usuarioRepository.findByLogin(chave)
                // O cliente pode digitar o CPF com pontos e traço
                .or(() -> chave.matches("[\\d.\\-\\s]+")
                        ? usuarioRepository.findByLogin(Documentos.somenteDigitos(chave))
                        : java.util.Optional.empty())
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
