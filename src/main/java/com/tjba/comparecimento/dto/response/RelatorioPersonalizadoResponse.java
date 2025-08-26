package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response para dados de relatórios personalizados.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatorioPersonalizadoResponse {

    private String tipoRelatorio;
    private LocalDateTime dataGeracao;
    private Map<String, Object> parametros;
    private Map<String, Object> dados;
    private Map<String, Object> estatisticas;
    private Map<String, Object> metadados;

    // Construtor padrão
    public RelatorioPersonalizadoResponse() {}

    // Construtor com parâmetros essenciais
    public RelatorioPersonalizadoResponse(String tipoRelatorio, Map<String, Object> dados) {
        this.tipoRelatorio = tipoRelatorio;
        this.dataGeracao = LocalDateTime.now();
        this.dados = dados;
    }

    // Getters e Setters
    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }

    public Map<String, Object> getDados() {
        return dados;
    }

    public void setDados(Map<String, Object> dados) {
        this.dados = dados;
    }

    public Map<String, Object> getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(Map<String, Object> estatisticas) {
        this.estatisticas = estatisticas;
    }

    public Map<String, Object> getMetadados() {
        return metadados;
    }

    public void setMetadados(Map<String, Object> metadados) {
        this.metadados = metadados;
    }
}