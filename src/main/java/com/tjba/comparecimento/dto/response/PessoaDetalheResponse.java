package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta detalhada de pessoa monitorada.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PessoaDetalheResponse {

    // Dados pessoais
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String rg;
    private String contato;
    private String contatoEmergencia;
    private StatusComparecimento status;

    // Dados do processo
    private String numeroProcesso;
    private String vara;
    private String comarca;
    private LocalDate dataDecisao;

    // Dados do regime
    private Integer periodicidadeDias;
    private LocalDate dataComparecimentoInicial;
    private LocalDate proximoComparecimento;

    // Dados do endereço
    private String enderecoCompleto;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    // Outros
    private String observacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Constructors
    public PessoaDetalheResponse() {}

    public PessoaDetalheResponse(Long id, String nomeCompleto, String cpf, String rg, String contato,
                                 String contatoEmergencia, StatusComparecimento status, String numeroProcesso,
                                 String vara, String comarca, LocalDate dataDecisao, Integer periodicidadeDias,
                                 LocalDate dataComparecimentoInicial, LocalDate proximoComparecimento,
                                 String enderecoCompleto, String bairro, String cidade, String estado,
                                 String cep, String observacoes, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.rg = rg;
        this.contato = contato;
        this.contatoEmergencia = contatoEmergencia;
        this.status = status;
        this.numeroProcesso = numeroProcesso;
        this.vara = vara;
        this.comarca = comarca;
        this.dataDecisao = dataDecisao;
        this.periodicidadeDias = periodicidadeDias;
        this.dataComparecimentoInicial = dataComparecimentoInicial;
        this.proximoComparecimento = proximoComparecimento;
        this.enderecoCompleto = enderecoCompleto;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.observacoes = observacoes;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
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

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
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

    public LocalDate getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDate dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public Integer getPeriodicidadeDias() {
        return periodicidadeDias;
    }

    public void setPeriodicidadeDias(Integer periodicidadeDias) {
        this.periodicidadeDias = periodicidadeDias;
    }

    public LocalDate getDataComparecimentoInicial() {
        return dataComparecimentoInicial;
    }

    public void setDataComparecimentoInicial(LocalDate dataComparecimentoInicial) {
        this.dataComparecimentoInicial = dataComparecimentoInicial;
    }

    public LocalDate getProximoComparecimento() {
        return proximoComparecimento;
    }

    public void setProximoComparecimento(LocalDate proximoComparecimento) {
        this.proximoComparecimento = proximoComparecimento;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    @Override
    public String toString() {
        return "PessoaDetalheResponse{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf + '\'' +
                ", status=" + status +
                ", numeroProcesso='" + numeroProcesso + '\'' +
                ", comarca='" + comarca + '\'' +
                ", proximoComparecimento=" + proximoComparecimento +
                '}';
    }
}