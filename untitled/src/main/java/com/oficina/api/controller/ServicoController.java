package com.oficina.api.controller;

import com.oficina.api.model.Servico;
import com.oficina.api.service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Servico> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Servico criar(@RequestBody Servico servico) {
        return service.salvar(servico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @RequestBody Servico servico) {
        return service.buscarPorId(id)
                .map(s -> {
                    servico.setId(id);
                    return ResponseEntity.ok(service.salvar(servico));
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
