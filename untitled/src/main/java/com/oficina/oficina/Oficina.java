package com.oficina.oficina;

import com.oficina.cliente.Cliente;
import com.oficina.servico.OrdemDeSv;
import com.oficina.veiculo.Veiculo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Oficina {
    private List<Cliente> clientes; // Corrigido o nome da lista
    private List<Veiculo> veiculos;
    private List<OrdemDeSv> ordemDeSvs;

    public Oficina() {
        this.clientes = new ArrayList<>();
        this.veiculos = new ArrayList<>();
        this.ordemDeSvs = new ArrayList<>();
    }
    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }

    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente); // Adiciona o cliente à lista de clientes
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        veiculos.add(veiculo); // Adiciona o veículo à lista de veículos
    }

    public List<OrdemDeSv>getOrdemDeSvs() {
        return Collections.unmodifiableList(ordemDeSvs); // Adiciona a ordem de serviço à lista de ordens
    }

    public String listarOrdemSv() {
        StringBuilder sb = new StringBuilder();
        for (OrdemDeSv ordem : ordemDeSvs) {
            sb.append(ordem.toString()).append("\n");
        }
        return sb.toString();
    }
}