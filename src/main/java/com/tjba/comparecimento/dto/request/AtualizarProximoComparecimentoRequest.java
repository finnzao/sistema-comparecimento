package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

// === ATUALIZAR PRÓXIMO COMPARECIMENTO ===
public class AtualizarProximoComparecimentoRequest {

    @NotNull(message = "Nova data é obrigatória")
    @Future(message = "Nova data deve ser futura")
    private LocalDate novaData;

    @NotBlank(message = "Motivo da alteração é obrigatório")
    @Size(min = 10, max = 200, message = "Motivo deve ter entre 10 e 200 caracteres")
    private String motivoAlteracao;

    @NotBlank(message = "Validado por é obrigatório")
    @Size(max = 100, message = "Validado por deve ter no máximo 100 caracteres")
    private String validadoPor;

    // === VALIDAÇÃO PERSONALIZADA ===
    @AssertTrue(message = "Nova data não pode ser superior a 1 ano")
    public boolean isNovaDataValida() {
        if (novaData == null) {
            return true; // Deixa para validação do @NotNull
        }
        LocalDate limiteSuperior = LocalDate.now().plusYears(1);
        return !novaData.isAfter(limiteSuperior);
    }

    // Constructors
    public AtualizarProximoComparecimentoRequest() {}

    // Getters e Setters
    public LocalDate getNovaData() {
        return novaData;
    }

    public void setNovaData(LocalDate novaData) {
        this.novaData = novaData;
    }

    public String getMotivoAlteracao() {
        return motivoAlteracao;
    }

    public void setMotivoAlteracao(String motivoAlteracao) {
        this.motivoAlteracao = motivoAlteracao != null ? motivoAlteracao.trim() : null;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor != null ? validadoPor.trim() : null;
    }

    @Override
    public String toString() {
        return "AtualizarProximoComparecimentoRequest{" +
                "novaData=" + novaData +
                ", motivoAlteracao='" + motivoAlteracao + '\'' +
                ", validadoPor='" + validadoPor + '\'' +
                '}';
    }
}
