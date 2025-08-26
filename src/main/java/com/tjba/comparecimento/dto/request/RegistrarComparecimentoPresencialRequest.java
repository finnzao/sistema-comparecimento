package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * DTOs para requests de comparecimento.
 */

// === COMPARECIMENTO PRESENCIAL ===
public class RegistrarComparecimentoPresencialRequest {

    @NotNull(message = "ID da pessoa é obrigatório")
    private Long pessoaId;

    @NotBlank(message = "Validado por é obrigatório")
    @Size(max = 100, message = "Validado por deve ter no máximo 100 caracteres")
    private String validadoPor;

    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    private String observacoes;

    // Constructors
    public RegistrarComparecimentoPresencialRequest() {}

    public RegistrarComparecimentoPresencialRequest(Long pessoaId, String validadoPor) {
        this.pessoaId = pessoaId;
        this.validadoPor = validadoPor;
    }

    // Getters e Setters
    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor != null ? validadoPor.trim() : null;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }

    @Override
    public String toString() {
        return "RegistrarComparecimentoPresencialRequest{" +
                "pessoaId=" + pessoaId +
                ", validadoPor='" + validadoPor + '\'' +
                '}';
    }
}

