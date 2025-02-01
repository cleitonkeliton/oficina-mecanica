package src.main.servico;

import src.main.veiculo.Veiculo;

import java.util.ArrayList;
import java.util.List;

public class OrdemDeSv {
    private Veiculo veiculo;
    private List<Servico> servicos; // Corrigido o nome da lista

    public OrdemDeSv(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.servicos = new ArrayList<>();
    }

    public void adicionarSv(Servico servico) {
        this.servicos.add(servico); // Adiciona o serviço à lista de serviços
    }

    public double calcularSv() {
        double total = 0;
        for (Servico servico : servicos) {
            total += servico.getValor();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ordem de serviço para: ").append(veiculo.toString()).append("\n");
        sb.append("Serviços:\n");
        for (Servico servico : servicos) {
            sb.append("- ").append(servico.toString()).append("\n");
        }
        sb.append("Valor total: R$").append(calcularSv()).append("\n");
        sb.append("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n"); // Linha de separação após o valor total
        return sb.toString();
    }

}