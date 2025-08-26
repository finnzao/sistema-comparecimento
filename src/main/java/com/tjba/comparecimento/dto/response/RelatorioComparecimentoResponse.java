package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;

    private Integer totalPessoas;
    private Integer totalComparecimentos;
    private Integer totalPresenciais;
    private Integer totalVirtuais;
    private Integer totalJustificativas;
    private Double percentualConformidade;
    private String comarca;

    // Estatísticas calculadas
    private Integer comparecimentosEsperados;
    private Double taxaComparecimento;
    private Integer diasPeriodo;
    private Double mediaComparecimentosPorDia;

    // Distribuição por tipo (percentuais)
    private Double percentualPresenciais;
    private Double percentualVirtuais;
    private Double percentualJustificativas;

    // Campos para compatibilidade com relatórios estruturados
    private String tipoRelatorio;
    private LocalDateTime dataGeracao;
    private Integer totalRegistros;
    private List<Object> dados;
    private Map<String, Object> resumo;
    private Map<String, Object> estatisticas;

    // Constructors
    public RelatorioComparecimentoResponse() {}

    public RelatorioComparecimentoResponse(LocalDate dataInicio, LocalDate dataFim, Integer totalPessoas,
                                           Integer totalComparecimentos, Integer totalPresenciais,
                                           Integer totalVirtuais, Integer totalJustificativas,
                                           Double percentualConformidade, String comarca) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalPessoas = totalPessoas;
        this.totalComparecimentos = totalComparecimentos;
        this.totalPresenciais = totalPresenciais;
        this.totalVirtuais = totalVirtuais;
        this.totalJustificativas = totalJustificativas;
        this.percentualConformidade = percentualConformidade;
        this.comarca = comarca;

        // Calcular estatísticas derivadas
        calcularEstatisticas();
    }

    // Construtor completo para relatórios estruturados
    public RelatorioComparecimentoResponse(String tipoRelatorio, LocalDate periodoInicio, LocalDate periodoFim,
                                           LocalDateTime dataGeracao, Integer totalRegistros, String comarca,
                                           List<Object> dados, Map<String, Object> resumo) {
        this.tipoRelatorio = tipoRelatorio;
        this.dataInicio = periodoInicio;
        this.dataFim = periodoFim;
        this.dataGeracao = dataGeracao;
        this.totalRegistros = totalRegistros;
        this.comarca = comarca;
        this.dados = dados;
        this.resumo = resumo;
    }

    /**
     * Calcular estatísticas derivadas automaticamente
     */
    private void calcularEstatisticas() {
        // Calcular dias do período
        if (dataInicio != null && dataFim != null) {
            this.diasPeriodo = (int) dataInicio.until(dataFim).getDays() + 1;
        }

        // Calcular média de comparecimentos por dia
        if (totalComparecimentos != null && diasPeriodo != null && diasPeriodo > 0) {
            this.mediaComparecimentosPorDia = totalComparecimentos.doubleValue() / diasPeriodo;
            this.mediaComparecimentosPorDia = Math.round(mediaComparecimentosPorDia * 100.0) / 100.0;
        }

        // Calcular percentuais por tipo
        if (totalComparecimentos != null && totalComparecimentos > 0) {
            if (totalPresenciais != null) {
                this.percentualPresenciais = (totalPresenciais.doubleValue() / totalComparecimentos.doubleValue()) * 100.0;
                this.percentualPresenciais = Math.round(percentualPresenciais * 100.0) / 100.0;
            }

            if (totalVirtuais != null) {
                this.percentualVirtuais = (totalVirtuais.doubleValue() / totalComparecimentos.doubleValue()) * 100.0;
                this.percentualVirtuais = Math.round(percentualVirtuais * 100.0) / 100.0;
            }

            if (totalJustificativas != null) {
                this.percentualJustificativas = (totalJustificativas.doubleValue() / totalComparecimentos.doubleValue()) * 100.0;
                this.percentualJustificativas = Math.round(percentualJustificativas * 100.0) / 100.0;
            }
        }

        // Calcular taxa de comparecimento
        if (comparecimentosEsperados != null && comparecimentosEsperados > 0) {
            Integer comparecimentosEfetivos = (totalPresenciais != null ? totalPresenciais : 0) +
                    (totalVirtuais != null ? totalVirtuais : 0);
            this.taxaComparecimento = (comparecimentosEfetivos.doubleValue() / comparecimentosEsperados.doubleValue()) * 100.0;
            this.taxaComparecimento = Math.round(taxaComparecimento * 100.0) / 100.0;
        }
    }

    /**
     * Obter resumo textual do relatório
     */
    public String getResumoTextual() {
        StringBuilder resumo = new StringBuilder();

        resumo.append("Relatório de comparecimentos");

        if (comarca != null && !comarca.trim().isEmpty()) {
            resumo.append(" - Comarca: ").append(comarca);
        }

        if (dataInicio != null && dataFim != null) {
            resumo.append(" (").append(dataInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .append(" a ").append(dataFim.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .append(")");
        }

        if (totalComparecimentos != null) {
            resumo.append(" - ").append(totalComparecimentos).append(" comparecimentos registrados");
        }

        return resumo.toString();
    }

    /**
     * Verificar se há dados para exibir
     */
    public boolean hasData() {
        return totalComparecimentos != null && totalComparecimentos > 0;
    }

    /**
     * Obter tipo de comparecimento predominante
     */
    public String getTipoPredominante() {
        if (totalComparecimentos == null || totalComparecimentos == 0) {
            return null;
        }

        Integer maxValue = Math.max(
                totalPresenciais != null ? totalPresenciais : 0,
                Math.max(
                        totalVirtuais != null ? totalVirtuais : 0,
                        totalJustificativas != null ? totalJustificativas : 0
                )
        );

        if (maxValue.equals(totalPresenciais)) {
            return "Presencial";
        } else if (maxValue.equals(totalVirtuais)) {
            return "Virtual";
        } else if (maxValue.equals(totalJustificativas)) {
            return "Justificativas";
        }

        return null;
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
            response.dataInicio = periodoInicio;
            return this;
        }

        public Builder periodoFim(LocalDate periodoFim) {
            response.dataFim = periodoFim;
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
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        calcularEstatisticas();
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
        calcularEstatisticas();
    }

    public Integer getTotalPessoas() {
        return totalPessoas;
    }

    public void setTotalPessoas(Integer totalPessoas) {
        this.totalPessoas = totalPessoas;
    }

    public Integer getTotalComparecimentos() {
        return totalComparecimentos;
    }

    public void setTotalComparecimentos(Integer totalComparecimentos) {
        this.totalComparecimentos = totalComparecimentos;
        calcularEstatisticas();
    }

    public Integer getTotalPresenciais() {
        return totalPresenciais;
    }

    public void setTotalPresenciais(Integer totalPresenciais) {
        this.totalPresenciais = totalPresenciais;
        calcularEstatisticas();
    }

    public Integer getTotalVirtuais() {
        return totalVirtuais;
    }

    public void setTotalVirtuais(Integer totalVirtuais) {
        this.totalVirtuais = totalVirtuais;
        calcularEstatisticas();
    }

    public Integer getTotalJustificativas() {
        return totalJustificativas;
    }

    public void setTotalJustificativas(Integer totalJustificativas) {
        this.totalJustificativas = totalJustificativas;
        calcularEstatisticas();
    }

    public Double getPercentualConformidade() {
        return percentualConformidade;
    }

    public void setPercentualConformidade(Double percentualConformidade) {
        this.percentualConformidade = percentualConformidade;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public Integer getComparecimentosEsperados() {
        return comparecimentosEsperados;
    }

    public void setComparecimentosEsperados(Integer comparecimentosEsperados) {
        this.comparecimentosEsperados = comparecimentosEsperados;
        calcularEstatisticas();
    }

    public Double getTaxaComparecimento() {
        return taxaComparecimento;
    }

    public void setTaxaComparecimento(Double taxaComparecimento) {
        this.taxaComparecimento = taxaComparecimento;
    }

    public Integer getDiasPeriodo() {
        return diasPeriodo;
    }

    public void setDiasPeriodo(Integer diasPeriodo) {
        this.diasPeriodo = diasPeriodo;
    }

    public Double getMediaComparecimentosPorDia() {
        return mediaComparecimentosPorDia;
    }

    public void setMediaComparecimentosPorDia(Double mediaComparecimentosPorDia) {
        this.mediaComparecimentosPorDia = mediaComparecimentosPorDia;
    }

    public Double getPercentualPresenciais() {
        return percentualPresenciais;
    }

    public void setPercentualPresenciais(Double percentualPresenciais) {
        this.percentualPresenciais = percentualPresenciais;
    }

    public Double getPercentualVirtuais() {
        return percentualVirtuais;
    }

    public void setPercentualVirtuais(Double percentualVirtuais) {
        this.percentualVirtuais = percentualVirtuais;
    }

    public Double getPercentualJustificativas() {
        return percentualJustificativas;
    }

    public void setPercentualJustificativas(Double percentualJustificativas) {
        this.percentualJustificativas = percentualJustificativas;
    }

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

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
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

    @Override
    public String toString() {
        return "RelatorioComparecimentoResponse{" +
                "dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", totalComparecimentos=" + totalComparecimentos +
                ", comarca='" + comarca + '\'' +
                ", percentualConformidade=" + percentualConformidade +
                '}';
    }
}