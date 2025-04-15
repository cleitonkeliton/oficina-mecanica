package src.main.cliente;

public class Cliente {
    private String nome;
    private String cpf;
    private String telefone;

    public Cliente(String nome, String cpf, String telefone) {
        if (cpf == null || !cpf.replaceAll("[^0-9]", "").matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido");
        }
        if (telefone == null || !telefone.matches("\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}")) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }
    public String getCpf () {
        return cpf;
    }

    public String getTelefone(){
        return telefone;
    }

    @Override
    public String toString() {
        return "Cliente: " + nome + ", CPF: " + cpf + ", Telefone: " + telefone;
    }
}