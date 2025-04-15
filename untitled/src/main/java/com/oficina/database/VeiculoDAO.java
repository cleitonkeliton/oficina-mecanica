package com.oficina.database;

import com.oficina.database.DatabaseConnection;
import veiculo.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public void inserirVeiculo(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (placa, marca, modelo, ano, cliente_cpf) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, veiculo.getPlaca());
            statement.setString(2, veiculo.getMarca());
            statement.setString(3, veiculo.getModelo());
            statement.setInt(4, veiculo.getAno());
            statement.setString(5, veiculo.getCliente().getCpf());
            statement.executeUpdate();
            System.out.println("Veículo inserido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir veículo: " + e.getMessage());
        }
    }

    public List<Veiculo> listarVeiculosComClientes() {
        String sql = "SELECT v.*, c.nome, c.telefone FROM veiculos v " +
                "JOIN clientes c ON v.cliente_cpf = c.cpf";

        List<Veiculo> veiculos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cliente.Cliente cliente = new cliente.Cliente(
                        rs.getString("nome"),
                        rs.getString("cliente_cpf"),
                        rs.getString("telefone")
                );

                Veiculo veiculo = new Veiculo(
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("ano"),
                        cliente
                );

                veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            // Tratar exceção
        }
        return veiculos;
    }
}
