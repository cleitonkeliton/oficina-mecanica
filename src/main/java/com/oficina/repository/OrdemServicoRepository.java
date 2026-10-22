package com.oficina.repository;

import com.oficina.model.OrdemServico;
import com.oficina.model.StatusServico;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    /**
     * Lê a ordem travando a linha até o fim da transação: duas pessoas alterando a mesma ordem
     * ao mesmo tempo esperam uma pela outra, em vez de uma apagar a mudança da outra.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrdemServico o where o.id = :id")
    Optional<OrdemServico> buscarTravando(@Param("id") Long id);

    List<OrdemServico> findByStatusNotInOrderByDataEntradaAsc(Collection<StatusServico> status);

    List<OrdemServico> findByStatusOrderByDataEntradaDesc(StatusServico status);

    List<OrdemServico> findTop100ByOrderByDataEntradaDesc();

    List<OrdemServico> findByClienteIdOrderByDataEntradaDesc(Long clienteId);

    List<OrdemServico> findByVeiculoIdOrderByDataEntradaDesc(Long veiculoId);

    boolean existsByVeiculoId(Long veiculoId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByVeiculoIdAndStatusNotIn(Long veiculoId, Collection<StatusServico> status);

    long countByStatus(StatusServico status);

    /** Busca por placa, nome do cliente ou número da ordem. */
    @Query("""
            select o from OrdemServico o join o.veiculo v join o.cliente c
            where (:status is null or o.status = :status)
              and (:termo = ''
                   or (:placa <> '' and v.placa like concat('%', :placa, '%'))
                   or lower(c.nome) like lower(concat('%', :termo, '%'))
                   or lower(v.modelo) like lower(concat('%', :termo, '%'))
                   or (:numero is not null and o.id = :numero))
            order by o.dataEntrada desc
            """)
    List<OrdemServico> pesquisar(@Param("status") StatusServico status,
                                 @Param("termo") String termo,
                                 @Param("placa") String placa,
                                 @Param("numero") Long numero);
}
