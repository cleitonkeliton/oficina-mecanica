package com.oficina;

import com.oficina.database.ClienteDAO;
import com.oficina.veiculo.Veiculo;

public class Main {
    public static void main(String[] args) {
        ClienteDAO clienteDAO = new ClienteDAO();
        com.oficina.database.VeiculoDAO veiculoDAO = new com.oficina.database.VeiculoDAO();

        // Inserir um cliente
        com.oficina.cliente.Cliente novoCliente = new com.oficina.cliente.Cliente("João Silva", "123.456.789-00", "(11) 9999-8888");
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