package src.main;

import src.main.veiculo.Veiculo;

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
        veiculoDAO.listarVeiculosComClientes().forEach(System.out::println);
    }
}