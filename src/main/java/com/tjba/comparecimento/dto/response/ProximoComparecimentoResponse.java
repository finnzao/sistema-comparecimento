package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * DTO para resposta de relatório de comparecimentos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProximoComparecimentoResponse {

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

    // Constructors
    public void RelatorioComparecimentoResponse() {}

    public void RelatorioComparecimentoResponse(LocalDate dataInicio, LocalDate dataFim, Integer totalPessoas,
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
    public String getResumo() {
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