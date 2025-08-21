package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tjba.comparecimento.entity.enums.TipoValidacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para resposta de histórico de comparecimento.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoricoComparecimentoResponse {

    private Long id;
    private Long pessoaId;
    private String nomePessoa;
    private LocalDate dataComparecimento;
    private LocalTime horaComparecimento;
    private TipoValidacao tipoValidacao;
    private String validadoPor;
    private String observacoes;
    private LocalDateTime criadoEm;

    // Constructors
    public HistoricoComparecimentoResponse() {}

    public HistoricoComparecimentoResponse(Long id, Long pessoaId, String nomePessoa,
                                           LocalDate dataComparecimento, LocalTime horaComparecimento,
                                           TipoValidacao tipoValidacao, String validadoPor,
                                           String observacoes, LocalDateTime criadoEm) {
        this.id = id;
        this.pessoaId = pessoaId;
        this.nomePessoa = nomePessoa;
        this.dataComparecimento = dataComparecimento;
        this.horaComparecimento = horaComparecimento;
        this.tipoValidacao = tipoValidacao;
        this.validadoPor = validadoPor;
        this.observacoes = observacoes;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public LocalDate getDataComparecimento() {
        return dataComparecimento;
    }

    public void setDataComparecimento(LocalDate dataComparecimento) {
        this.dataComparecimento = dataComparecimento;
    }

    public LocalTime getHoraComparecimento() {
        return horaComparecimento;
    }

    public void setHoraComparecimento(LocalTime horaComparecimento) {
        this.horaComparecimento = horaComparecimento;
    }

    public TipoValidacao getTipoValidacao() {
        return tipoValidacao;
    }

    public void setTipoValidacao(TipoValidacao tipoValidacao) {
        this.tipoValidacao = tipoValidacao;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return "HistoricoComparecimentoResponse{" +
                "id=" + id +
                ", nomePessoa='" + nomePessoa + '\'' +
                ", dataComparecimento=" + dataComparecimento +
                ", tipoValidacao=" + tipoValidacao +
                ", validadoPor='" + validadoPor + '\'' +
                '}';
    }
}