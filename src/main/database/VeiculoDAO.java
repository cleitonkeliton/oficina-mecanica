package src.main.database;

import src.main.veiculo.Veiculo;

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

    public List<Veiculo> listarVeiculos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                // Aqui você precisará buscar o cliente associado ao veículo
                // Isso pode ser feito com uma consulta adicional ou um JOIN
                // Exemplo simplificado:
                String clienteCpf = resultSet.getString("cliente_cpf");
                // Buscar o cliente no banco de dados (ou usar um cache)
                // Cliente cliente = buscarClientePorCpf(clienteCpf);
                // Veiculo veiculo = new Veiculo(resultSet.getString("placa"), ...);
                // veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar veículos: " + e.getMessage());
        }
        return veiculos;
    }
}