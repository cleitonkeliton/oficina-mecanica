package com.oficina.repository;

import com.oficina.model.Veiculo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    /** Lê o veículo travando a linha até o fim da transação (outra entrada do mesmo carro espera). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Veiculo v where v.id = :id")
    Optional<Veiculo> buscarTravando(@Param("id") Long id);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    boolean existsByPlaca(String placa);

    boolean existsByClienteId(Long clienteId);

    List<Veiculo> findByClienteIdOrderByPlacaAsc(Long clienteId);

    List<Veiculo> findAllByOrderByPlacaAsc();

    /** Busca por parte da placa, marca, modelo ou nome do dono. */
    @Query("""
            select v from Veiculo v join v.cliente c
            where (:placa <> '' and v.placa like concat('%', :placa, '%'))
               or lower(v.marca) like lower(concat('%', :termo, '%'))
               or lower(v.modelo) like lower(concat('%', :termo, '%'))
               or lower(c.nome) like lower(concat('%', :termo, '%'))
            order by v.placa
            """)
    List<Veiculo> pesquisar(@Param("termo") String termo, @Param("placa") String placa);
}
