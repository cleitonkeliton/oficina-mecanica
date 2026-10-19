package com.oficina.api.repository;

import com.oficina.api.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    List<Veiculo> findByClienteId(Long clienteId);

    boolean existsByPlacaIgnoreCase(String placa);
}
