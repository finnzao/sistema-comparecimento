package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs para responses do dashboard.
 */

// === ESTATÍSTICAS GERAIS ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class EstatisticasGeraisResponse {

    private Integer totalPessoas;
    private Integer emConformidade;
    private Integer inadimplentes;
    private Integer comparecimentosHoje;
    private Integer atrasados;
    private Integer comparecimentosMes;
    private Double percentualConformidade;
    private Integer novosCadastrosMes;

    public EstatisticasGeraisResponse(Integer totalPessoas, Integer emConformidade, Integer inadimplentes,
                                      Integer comparecimentosHoje, Integer atrasados, Integer comparecimentosMes,
                                      Double percentualConformidade, Integer novosCadastrosMes) {
        this.totalPessoas = totalPessoas;
        this.emConformidade = emConformidade;
        this.inadimplentes = inadimplentes;
        this.comparecimentosHoje = comparecimentosHoje;
        this.atrasados = atrasados;
        this.comparecimentosMes = comparecimentosMes;
        this.percentualConformidade = percentualConformidade;
        this.novosCadastrosMes = novosCadastrosMes;
    }

    // Getters
    public Integer getTotalPessoas() { return totalPessoas; }
    public Integer getEmConformidade() { return emConformidade; }
    public Integer getInadimplentes() { return inadimplentes; }
    public Integer getComparecimentosHoje() { return comparecimentosHoje; }
    public Integer getAtrasados() { return atrasados; }
    public Integer getComparecimentosMes() { return comparecimentosMes; }
    public Double getPercentualConformidade() { return percentualConformidade; }
    public Integer getNovosCadastrosMes() { return novosCadastrosMes; }
}

// === ESTATÍSTICAS POR COMARCA ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class EstatisticaComarcaResponse {

    private String comarca;
    private Integer totalPessoas;
    private Integer emConformidade;
    private Integer inadimplentes;
    private Double percentualConformidade;

    public EstatisticaComarcaResponse(String comarca, Integer totalPessoas, Integer emConformidade,
                                      Integer inadimplentes, Double percentualConformidade) {
        this.comarca = comarca;
        this.totalPessoas = totalPessoas;
        this.emConformidade = emConformidade;
        this.inadimplentes = inadimplentes;
        this.percentualConformidade = percentualConformidade;
    }

    // Getters
    public String getComarca() { return comarca; }
    public Integer getTotalPessoas() { return totalPessoas; }
    public Integer getEmConformidade() { return emConformidade; }
    public Integer getInadimplentes() { return inadimplentes; }
    public Double getPercentualConformidade() { return percentualConformidade; }
}

// === PRÓXIMOS COMPARECIMENTOS ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class ProximoComparecimentoResponse {

    private LocalDate data;
    private Integer quantidade;
    private String diaSemana;

    public ProximoComparecimentoResponse(LocalDate data, Integer quantidade, String diaSemana) {
        this.data = data;
        this.quantidade = quantidade;
        this.diaSemana = diaSemana;
    }

    // Getters
    public LocalDate getData() { return data; }
    public Integer getQuantidade() { return quantidade; }
    public String getDiaSemana() { return diaSemana; }
}

// === GRÁFICO DE COMPARECIMENTOS ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class GraficoComparecimentosResponse {

    private List<String> labels;
    private List<Integer> dadosPresenciais;
    private List<Integer> dadosVirtuais;
    private List<Integer> dadosJustificativas;

    public GraficoComparecimentosResponse(List<String> labels, List<Integer> dadosPresenciais,
                                          List<Integer> dadosVirtuais, List<Integer> dadosJustificativas) {
        this.labels = labels;
        this.dadosPresenciais = dadosPresenciais;
        this.dadosVirtuais = dadosVirtuais;
        this.dadosJustificativas = dadosJustificativas;
    }

    // Getters
    public List<String> getLabels() { return labels; }
    public List<Integer> getDadosPresenciais() { return dadosPresenciais; }
    public List<Integer> getDadosVirtuais() { return dadosVirtuais; }
    public List<Integer> getDadosJustificativas() { return dadosJustificativas; }
}

// === ALERTAS ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class AlertaResponse {

    private String tipo; // warning, info, danger, success
    private String titulo;
    private String mensagem;
    private LocalDate data;
    private String prioridade; // high, medium, low

    public AlertaResponse(String tipo, String titulo, String mensagem, LocalDate data, String prioridade) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.data = data;
        this.prioridade = prioridade;
    }

    // Getters
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensagem() { return mensagem; }
    public LocalDate getData() { return data; }
    public String getPrioridade() { return prioridade; }
}

// === ATIVIDADES RECENTES ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class AtividadeRecenteResponse {

    private String tipo; // comparecimento, cadastro, alteracao
    private String descricao;
    private String usuario;
    private LocalDateTime dataHora;

    public AtividadeRecenteResponse(String tipo, String descricao, String usuario, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.usuario = usuario;
        this.dataHora = dataHora;
    }

    // Getters
    public String getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public String getUsuario() { return usuario; }
    public LocalDateTime getDataHora() { return dataHora; }
}

// === PERFORMANCE ===
@JsonInclude(JsonInclude.Include.NON_NULL)
class PerformanceResponse {

    private Double taxaComparecimento;
    private Double tempoMedioEntreComparecimentos;
    private Double percentualVirtual;
    private Integer totalRealizados;
    private Integer totalEsperados;

    public PerformanceResponse(Double taxaComparecimento, Double tempoMedioEntreComparecimentos,
                               Double percentualVirtual, Integer totalRealizados, Integer totalEsperados) {
        this.taxaComparecimento = taxaComparecimento;
        this.tempoMedioEntreComparecimentos = tempoMedioEntreComparecimentos;
        this.percentualVirtual = percentualVirtual;
        this.totalRealizados = totalRealizados;
        this.totalEsperados = totalEsperados;
    }

    // Getters
    public Double getTaxaComparecimento() { return taxaComparecimento; }
    public Double getTempoMedioEntreComparecimentos() { return tempoMedioEntreComparecimentos; }
    public Double getPercentualVirtual() { return percentualVirtual; }
    public Integer getTotalRealizados() { return totalRealizados; }
    public Integer getTotalEsperados() { return totalEsperados; }
}