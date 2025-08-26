package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * DTO para resposta de relatório de comparecimentos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer totalPessoas;
    private Integer totalComparecimentos;
    private Integer totalPresenciais;
    private Integer totalVirtuais;
    private Integer totalJustificativas;
    private Double percentualConformidade;
    private String comarca;

    // Constructors
    public UserInfoResponse() {}

    public UserInfoResponse(LocalDate dataInicio, LocalDate dataFim, Integer totalPessoas,
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
    }

    // Getters e Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
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
    }

    public Integer getTotalPresenciais() {
        return totalPresenciais;
    }

    public void setTotalPresenciais(Integer totalPresenciais) {
        this.totalPresenciais = totalPresenciais;
    }

    public Integer getTotalVirtuais() {
        return totalVirtuais;
    }

    public void setTotalVirtuais(Integer totalVirtuais) {
        this.totalVirtuais = totalVirtuais;
    }

    public Integer getTotalJustificativas() {
        return totalJustificativas;
    }

    public void setTotalJustificativas(Integer totalJustificativas) {
        this.totalJustificativas = totalJustificativas;
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

    @Override
    public String toString() {
        return "RelatorioComparecimentoResponse{" +
                "dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", totalPessoas=" + totalPessoas +
                ", totalComparecimentos=" + totalComparecimentos +
                ", totalPresenciais=" + totalPresenciais +
                ", totalVirtuais=" + totalVirtuais +
                ", totalJustificativas=" + totalJustificativas +
                ", percentualConformidade=" + percentualConformidade +
                ", comarca='" + comarca + '\'' +
                '}';
    }
}