package com.oficina.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.List;

/**
 * Informações exibidas no site (tela principal). Existe uma única linha nesta tabela,
 * editada pelo administrador.
 */
@Entity
@Table(name = "configuracao_site")
public class ConfiguracaoSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome da oficina")
    @Size(max = 100)
    private String nomeOficina;

    @Size(max = 200)
    private String slogan;

    /** Faixa de aviso/promoção no topo do site (opcional). */
    @Size(max = 300)
    private String mensagemDestaque;

    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    private String sobre;

    /** Um diferencial por linha. */
    @Size(max = 2000)
    @Column(columnDefinition = "TEXT")
    private String diferenciais;

    /** Um horário por linha, ex.: "Segunda a sexta: 8h às 18h". */
    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String horarioFuncionamento;

    @Size(max = 300)
    private String formasPagamento;

    @Size(max = 300)
    private String endereco;

    @Size(max = 30)
    private String telefone;

    @Size(max = 30)
    private String whatsapp;

    @Email(message = "E-mail inválido")
    @Size(max = 120)
    private String email;

    @Size(max = 300)
    private String instagram;

    @Size(max = 300)
    private String facebook;

    /** Link do Google Maps (ou outro) para o endereço. */
    @Size(max = 500)
    private String linkMapa;

    public ConfiguracaoSite() {}

    /** Separa um campo de várias linhas em itens, ignorando linhas vazias. */
    private static List<String> linhas(String texto) {
        if (texto == null) {
            return List.of();
        }
        return Arrays.stream(texto.split("\\R"))
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toList();
    }

    public List<String> getListaDiferenciais() {
        return linhas(diferenciais);
    }

    public List<String> getListaHorarios() {
        return linhas(horarioFuncionamento);
    }

    public List<String> getParagrafosSobre() {
        return linhas(sobre);
    }

    /** Número do WhatsApp só com dígitos, pronto para o link wa.me (com 55 na frente). */
    public String getWhatsappLink() {
        if (whatsapp == null || whatsapp.isBlank()) {
            return null;
        }
        String digitos = whatsapp.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return null;
        }
        if (!digitos.startsWith("55") || digitos.length() <= 11) {
            digitos = "55" + digitos;
        }
        return "https://wa.me/" + digitos;
    }

    /** Telefone só com dígitos, para o link tel:. */
    public String getTelefoneLink() {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        return "tel:" + telefone.replaceAll("[^\\d+]", "");
    }

    /** Aceita o endereço completo ou só o @ do perfil. */
    public String getInstagramLink() {
        return linkRedeSocial(instagram, "https://instagram.com/");
    }

    public String getFacebookLink() {
        return linkRedeSocial(facebook, "https://facebook.com/");
    }

    public String getLinkMapaSeguro() {
        return urlHttp(linkMapa);
    }

    private static String linkRedeSocial(String valor, String base) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String v = valor.trim();
        if (v.contains("://")) {
            return urlHttp(v);
        }
        return base + v.replaceFirst("^@", "");
    }

    /** Só devolve links http/https, para não colocar "javascript:" ou parecidos no site. */
    private static String urlHttp(String valor) {
        if (valor == null) {
            return null;
        }
        String v = valor.trim();
        String minusculo = v.toLowerCase();
        return minusculo.startsWith("http://") || minusculo.startsWith("https://") ? v : null;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeOficina() { return nomeOficina; }
    public void setNomeOficina(String nomeOficina) { this.nomeOficina = nomeOficina; }

    public String getSlogan() { return slogan; }
    public void setSlogan(String slogan) { this.slogan = slogan; }

    public String getMensagemDestaque() { return mensagemDestaque; }
    public void setMensagemDestaque(String mensagemDestaque) { this.mensagemDestaque = mensagemDestaque; }

    public String getSobre() { return sobre; }
    public void setSobre(String sobre) { this.sobre = sobre; }

    public String getDiferenciais() { return diferenciais; }
    public void setDiferenciais(String diferenciais) { this.diferenciais = diferenciais; }

    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }

    public String getFormasPagamento() { return formasPagamento; }
    public void setFormasPagamento(String formasPagamento) { this.formasPagamento = formasPagamento; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getLinkMapa() { return linkMapa; }
    public void setLinkMapa(String linkMapa) { this.linkMapa = linkMapa; }
}
