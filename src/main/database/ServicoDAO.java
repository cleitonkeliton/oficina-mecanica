package src.main.database;

import src.main.servico.Servico;
import src.main.servico.ServicoMecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void inserirServico(Servico servico) {
        String sql = "INSERT INTO servicos (descricao, valor) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, servico.getDescricaoSv());
            statement.setDouble(2, servico.getValor());
            statement.executeUpdate();
            System.out.println("Serviço inserido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir serviço: " + e.getMessage());
        }
    }

    public List<Servico> listarServicos() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Servico servico = new ServicoMecanico(
                        resultSet.getString("descricao"),
                        resultSet.getDouble("valor")
                );
                servicos.add(servico);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar serviços: " + e.getMessage());
        }
        return servicos;
    }
}