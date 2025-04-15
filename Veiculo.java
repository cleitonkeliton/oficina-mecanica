package src.main.veiculo;

public class Veiculo {
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private Cliente cliente; //Importando a classe cliente de forma privada e dando um "valor".

    //Através deste construtor chamo os atributos do Cliente.
    public Veiculo(String placa, String marca, String modelo, int ano, Cliente cliente){
        this.placa=placa;
        this.marca=marca;
        this.modelo=modelo;
        this.ano=ano;
        this.cliente=cliente;
    }

    public static void add(Veiculo veiculo) {
    }

    //getter (LER ou VER)
    public String getPlaca(){
        return placa;
    }

    public String getMarca() {return marca;}

    public int getAno() {return ano;}

    public Cliente getCliente() {
        return null;
    }
    public String getModelo(){
        return modelo;
    }
    //Reescrever o ToString pra quando a classe for chamada
    @Override
    public String toString(){
        return"Veiculo de marca: "+marca+" e Modelo: "+modelo+" Fabricado no ano: "+ano+
                " de placa: "+placa+" Nome do Cliente: "+cliente.getNome();
    }
}
