package com.oficina.database;

import com.oficina.database.DatabaseConnection;
import com.oficina.servico.Servico;
import com.oficina.servico.OrdemDeSv;
import java.sql.*;

public class OrdemDeSvDAO {

    public void inserirOrdemDeSv(OrdemDeSv ordemDeSv) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlOrdem = "INSERT INTO ordens (veiculo_placa, data) VALUES (?, NOW())";
            try (PreparedStatement stmtOrdem = connection.prepareStatement(sqlOrdem, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrdem.setString(1, ordemDeSv.getVeiculo().getPlaca());
                stmtOrdem.executeUpdate();

                try (ResultSet generatedKeys = stmtOrdem.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long ordemId = generatedKeys.getLong(1);

                        String sqlServico = "INSERT INTO servicos_ordem (ordem_id, descricao, valor) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtServico = connection.prepareStatement(sqlServico)) {
                            for (Servico servico : ordemDeSv.getServicos()) {
                                stmtServico.setLong(1, ordemId);
                                stmtServico.setString(2, servico.getDescricao());
                                stmtServico.setDouble(3, servico.getValor());
                                stmtServico.addBatch();
                            }
                            stmtServico.executeBatch();
                        }
                    }
                }
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Erro na transação", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }
}
