package src.main.oficina;

import src.main.cliente.Cliente;
import src.main.servico.OrdemDeSv;
import src.main.veiculo.Veiculo;

import java.util.ArrayList;
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

    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente); // Adiciona o cliente à lista de clientes
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        veiculos.add(veiculo); // Adiciona o veículo à lista de veículos
    }

    public void adicionarOrdemServico(OrdemDeSv ordemDeSv) {
        ordemDeSvs.add(ordemDeSv); // Adiciona a ordem de serviço à lista de ordens
    }

    public void listarOrdemSv() {
        for (OrdemDeSv ordem : ordemDeSvs) {
            System.out.println(ordem);
        }
    }
}