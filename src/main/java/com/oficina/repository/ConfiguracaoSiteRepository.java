package com.oficina.repository;

import com.oficina.model.ConfiguracaoSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracaoSiteRepository extends JpaRepository<ConfiguracaoSite, Long> {

    Optional<ConfiguracaoSite> findFirstByOrderByIdAsc();
}
