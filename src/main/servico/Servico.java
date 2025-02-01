package src.main.servico;
//classe ABSTRATA.
public abstract class Servico {
    private String descricaoSv;
    private double valor;

    public Servico(String descricaoSv, double valor){
        this.descricaoSv=descricaoSv;
        this.valor=valor;
    }
    //getter (LER ou VER)
    public String getDescricaoSv(){
        return descricaoSv;
    }
    public double getValor(){
        return valor;
    }
    //Reescrever o ToString pra quando a classe for chamada
    @Override
    public String toString(){
        return "Serviço realizado: "+descricaoSv+" Valor do Serviço: "+valor;
    }

    public void add(Servico servico) {

    }
}
