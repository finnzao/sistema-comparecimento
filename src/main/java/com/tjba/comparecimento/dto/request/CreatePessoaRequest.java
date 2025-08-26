package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO para criação de pessoa monitorada.
 */
public class CreatePessoaRequest {

    // === DADOS PESSOAIS ===
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s'.-]+$", message = "Nome deve conter apenas letras e caracteres válidos")
    private String nomeCompleto;

    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$|^$", message = "CPF deve ter formato válido")
    private String cpf;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    private String rg;

    @NotBlank(message = "Contato é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Contato deve ter formato válido")
    private String contato;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^$", message = "Contato de emergência deve ter formato válido")
    private String contatoEmergencia;

    // === DADOS DO PROCESSO ===
    @NotBlank(message = "Número do processo é obrigatório")
    @Pattern(regexp = "^\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4}$",
            message = "Processo deve ter o formato 0000000-00.0000.0.00.0000")
    private String numeroProcesso;

    @NotBlank(message = "Vara é obrigatória")
    @Size(max = 100, message = "Vara deve ter no máximo 100 caracteres")
    private String vara;

    @NotBlank(message = "Comarca é obrigatória")
    @Size(max = 100, message = "Comarca deve ter no máximo 100 caracteres")
    private String comarca;

    @NotNull(message = "Data da decisão é obrigatória")
    @PastOrPresent(message = "Data da decisão não pode ser futura")
    private LocalDate dataDecisao;

    // === DADOS DO REGIME ===
    @NotNull(message = "Periodicidade é obrigatória")
    @Min(value = 1, message = "Periodicidade deve ser maior que zero")
    @Max(value = 365, message = "Periodicidade não pode ser maior que 365 dias")
    private Integer periodicidadeDias;

    @NotNull(message = "Data do primeiro comparecimento é obrigatória")
    private LocalDate dataComparecimentoInicial;

    // === DADOS DO ENDEREÇO ===
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve ter formato válido")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ser a sigla com 2 letras maiúsculas")
    private String estado;

    // === OUTROS ===
    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    private String observacoes;

    // === VALIDAÇÃO PERSONALIZADA ===
    @AssertTrue(message = "Pelo menos CPF ou RG deve ser informado")
    public boolean isDocumentosValidos() {
        return (cpf != null && !cpf.trim().isEmpty()) ||
                (rg != null && !rg.trim().isEmpty());
    }

    @AssertTrue(message = "Data do primeiro comparecimento deve ser posterior à data da decisão")
    public boolean isDataComparecimentoValida() {
        if (dataDecisao == null || dataComparecimentoInicial == null) {
            return true; // Deixa para validação individual dos campos
        }
        return !dataComparecimentoInicial.isBefore(dataDecisao);
    }

    // Constructors
    public CreatePessoaRequest() {}

    // Getters e Setters
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : null;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf != null ? cpf.trim() : null;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg != null ? rg.trim() : null;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato != null ? contato.trim() : null;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia != null ? contatoEmergencia.trim() : null;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso != null ? numeroProcesso.trim() : null;
    }

    public String getVara() {
        return vara;
    }

    public void setVara(String vara) {
        this.vara = vara != null ? vara.trim() : null;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca != null ? comarca.trim() : null;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep != null ? cep.trim() : null;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro != null ? logradouro.trim() : null;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero != null ? numero.trim() : null;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : null;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro != null ? bairro.trim() : null;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade != null ? cidade.trim() : null;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado != null ? estado.toUpperCase().trim() : null;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }

    @Override
    public String toString() {
        return "CreatePessoaRequest{" +
                "nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf + '\'' +
                ", numeroProcesso='" + numeroProcesso + '\'' +
                ", comarca='" + comarca + '\'' +
                '}';
    }
}