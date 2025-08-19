package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Entidade que representa um endereço.
 */
@Entity
@Table(name = "enderecos",
        indexes = {
                @Index(name = "idx_endereco_cep", columnList = "cep"),
                @Index(name = "idx_endereco_cidade", columnList = "cidade"),
                @Index(name = "idx_endereco_estado", columnList = "estado")
        }
)
@NamedQueries({
        @NamedQuery(
                name = "Endereco.findByCep",
                query = "SELECT e FROM Endereco e WHERE e.cep = :cep"
        ),
        @NamedQuery(
                name = "Endereco.findByCidade",
                query = "SELECT e FROM Endereco e WHERE e.cidade = :cidade"
        )
})
public class Endereco extends BaseEntity {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter o formato 00000-000")
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    @Column(name = "logradouro", nullable = false, length = 200)
    private String logradouro;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(name = "numero", length = 20)
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento", length = 100)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser a sigla com 2 letras maiúsculas")
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    // Constructors
    public Endereco() {
        super();
    }

    public Endereco(String cep, String logradouro, String bairro, String cidade, String estado) {
        this();
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    // Getters e Setters
    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep != null ? cep.replaceAll("[^\\d]", "") : null;
        if (this.cep != null && this.cep.length() == 8) {
            this.cep = this.cep.substring(0, 5) + "-" + this.cep.substring(5);
        }
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado != null ? estado.toUpperCase() : null;
    }

    // Métodos utilitários
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();

        sb.append(logradouro);
        if (numero != null && !numero.trim().isEmpty()) {
            sb.append(", ").append(numero);
        }
        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(", ").append(complemento);
        }
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade).append(" - ").append(estado);
        if (cep != null) {
            sb.append(", CEP: ").append(cep);
        }

        return sb.toString();
    }

    public String getEnderecoResumido() {
        return String.format("%s, %s - %s",
                logradouro + (numero != null ? ", " + numero : ""),
                cidade,
                estado);
    }

    public String getCepSomenteNumeros() {
        return cep != null ? cep.replaceAll("[^\\d]", "") : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(cep, endereco.cep) &&
                Objects.equals(logradouro, endereco.logradouro) &&
                Objects.equals(numero, endereco.numero) &&
                Objects.equals(bairro, endereco.bairro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, logradouro, numero, bairro);
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + getId() +
                ", endereco='" + getEnderecoResumido() + '\'' +
                ", cep='" + cep + '\'' +
                '}';
    }
}
