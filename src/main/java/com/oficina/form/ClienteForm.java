package com.oficina.form;

import com.oficina.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteForm {

    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 120, message = "Nome muito longo")
    private String nome;

    @NotBlank(message = "Informe o CPF")
    @Size(max = 20, message = "CPF inválido")
    private String cpf;

    @Size(max = 30, message = "Telefone muito longo")
    private String telefone;

    @Email(message = "E-mail inválido")
    @Size(max = 120, message = "E-mail muito longo")
    private String email;

    /** Senha para o cliente acompanhar o carro pelo site. Em branco = não cria/não altera. */
    @Size(max = 60, message = "Senha muito longa")
    private String senhaAcesso;

    public ClienteForm() {}

    public static ClienteForm de(Cliente cliente) {
        ClienteForm f = new ClienteForm();
        f.id = cliente.getId();
        f.nome = cliente.getNome();
        f.cpf = cliente.getCpfFormatado();
        f.telefone = cliente.getTelefone();
        f.email = cliente.getEmail();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaAcesso() { return senhaAcesso; }
    public void setSenhaAcesso(String senhaAcesso) { this.senhaAcesso = senhaAcesso; }
}
