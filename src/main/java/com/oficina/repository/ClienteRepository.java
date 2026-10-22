package com.oficina.repository;

import com.oficina.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByCpf(String cpf);

    List<Cliente> findAllByOrderByNomeAsc();

    /** Busca por parte do nome, do CPF ou do telefone. */
    @Query("""
            select c from Cliente c
            where lower(c.nome) like lower(concat('%', :termo, '%'))
               or (:digitos <> '' and c.cpf like concat('%', :digitos, '%'))
               or c.telefone like concat('%', :termo, '%')
               or (:digitos <> '' and replace(replace(replace(replace(c.telefone, '(', ''), ')', ''), '-', ''), ' ', '')
                   like concat('%', :digitos, '%'))
            order by c.nome
            """)
    List<Cliente> pesquisar(@Param("termo") String termo, @Param("digitos") String digitos);
}
