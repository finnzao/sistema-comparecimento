package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceResponse {

    @NotNull
    @PositiveOrZero
    private Double taxaComparecimento;

    @NotNull
    @PositiveOrZero
    private Double tempoMedioEntreComparecimentos;

    @NotNull
    @PositiveOrZero
    private Double percentualComparecimentoVirtual;

    @NotNull
    @PositiveOrZero
    private Integer totalComparecimentosRealizados;

    @NotNull
    @PositiveOrZero
    private Integer totalComparecimentosEsperados;

    public PerformanceResponse() {}

    public PerformanceResponse(Double taxaComparecimento, Double tempoMedioEntreComparecimentos,
                               Double percentualComparecimentoVirtual, Integer totalComparecimentosRealizados,
                               Integer totalComparecimentosEsperados) {
        this.taxaComparecimento = taxaComparecimento;
        this.tempoMedioEntreComparecimentos = tempoMedioEntreComparecimentos;
        this.percentualComparecimentoVirtual = percentualComparecimentoVirtual;
        this.totalComparecimentosRealizados = totalComparecimentosRealizados;
        this.totalComparecimentosEsperados = totalComparecimentosEsperados;
    }

    // Getters e Setters
    public Double getTaxaComparecimento() { return taxaComparecimento; }
    public void setTaxaComparecimento(Double taxaComparecimento) { this.taxaComparecimento = taxaComparecimento; }
    public Double getTempoMedioEntreComparecimentos() { return tempoMedioEntreComparecimentos; }
    public void setTempoMedioEntreComparecimentos(Double tempoMedioEntreComparecimentos) { this.tempoMedioEntreComparecimentos = tempoMedioEntreComparecimentos; }
    public Double getPercentualComparecimentoVirtual() { return percentualComparecimentoVirtual; }
    public void setPercentualComparecimentoVirtual(Double percentualComparecimentoVirtual) { this.percentualComparecimentoVirtual = percentualComparecimentoVirtual; }
    public Integer getTotalComparecimentosRealizados() { return totalComparecimentosRealizados; }
    public void setTotalComparecimentosRealizados(Integer totalComparecimentosRealizados) { this.totalComparecimentosRealizados = totalComparecimentosRealizados; }
    public Integer getTotalComparecimentosEsperados() { return totalComparecimentosEsperados; }
    public void setTotalComparecimentosEsperados(Integer totalComparecimentosEsperados) { this.totalComparecimentosEsperados = totalComparecimentosEsperados; }
}