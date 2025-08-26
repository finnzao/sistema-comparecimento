package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// === CONFIGURAÇÃO SISTEMA ===
public class ConfiguracaoSistemaRequest {

    @NotBlank(message = "Valor é obrigatório")
    @Size(max = 500, message = "Valor deve ter no máximo 500 caracteres")
    private String valor;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    private Boolean ativo = true;

    // Constructors
    public ConfiguracaoSistemaRequest() {}

    public ConfiguracaoSistemaRequest(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
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
        this.ativo = ativo != null ? ativo : true;
    }

    @Override
    public String toString() {
        return "ConfiguracaoSistemaRequest{" +
                "valor='" + valor + '\'' +
                ", descricao='" + descricao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
