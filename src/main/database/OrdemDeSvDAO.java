package src.main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdemDeSvDAO {

    public void inserirOrdemDeSv(OrdemDeSv ordemDeSv) {
        String sql = "INSERT INTO ordens_de_servico (veiculo_placa, descricao, valor) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ordemDeSv.getVeiculo().getPlaca());
            for (Servico servico : ordemDeSv.getServicos()) {
                statement.setString(2, servico.getDescricaoSv());
                statement.setDouble(3, servico.getValor());
                statement.executeUpdate();
            }
            System.out.println("Ordem de serviço inserida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir ordem de serviço: " + e.getMessage());
        }
    }

    public List<OrdemDeSv> listarOrdensDeSv() {
        List<OrdemDeSv> ordensDeSv = new ArrayList<>();
        String sql = "SELECT * FROM ordens_de_servico";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // Aqui você precisará buscar o veículo e os serviços associados à ordem de serviço
                // Exemplo simplificado:
                String veiculoPlaca = resultSet.getString("veiculo_placa");
                // Buscar o veículo no banco de dados (ou usar um cache)
                // Veiculo veiculo = buscarVeiculoPorPlaca(veiculoPlaca);
                // OrdemDeSv ordemDeSv = new OrdemDeSv(veiculo);
                // ordensDeSv.add(ordemDeSv);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar ordens de serviço: " + e.getMessage());
        }
        return ordensDeSv;
    }
}