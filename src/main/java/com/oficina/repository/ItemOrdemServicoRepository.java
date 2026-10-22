package com.oficina.repository;

import com.oficina.model.ItemOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {

    boolean existsByServicoId(Long servicoId);
}
