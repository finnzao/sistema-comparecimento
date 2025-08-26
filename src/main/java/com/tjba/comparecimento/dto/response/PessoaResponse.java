package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta simplificada de pessoa monitorada (listagens).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PessoaResponse {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String rg;
    private String contato;
    private StatusComparecimento status;
    private String numeroProcesso;
    private String vara;
    private String comarca;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proximoComparecimento;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime criadoEm;

    // Constructors
    public PessoaResponse() {}

    public PessoaResponse(Long id, String nomeCompleto, String cpf, String rg, String contato,
                          StatusComparecimento status, String numeroProcesso, String vara, String comarca,
                          LocalDate proximoComparecimento, LocalDateTime criadoEm) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.rg = rg;
        this.contato = contato;
        this.status = status;
        this.numeroProcesso = numeroProcesso;
        this.vara = vara;
        this.comarca = comarca;
        this.proximoComparecimento = proximoComparecimento;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public StatusComparecimento getStatus() {
        return status;
    }

    public void setStatus(StatusComparecimento status) {
        this.status = status;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getVara() {
        return vara;
    }

    public void setVara(String vara) {
        this.vara = vara;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public LocalDate getProximoComparecimento() {
        return proximoComparecimento;
    }

    public void setProximoComparecimento(LocalDate proximoComparecimento) {
        this.proximoComparecimento = proximoComparecimento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return "PessoaResponse{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf + '\'' +
                ", status=" + status +
                ", comarca='" + comarca + '\'' +
                ", proximoComparecimento=" + proximoComparecimento +
                '}';
    }
}