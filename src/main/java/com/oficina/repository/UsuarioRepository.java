package com.oficina.repository;

import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    Optional<Usuario> findByClienteId(Long clienteId);

    boolean existsByPerfil(Perfil perfil);

    long countByPerfilAndAtivoTrue(Perfil perfil);

    List<Usuario> findByPerfilInOrderByNomeAsc(Collection<Perfil> perfis);
}
