package com.oficina.servico;

import com.oficina.veiculo.Veiculo;
import java.util.ArrayList;
import java.util.List;

public class OrdemDeSv {
    private final Veiculo veiculo;
    private final List<com.oficina.servico.Servico> servicos;

    public OrdemDeSv(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.servicos = new ArrayList<>();
    }

    public void adicionarSv(com.oficina.servico.Servico servico) {
        this.servicos.add(servico); // Adiciona o serviço à lista de serviços

    }

    public double calcularSv() {
        double total = 0;
        for (com.oficina.servico.Servico servico : servicos) {
            total += servico.getValor();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ordem de serviço para: ").append(veiculo.toString()).append("\n")
                .append("Serviços:\n");

        for (com.oficina.servico.Servico servico : servicos) {
            sb.append("- ").append(servico.toString()).append("\n");
        }

        sb.append(String.format("Valor total: R$ %.2f\n", calcularSv()))
                .append("==============================================\n");

        return sb.toString();
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public List<com.oficina.servico.Servico> getServicos() {
        return servicos;
    }
}