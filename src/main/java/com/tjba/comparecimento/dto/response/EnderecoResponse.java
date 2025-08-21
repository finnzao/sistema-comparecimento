package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para resposta de endereço.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnderecoResponse {

    private Long id;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String enderecoCompleto;
    private String enderecoResumido;
    private Long pessoaId;

    // Constructors
    public EnderecoResponse() {}

    public EnderecoResponse(Long id, String cep, String logradouro, String numero, String complemento,
                            String bairro, String cidade, String estado, String enderecoCompleto,
                            String enderecoResumido, Long pessoaId) {
        this.id = id;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.enderecoCompleto = enderecoCompleto;
        this.enderecoResumido = enderecoResumido;
        this.pessoaId = pessoaId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getEnderecoResumido() {
        return enderecoResumido;
    }

    public void setEnderecoResumido(String enderecoResumido) {
        this.enderecoResumido = enderecoResumido;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    @Override
    public String toString() {
        return "EnderecoResponse{" +
                "id=" + id +
                ", cep='" + cep + '\'' +
                ", enderecoCompleto='" + enderecoCompleto + '\'' +
                ", pessoaId=" + pessoaId +
                '}';
    }
}