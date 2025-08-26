package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraficoComparecimentosResponse {

    @NotNull
    private List<String> labels;

    @NotNull
    private List<Integer> dadosPresenciais;

    @NotNull
    private List<Integer> dadosVirtuais;

    @NotNull
    private List<Integer> dadosJustificativas;

    public GraficoComparecimentosResponse() {}

    public GraficoComparecimentosResponse(List<String> labels, List<Integer> dadosPresenciais,
                                          List<Integer> dadosVirtuais, List<Integer> dadosJustificativas) {
        this.labels = labels;
        this.dadosPresenciais = dadosPresenciais;
        this.dadosVirtuais = dadosVirtuais;
        this.dadosJustificativas = dadosJustificativas;
    }

    // Getters e Setters
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<Integer> getDadosPresenciais() { return dadosPresenciais; }
    public void setDadosPresenciais(List<Integer> dadosPresenciais) { this.dadosPresenciais = dadosPresenciais; }
    public List<Integer> getDadosVirtuais() { return dadosVirtuais; }
    public void setDadosVirtuais(List<Integer> dadosVirtuais) { this.dadosVirtuais = dadosVirtuais; }
    public List<Integer> getDadosJustificativas() { return dadosJustificativas; }
    public void setDadosJustificativas(List<Integer> dadosJustificativas) { this.dadosJustificativas = dadosJustificativas; }
}