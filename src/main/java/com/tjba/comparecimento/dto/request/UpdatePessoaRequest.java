package com.tjba.comparecimento.dto.request;

import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import jakarta.validation.constraints.*;

/**
 * DTO para atualização de pessoa monitorada.
 */
public class UpdatePessoaRequest {

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

    private StatusComparecimento status;

    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    private String observacoes;

    // === VALIDAÇÃO PERSONALIZADA ===
    @AssertTrue(message = "Pelo menos CPF ou RG deve ser informado")
    public boolean isDocumentosValidos() {
        return (cpf != null && !cpf.trim().isEmpty()) ||
                (rg != null && !rg.trim().isEmpty());
    }

    // Constructors
    public UpdatePessoaRequest() {}

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

    public StatusComparecimento getStatus() {
        return status;
    }

    public void setStatus(StatusComparecimento status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }

    @Override
    public String toString() {
        return "UpdatePessoaRequest{" +
                "nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf + '\'' +
                ", rg='" + rg + '\'' +
                ", status=" + status +
                '}';
    }
}