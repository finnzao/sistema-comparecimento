package com.tjba.comparecimento.dto.request;

import jakarta.validation.constraints.*;

// === COMPARECIMENTO VIRTUAL ===
public class RegistrarComparecimentoVirtualRequest extends RegistrarComparecimentoPresencialRequest {

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
