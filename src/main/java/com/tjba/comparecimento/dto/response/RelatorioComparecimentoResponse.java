package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response para relatórios de comparecimento estruturados.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatorioComparecimentoResponse {

    private String tipoRelatorio;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private LocalDateTime dataGeracao;
    private Integer totalRegistros;
    private String comarca;
    private List<Object> dados;
    private Map<String, Object> resumo;
    private Map<String, Object> estatisticas;

    // Construtor padrão
    public RelatorioComparecimentoResponse() {}

    // Construtor completo
    public RelatorioComparecimentoResponse(String tipoRelatorio, LocalDate periodoInicio, LocalDate periodoFim,
                                           LocalDateTime dataGeracao, Integer totalRegistros, String comarca,
                                           List<Object> dados, Map<String, Object> resumo) {
        this.tipoRelatorio = tipoRelatorio;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.dataGeracao = dataGeracao;
        this.totalRegistros = totalRegistros;
        this.comarca = comarca;
        this.dados = dados;
        this.resumo = resumo;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RelatorioComparecimentoResponse response = new RelatorioComparecimentoResponse();

        public Builder tipoRelatorio(String tipoRelatorio) {
            response.tipoRelatorio = tipoRelatorio;
            return this;
        }

        public Builder periodoInicio(LocalDate periodoInicio) {
            response.periodoInicio = periodoInicio;
            return this;
        }

        public Builder periodoFim(LocalDate periodoFim) {
            response.periodoFim = periodoFim;
            return this;
        }

        public Builder dataGeracao(LocalDateTime dataGeracao) {
            response.dataGeracao = dataGeracao;
            return this;
        }

        public Builder totalRegistros(Integer totalRegistros) {
            response.totalRegistros = totalRegistros;
            return this;
        }

        public Builder comarca(String comarca) {
            response.comarca = comarca;
            return this;
        }

        public Builder dados(List<Object> dados) {
            response.dados = dados;
            return this;
        }

        public Builder resumo(Map<String, Object> resumo) {
            response.resumo = resumo;
            return this;
        }

        public Builder estatisticas(Map<String, Object> estatisticas) {
            response.estatisticas = estatisticas;
            return this;
        }

        public RelatorioComparecimentoResponse build() {
            return response;
        }
    }

    // Getters e Setters
    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public List<Object> getDados() {
        return dados;
    }

    public void setDados(List<Object> dados) {
        this.dados = dados;
    }

    public Map<String, Object> getResumo() {
        return resumo;
    }

    public void setResumo(Map<String, Object> resumo) {
        this.resumo = resumo;
    }

    public Map<String, Object> getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(Map<String, Object> estatisticas) {
        this.estatisticas = estatisticas;
    }
}
