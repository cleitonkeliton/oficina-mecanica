package com.oficina.api.repository;

import com.oficina.api.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findAllByOrderByIdDesc();

    List<OrdemServico> findByClienteIdOrderByIdDesc(Long clienteId);
}
