package com.oficina.database;

import com.oficina.servico.Servico;
import com.oficina.servico.OrdemDeSv;

import java.sql.*;

public class OrdemDeSvDAO {

    public void inserirOrdemDeSv(OrdemDeSv ordemDeSv) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            long ordemId = inserirOrdem(connection, ordemDeSv);
            inserirServicos(connection, ordemId, ordemDeSv);

            connection.commit();
            System.out.println("Ordem de serviço inserida com sucesso! ID: " + ordemId);
        } catch (SQLException e) {
            rollback(connection, e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    private long inserirOrdem(Connection connection, OrdemDeSv ordemDeSv) throws SQLException {
        String sqlOrdem = "INSERT INTO ordens (veiculo_placa, data) VALUES (?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sqlOrdem, Statement.RETURN_GENERATED_KEYS)) {
            if (ordemDeSv.getVeiculo() == null || ordemDeSv.getVeiculo().getPlaca() == null) {
                throw new IllegalArgumentException("Veículo da ordem não pode ser nulo");
            }

            stmt.setString(1, ordemDeSv.getVeiculo().getPlaca());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Falha ao obter o ID da ordem inserida.");
                }
            }
        }
    }

    private void inserirServicos(Connection connection, long ordemId, OrdemDeSv ordemDeSv) throws SQLException {
        String sqlServico = "INSERT INTO servicos_ordem (ordem_id, descricao, valor) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlServico)) {
            for (Servico servico : ordemDeSv.getServicos()) {
                stmt.setLong(1, ordemId);
                stmt.setString(2, servico.getDescricao());
                stmt.setDouble(3, servico.getValor());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void rollback(Connection connection, SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
                System.err.println("Rollback realizado devido a erro: " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        throw new RuntimeException("Erro na transação de OrdemDeSv", e);
    }
}
