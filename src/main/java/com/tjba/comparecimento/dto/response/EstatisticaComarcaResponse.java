package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstatisticaComarcaResponse {

    @NotNull
    private String comarca;

    @NotNull
    @PositiveOrZero
    private Integer totalPessoas;

    @NotNull
    @PositiveOrZero
    private Integer pessoasEmConformidade;

    @NotNull
    @PositiveOrZero
    private Integer pessoasInadimplentes;

    @NotNull
    @PositiveOrZero
    private Double percentualConformidade;

    public EstatisticaComarcaResponse() {}

    public EstatisticaComarcaResponse(String comarca, Integer totalPessoas, Integer pessoasEmConformidade,
                                      Integer pessoasInadimplentes, Double percentualConformidade) {
        this.comarca = comarca;
        this.totalPessoas = totalPessoas;
        this.pessoasEmConformidade = pessoasEmConformidade;
        this.pessoasInadimplentes = pessoasInadimplentes;
        this.percentualConformidade = percentualConformidade;
    }

    // Getters e Setters
    public String getComarca() { return comarca; }
    public void setComarca(String comarca) { this.comarca = comarca; }
    public Integer getTotalPessoas() { return totalPessoas; }
    public void setTotalPessoas(Integer totalPessoas) { this.totalPessoas = totalPessoas; }
    public Integer getPessoasEmConformidade() { return pessoasEmConformidade; }
    public void setPessoasEmConformidade(Integer pessoasEmConformidade) { this.pessoasEmConformidade = pessoasEmConformidade; }
    public Integer getPessoasInadimplentes() { return pessoasInadimplentes; }
    public void setPessoasInadimplentes(Integer pessoasInadimplentes) { this.pessoasInadimplentes = pessoasInadimplentes; }
    public Double getPercentualConformidade() { return percentualConformidade; }
    public void setPercentualConformidade(Double percentualConformidade) { this.percentualConformidade = percentualConformidade; }
}