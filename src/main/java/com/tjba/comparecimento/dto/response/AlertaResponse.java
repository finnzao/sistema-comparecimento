package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Response para alertas do sistema.
 */
public class AlertaResponse {
    private String tipo; // warning, danger, info, success
    private String titulo;
    private String mensagem;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
    private String prioridade; // high, medium, low

    public AlertaResponse(String tipo, String titulo, String mensagem, LocalDate data, String prioridade) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.data = data;
        this.prioridade = prioridade;
    }

    // Getters
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensagem() { return mensagem; }
    public LocalDate getData() { return data; }
    public String getPrioridade() { return prioridade; }
}