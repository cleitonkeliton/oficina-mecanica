package com.oficina.database;

import com.oficina.api.model.Veiculo;
import com.oficina.api.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public List<Veiculo> listarVeiculosComClientes() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT v.id as veiculo_id, v.marca, v.modelo, v.placa, c.id as cliente_id, c.nome, c.cpf " +
                "FROM veiculos v " +
                "JOIN clientes c ON v.cliente_id = c.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getLong("cliente_id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));

                Veiculo veiculo = new Veiculo();
                veiculo.setId(rs.getLong("veiculo_id"));
                veiculo.setMarca(rs.getString("marca"));
                veiculo.setModelo(rs.getString("modelo"));
                veiculo.setPlaca(rs.getString("placa"));
                veiculo.setCliente(cliente);

                veiculos.add(veiculo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculos;
    }
}
