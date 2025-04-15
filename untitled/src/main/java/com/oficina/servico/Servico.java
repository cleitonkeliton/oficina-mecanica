package servico;

public abstract class Servico {
    private String descricaoSv;
    private String tipoReparo;
    private boolean garantia;
    private double valor;

    public Servico(String descricaoSv, double valor, String tipoReparo, boolean garantia){
        this.descricaoSv = descricaoSv;
        this.tipoReparo = tipoReparo;
        this.garantia = garantia;
        this.valor = valor;
    }

    public String getDescricaoSv(){
        return descricaoSv;
    }
    public double getValor(){
        return valor;
    }

    public String getTipoReparo() {
        return tipoReparo;
    }

    public boolean isGarantia() {
        return garantia;
    }

    @Override
    public String toString() {
        return "Serviço realizado: " + descricaoSv + " | Tipo de Reparo: " + tipoReparo +
                " | Garantia: " + (garantia ? "Sim" : "Não") + " | Valor: R$" + valor;
    }

    public String getDescricao() {
        return descricaoSv;
    }
}
