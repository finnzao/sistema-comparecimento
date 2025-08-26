package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AtividadeRecenteResponse {

    @NotNull
    private String tipo; // comparecimento, cadastro, alteracao

    @NotNull
    private String descricao;

    @NotNull
    private String usuario;

    @NotNull
    private LocalDateTime dataHora;

    public AtividadeRecenteResponse() {}

    public AtividadeRecenteResponse(String tipo, String descricao, String usuario, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.usuario = usuario;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}