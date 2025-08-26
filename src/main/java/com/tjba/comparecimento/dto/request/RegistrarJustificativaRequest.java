package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

// === JUSTIFICATIVA ===
public class RegistrarJustificativaRequest {

    @NotNull(message = "ID da pessoa é obrigatório")
    private Long pessoaId;

    @NotNull(message = "Data da ausência é obrigatória")
    @PastOrPresent(message = "Data da ausência não pode ser futura")
    private LocalDate dataAusencia;

    @NotBlank(message = "Motivo da justificativa é obrigatório")
    @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
    private String motivoJustificativa;

    @NotBlank(message = "Validado por é obrigatório")
    @Size(max = 100, message = "Validado por deve ter no máximo 100 caracteres")
    private String validadoPor;

    @Size(max = 5, message = "Máximo de 5 documentos anexados")
    private List<String> documentosAnexados;

    private boolean reagendarProximo = true;

    // === VALIDAÇÃO PERSONALIZADA ===
    @AssertTrue(message = "Data da ausência deve ser nos últimos 30 dias")
    public boolean isDataAusenciaValida() {
        if (dataAusencia == null) {
            return true; // Deixa para validação do @NotNull
        }
        LocalDate limiteInferior = LocalDate.now().minusDays(30);
        return !dataAusencia.isBefore(limiteInferior);
    }

    // Constructors
    public RegistrarJustificativaRequest() {}

    // Getters e Setters
    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public LocalDate getDataAusencia() {
        return dataAusencia;
    }

    public void setDataAusencia(LocalDate dataAusencia) {
        this.dataAusencia = dataAusencia;
    }

    public String getMotivoJustificativa() {
        return motivoJustificativa;
    }

    public void setMotivoJustificativa(String motivoJustificativa) {
        this.motivoJustificativa = motivoJustificativa != null ? motivoJustificativa.trim() : null;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor != null ? validadoPor.trim() : null;
    }

    public List<String> getDocumentosAnexados() {
        return documentosAnexados;
    }

    public void setDocumentosAnexados(List<String> documentosAnexados) {
        this.documentosAnexados = documentosAnexados;
    }

    public boolean isReagendarProximo() {
        return reagendarProximo;
    }

    public void setReagendarProximo(boolean reagendarProximo) {
        this.reagendarProximo = reagendarProximo;
    }

    @Override
    public String toString() {
        return "RegistrarJustificativaRequest{" +
                "pessoaId=" + pessoaId +
                ", dataAusencia=" + dataAusencia +
                ", motivoJustificativa='" + motivoJustificativa + '\'' +
                ", reagendarProximo=" + reagendarProximo +
                '}';
    }
}
