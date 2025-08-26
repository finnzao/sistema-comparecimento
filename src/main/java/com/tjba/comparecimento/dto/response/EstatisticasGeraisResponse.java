package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para resposta de estatísticas gerais do dashboard.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstatisticasGeraisResponse {

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
    private Integer comparecimentosHoje;

    @NotNull
    @PositiveOrZero
    private Integer pessoasAtrasadas;

    @NotNull
    @PositiveOrZero
    private Integer comparecimentosMes;

    @NotNull
    @PositiveOrZero
    private Double percentualConformidade;

    @NotNull
    @PositiveOrZero
    private Integer novosCadastrosMes;

    // Construtor padrão
    public EstatisticasGeraisResponse() {}

    // Construtor completo
    public EstatisticasGeraisResponse(Integer totalPessoas, Integer pessoasEmConformidade,
                                      Integer pessoasInadimplentes, Integer comparecimentosHoje,
                                      Integer pessoasAtrasadas, Integer comparecimentosMes,
                                      Double percentualConformidade, Integer novosCadastrosMes) {
        this.totalPessoas = totalPessoas;
        this.pessoasEmConformidade = pessoasEmConformidade;
        this.pessoasInadimplentes = pessoasInadimplentes;
        this.comparecimentosHoje = comparecimentosHoje;
        this.pessoasAtrasadas = pessoasAtrasadas;
        this.comparecimentosMes = comparecimentosMes;
        this.percentualConformidade = percentualConformidade;
        this.novosCadastrosMes = novosCadastrosMes;
    }

    // Getters e Setters
    public Integer getTotalPessoas() {
        return totalPessoas;
    }

    public void setTotalPessoas(Integer totalPessoas) {
        this.totalPessoas = totalPessoas;
    }

    public Integer getPessoasEmConformidade() {
        return pessoasEmConformidade;
    }

    public void setPessoasEmConformidade(Integer pessoasEmConformidade) {
        this.pessoasEmConformidade = pessoasEmConformidade;
    }

    public Integer getPessoasInadimplentes() {
        return pessoasInadimplentes;
    }

    public void setPessoasInadimplentes(Integer pessoasInadimplentes) {
        this.pessoasInadimplentes = pessoasInadimplentes;
    }

    public Integer getComparecimentosHoje() {
        return comparecimentosHoje;
    }

    public void setComparecimentosHoje(Integer comparecimentosHoje) {
        this.comparecimentosHoje = comparecimentosHoje;
    }

    public Integer getPessoasAtrasadas() {
        return pessoasAtrasadas;
    }

    public void setPessoasAtrasadas(Integer pessoasAtrasadas) {
        this.pessoasAtrasadas = pessoasAtrasadas;
    }

    public Integer getComparecimentosMes() {
        return comparecimentosMes;
    }

    public void setComparecimentosMes(Integer comparecimentosMes) {
        this.comparecimentosMes = comparecimentosMes;
    }

    public Double getPercentualConformidade() {
        return percentualConformidade;
    }

    public void setPercentualConformidade(Double percentualConformidade) {
        this.percentualConformidade = percentualConformidade;
    }

    public Integer getNovosCadastrosMes() {
        return novosCadastrosMes;
    }

    public void setNovosCadastrosMes(Integer novosCadastrosMes) {
        this.novosCadastrosMes = novosCadastrosMes;
    }

    @Override
    public String toString() {
        return "EstatisticasGeraisResponse{" +
                "totalPessoas=" + totalPessoas +
                ", pessoasEmConformidade=" + pessoasEmConformidade +
                ", pessoasInadimplentes=" + pessoasInadimplentes +
                ", comparecimentosHoje=" + comparecimentosHoje +
                ", pessoasAtrasadas=" + pessoasAtrasadas +
                ", comparecimentosMes=" + comparecimentosMes +
                ", percentualConformidade=" + percentualConformidade +
                ", novosCadastrosMes=" + novosCadastrosMes +
                '}';
    }
}