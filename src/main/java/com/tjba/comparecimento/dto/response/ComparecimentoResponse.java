package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tjba.comparecimento.entity.enums.TipoValidacao;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para resposta de comparecimento registrado.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComparecimentoResponse {

    private Long id;
    private Long pessoaId;
    private LocalDate dataComparecimento;
    private LocalTime horaComparecimento;
    private TipoValidacao tipoValidacao;
    private String validadoPor;
    private String observacoes;
    private LocalDate proximoComparecimento;

    // Constructors
    public ComparecimentoResponse() {}

    public ComparecimentoResponse(Long id, Long pessoaId, LocalDate dataComparecimento,
                                  LocalTime horaComparecimento, TipoValidacao tipoValidacao,
                                  String validadoPor, String observacoes, LocalDate proximoComparecimento) {
        this.id = id;
        this.pessoaId = pessoaId;
        this.dataComparecimento = dataComparecimento;
        this.horaComparecimento = horaComparecimento;
        this.tipoValidacao = tipoValidacao;
        this.validadoPor = validadoPor;
        this.observacoes = observacoes;
        this.proximoComparecimento = proximoComparecimento;
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

    public LocalDate getProximoComparecimento() {
        return proximoComparecimento;
    }

    public void setProximoComparecimento(LocalDate proximoComparecimento) {
        this.proximoComparecimento = proximoComparecimento;
    }

    @Override
    public String toString() {
        return "ComparecimentoResponse{" +
                "id=" + id +
                ", pessoaId=" + pessoaId +
                ", dataComparecimento=" + dataComparecimento +
                ", tipoValidacao=" + tipoValidacao +
                ", validadoPor='" + validadoPor + '\'' +
                ", proximoComparecimento=" + proximoComparecimento +
                '}';
    }
}