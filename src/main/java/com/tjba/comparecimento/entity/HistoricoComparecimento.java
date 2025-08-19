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
                @Index(name = "idx_historico_pessoa", columnList = "pessoa_id"),
                @Index(name = "idx_historico_data", columnList = "data_comparecimento"),
                @Index(name = "idx_historico_tipo", columnList = "tipo_validacao"),
                @Index(name = "idx_historico_pessoa_data", columnList = "pessoa_id, data_comparecimento DESC")
        }
)
@NamedQueries({
        @NamedQuery(
                name = "HistoricoComparecimento.findByPessoaOrderByDataDesc",
                query = "SELECT h FROM HistoricoComparecimento h WHERE h.pessoa = :pessoa ORDER BY h.dataComparecimento DESC"
        ),
        @NamedQuery(
                name = "HistoricoComparecimento.findByPessoaAndPeriodo",
                query = "SELECT h FROM HistoricoComparecimento h WHERE h.pessoa = :pessoa AND h.dataComparecimento BETWEEN :inicio AND :fim ORDER BY h.dataComparecimento DESC"
        ),
        @NamedQuery(
                name = "HistoricoComparecimento.countByPessoaAndTipo",
                query = "SELECT COUNT(h) FROM HistoricoComparecimento h WHERE h.pessoa = :pessoa AND h.tipoValidacao = :tipo"
        )
})
public class HistoricoComparecimento extends BaseEntity {

    @NotNull(message = "Pessoa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historico_pessoa"))
    private Pessoa pessoa;

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

    @Column(name = "anexos", columnDefinition = "jsonb")
    private String anexos;

    // Constructors
    public HistoricoComparecimento() {
        super();
    }

    public HistoricoComparecimento(Pessoa pessoa, LocalDate dataComparecimento,
                                   TipoValidacao tipoValidacao, String validadoPor) {
        this();
        this.pessoa = pessoa;
        this.dataComparecimento = dataComparecimento;
        this.tipoValidacao = tipoValidacao;
        this.validadoPor = validadoPor;
        this.horaComparecimento = LocalTime.now();
    }

    // Getters e Setters
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
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

    public String getAnexos() {
        return anexos;
    }

    public void setAnexos(String anexos) {
        this.anexos = anexos;
    }

    // Métodos utilitários
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoComparecimento that = (HistoricoComparecimento) o;
        return Objects.equals(pessoa, that.pessoa) &&
                Objects.equals(dataComparecimento, that.dataComparecimento) &&
                Objects.equals(horaComparecimento, that.horaComparecimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoa, dataComparecimento, horaComparecimento);
    }

    @Override
    public String toString() {
        return "HistoricoComparecimento{" +
                "id=" + getId() +
                ", pessoa=" + (pessoa != null ? pessoa.getNome() : "null") +
                ", dataComparecimento=" + dataComparecimento +
                ", tipoValidacao=" + tipoValidacao +
                '}';
    }
}
