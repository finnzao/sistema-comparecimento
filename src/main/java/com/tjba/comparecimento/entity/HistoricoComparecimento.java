package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidade que representa o histórico de comparecimentos de uma pessoa.
 */
@Entity
@Table(name = "historico_comparecimentos",
        indexes = {
                @Index(name = "idx_historico_pessoa", columnList = "pessoa_monitorada_id"),
                @Index(name = "idx_historico_data", columnList = "data_comparecimento"),
                @Index(name = "idx_historico_tipo", columnList = "tipo_validacao")
        }
)
public class HistoricoComparecimento extends BaseEntity {

    @NotNull(message = "Pessoa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_monitorada_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historico_pessoa"))
    private PessoaMonitorada pessoaMonitorada;

    @NotNull(message = "Data do comparecimento é obrigatória")
    @Column(name = "data_comparecimento", nullable = false)
    private LocalDate dataComparecimento;

    @Column(name = "hora_comparecimento")
    private LocalTime horaComparecimento;

    @NotNull(message = "Tipo de validação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_validacao", nullable = false, length = 20)
    private TipoValidacao tipoValidacao;

    @NotBlank(message = "Validado por é obrigatório")
    @Size(max = 100, message = "Validado por deve ter no máximo 100 caracteres")
    @Column(name = "validado_por", nullable = false, length = 100)
    private String validadoPor;

    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    @Column(name = "observacoes", length = 500)
    private String observacoes;

    // === CONSTRUTORES ===
    public HistoricoComparecimento() {
        super();
    }

    public HistoricoComparecimento(PessoaMonitorada pessoaMonitorada, LocalDate dataComparecimento,
                                   TipoValidacao tipoValidacao, String validadoPor) {
        this();
        this.pessoaMonitorada = pessoaMonitorada;
        this.dataComparecimento = dataComparecimento;
        this.tipoValidacao = tipoValidacao;
        this.validadoPor = validadoPor;
        this.horaComparecimento = LocalTime.now();
    }

    // === MÉTODOS UTILITÁRIOS ===
    public LocalDateTime getDataHoraComparecimento() {
        if (dataComparecimento == null) return null;
        if (horaComparecimento == null) return dataComparecimento.atStartOfDay();
        return dataComparecimento.atTime(horaComparecimento);
    }

    public String getResumo() {
        return String.format("%s - %s em %s",
                dataComparecimento,
                tipoValidacao.getLabel(),
                horaComparecimento != null ? horaComparecimento : "horário não informado");
    }

    public boolean isComparecimentoVirtual() {
        return tipoValidacao != null && tipoValidacao.isVirtual();
    }

    public boolean isJustificativa() {
        return tipoValidacao != null && tipoValidacao.isJustificativa();
    }

    // === GETTERS E SETTERS ===
    public PessoaMonitorada getPessoaMonitorada() {
        return pessoaMonitorada;
    }

    public void setPessoaMonitorada(PessoaMonitorada pessoaMonitorada) {
        this.pessoaMonitorada = pessoaMonitorada;
    }

    public LocalDate getDataComparecimento() {
        return dataComparecimento;
    }

    public void setDataComparecimento(LocalDate dataComparecimento) {
        this.dataComparecimento = dataComparecimento;
    }

    public LocalTime getHoraComparecimento() {
        return horaComparecimento;
    }

    public void setHoraComparecimento(LocalTime horaComparecimento) {
        this.horaComparecimento = horaComparecimento;
    }

    public TipoValidacao getTipoValidacao() {
        return tipoValidacao;
    }

    public void setTipoValidacao(TipoValidacao tipoValidacao) {
        this.tipoValidacao = tipoValidacao;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoComparecimento that = (HistoricoComparecimento) o;
        return Objects.equals(pessoaMonitorada, that.pessoaMonitorada) &&
                Objects.equals(dataComparecimento, that.dataComparecimento) &&
                Objects.equals(horaComparecimento, that.horaComparecimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoaMonitorada, dataComparecimento, horaComparecimento);
    }

    @Override
    public String toString() {
        return "HistoricoComparecimento{" +
                "id=" + getId() +
                ", pessoaMonitorada=" + (pessoaMonitorada != null ? pessoaMonitorada.getNomeCompleto() : "null") +
                ", dataComparecimento=" + dataComparecimento +
                ", tipoValidacao=" + tipoValidacao +
                '}';
    }
}