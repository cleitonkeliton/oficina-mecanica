package servico;

import servico.Servico;

public class ServicoMecanico extends Servico {
    //construtor SUPER.
    public ServicoMecanico(String descricaoSv, double valor, boolean garantia, String tipoServico) {
        super(descricaoSv, valor, "Mecânico", true);
    }
}

