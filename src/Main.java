package src;

import src.main.database.ClienteDAO;
import src.main.database.VeiculoDAO;
import src.main.database.ServicoDAO;
import src.main.database.OrdemDeSvDAO;
import src.main.cliente.Cliente;
import src.main.veiculo.Veiculo;
import src.main.servico.ServicoMecanico;
import src.main.servico.OrdemDeSv;

public class Main {
    public static void main(String[] args) {
        ClienteDAO clienteDAO = new ClienteDAO();
        VeiculoDAO veiculoDAO = new VeiculoDAO();

        // Inserir um cliente
        Cliente novoCliente = new Cliente("João Silva", "123.456.789-00", "(11) 9999-8888");
        clienteDAO.inserirCliente(novoCliente);

        // Listar clientes
        System.out.println("Lista de clientes:");
        clienteDAO.listarClientes().forEach(System.out::println);

        // Inserir um veículo
        Veiculo novoVeiculo = new Veiculo("ABC-1234", "Toyota", "Corolla", 2019, novoCliente);
        veiculoDAO.inserirVeiculo(novoVeiculo);

        // Listar veículos
        System.out.println("Lista de veículos:");
        veiculoDAO.listarVeiculos().forEach(System.out::println);
    }
}