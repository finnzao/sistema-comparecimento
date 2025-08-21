package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * DTO para resposta de relatório de comparecimentos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatorioComparecimentoResponse {

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
    public RelatorioComparecimentoResponse() {}

    public RelatorioComparecimentoResponse(LocalDate dataInicio, LocalDate dataFim, Integer totalPessoas,
                                           Intege