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

// === COMPARECIMENTO VIRTUAL ===
class RegistrarComparecimentoVirtualRequest extends RegistrarComparecimentoPresencialRequest {

    @NotBlank(message = "Plataforma é obrigatória")
    @Size(max = 50, message = "Plataforma deve ter no máximo 50 caracteres")
    private String plataforma; // Teams, Zoom, Google Meet, etc.

    @NotNull(message = "Duração é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior que zero")
    @Max(value = 480, message = "Duração não pode ser maior que 480 minutos (8 horas)")
    private Integer duracaoMinutos;

    @Size(max = 500, message = "Link da reunião deve ter no máximo 500 caracteres")
    @Pattern(regexp = "^(https?://.+)?$", message = "Link deve ser uma URL válida")
    private String linkReuniao;

    @Size(max = 300, message = "Observações virtuais deve ter no máximo 300 caracteres")
    private String observacoesVirtual;

    // Constructors
    public RegistrarComparecimentoVirtualRequest() {
        super();
    }

    // Getters e Setters
    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma != null ? plataforma.trim() : null;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getLinkReuniao() {
        return linkReuniao;
    }

    public void setLinkReuniao(String linkReuniao) {
        this.linkReuniao = linkReuniao != null ? linkReuniao.trim() : null;
    }

    public String getObservacoesVirtual() {
        return observacoesVirtual;
    }

    public void setObservacoesVirtual(String observacoesVirtual) {
        this.observacoesVirtual = observacoesVirtual != null ? observacoesVirtual.trim() : null;
    }

    @Override
    public String toString() {
        return "RegistrarComparecimentoVirtualRequest{" +
                "pessoaId=" + getPessoaId() +
                ", plataforma='" + plataforma + '\'' +
                ", duracaoMinutos=" + duracaoMinutos +
                ", validadoPor='" + getValidadoPor() + '\'' +
                '}';
    }
}

// === JUSTIFICATIVA ===
class RegistrarJustificativaRequest {

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

// === ATUALIZAR PRÓXIMO COMPARECIMENTO ===
class AtualizarProximoComparecimentoRequest {

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