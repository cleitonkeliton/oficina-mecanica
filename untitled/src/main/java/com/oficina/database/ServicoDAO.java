package src.main.database;

import src.main.servico.Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    private ServicoFactory factory;

    public ServicoDAO(ServicoFactory factory) {
        this.factory = factory;
    }

    public List<Servico> listarServicos() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT descricao, valor, tipo, garantia FROM servicos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servico servico = factory.criarServico(
                        rs.getString("descricao"),
                        rs.getDouble("valor"),
                        rs.getString("tipo"),
                        rs.getBoolean("garantia")
                );
                servicos.add(servico);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar serviços: " + e.getMessage());
        }
        return servicos;
    }
}
