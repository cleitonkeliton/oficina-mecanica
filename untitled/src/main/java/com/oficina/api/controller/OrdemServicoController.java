package com.oficina.api.controller;

import com.oficina.api.model.OrdemServico;
import com.oficina.api.service.OrdemServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrdemServico> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrdemServico criar(@RequestBody OrdemServico ordem) {
        return service.salvar(ordem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServico> atualizar(@PathVariable Long id, @RequestBody OrdemServico ordem) {
        return service.buscarPorId(id)
                .map(os -> {
                    ordem.setId(id);
                    return ResponseEntity.ok(service.salvar(ordem));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
