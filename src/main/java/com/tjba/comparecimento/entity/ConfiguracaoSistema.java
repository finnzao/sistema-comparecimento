package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;

/**
 * Entidade que representa uma configuração do sistema.
 */
@Entity
@Table(name = "configuracoes_sistema",
        indexes = {
                @Index(name = "idx_config_chave", columnList = "chave", unique = true),
                @Index(name = "idx_config_ativo", columnList = "ativo")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_config_chave", columnNames = "chave")
        }
)
public class ConfiguracaoSistema extends BaseEntity {

    @NotBlank(message = "Chave é obrigatória")
    @Size(max = 100, message = "Chave deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[a-z0-9._-]+$", message = "Chave deve conter apenas letras minúsculas, números, pontos, underscores e hífens")
    @Column(name = "chave", nullable = false, unique = true, length = 100)
    private String chave;

    @NotBlank(message = "Valor é obrigatório")
    @Size(max = 500, message = "Valor deve ter no máximo 500 caracteres")
    @Column(name = "valor", nullable = false, length = 500)
    private String valor;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Column(name = "descricao", length = 200)
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "editavel", nullable = false)
    private Boolean editavel = Boolean.TRUE;

    // === CONSTRUTORES ===
    public ConfiguracaoSistema() {
        super();
    }

    public ConfiguracaoSistema(String chave, String valor) {
        this();
        this.chave = chave;
        this.valor = valor;
        this.ativo = Boolean.TRUE;
        this.editavel = Boolean.TRUE;
        this.categoria = extractCategoriaFromChave(chave);
    }

    public ConfiguracaoSistema(String chave, String valor, String descricao) {
        this(chave, valor);
        this.descricao = descricao;
    }

    // === MÉTODOS UTILITÁRIOS ===
    public boolean isAtiva() {
        return Boolean.TRUE.equals(ativo);
    }

    public boolean isEditavel() {
        return Boolean.TRUE.equals(editavel);
    }

    public void ativar() {
        this.ativo = Boolean.TRUE;
    }

    public void desativar() {
        this.ativo = Boolean.FALSE;
    }

    public String getValorAsString() {
        return valor;
    }

    public Integer getValorAsInteger() {
        try {
            return valor != null ? Integer.parseInt(valor) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getValorAsBoolean() {
        return valor != null ? Boolean.parseBoolean(valor) : null;
    }

    public Double getValorAsDouble() {
        try {
            return valor != null ? Double.parseDouble(valor) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isValorBooleano() {
        return "true".equalsIgnoreCase(valor) || "false".equalsIgnoreCase(valor);
    }

    public boolean isValorNumerico() {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValorHorario() {
        return valor != null && valor.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    // === FORMATAÇÃO AUTOMÁTICA ===
    public void setChave(String chave) {
        this.chave = chave != null ? chave.toLowerCase().trim() : null;
        if (this.chave != null) {
            this.categoria = extractCategoriaFromChave(this.chave);
        }
    }

    private String extractCategoriaFromChave(String chave) {
        if (chave == null || !chave.contains(".")) {
            return "geral";
        }
        return chave.substring(0, chave.indexOf("."));
    }

    // === GETTERS E SETTERS ===
    public String getChave() {
        return chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor != null ? valor.trim() : null;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao != null ? descricao.trim() : null;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo != null ? ativo : Boolean.TRUE;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getEditavel() {
        return editavel;
    }

    public void setEditavel(Boolean editavel) {
        this.editavel = editavel != null ? editavel : Boolean.TRUE;
    }

    // === VALIDAÇÃO PERSONALIZADA ===
    @PrePersist
    @PreUpdate
    private void validate() {
        if (chave != null) {
            // Validar formato da chave
            if (!chave.matches("^[a-z0-9._-]+$")) {
                throw new IllegalArgumentException("Chave deve conter apenas letras minúsculas, números, pontos, underscores e hífens");
            }

            // Extrair categoria automaticamente
            this.categoria = extractCategoriaFromChave(chave);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracaoSistema that = (ConfiguracaoSistema) o;
        return Objects.equals(chave, that.chave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chave);
    }

    @Override
    public String toString() {
        return "ConfiguracaoSistema{" +
                "id=" + getId() +
                ", chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}