package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidade que representa o regime de comparecimento de uma pessoa monitorada.
 */
@Entity
@Table(name = "regimes_comparecimento",
        indexes = {
                @Index(name = "idx_regime_pessoa", columnList = "pessoa_monitorada_id"),
                @Index(name = "idx_regime_proximo_comparecimento", columnList = "proximo_comparecimento")
        }
)
public class RegimeComparecimento extends BaseEntity {

    @NotNull(message = "Periodicidade é obrigatória")
    @Min(value = 1, message = "Periodicidade deve ser maior que zero")
    @Max(value = 365, message = "Periodicidade não pode ser maior que 365 dias")
    @Column(name = "periodicidade_dias", nullable = false)
    private Integer periodicidadeDias;

    @NotNull(message = "Data do primeiro comparecimento é obrigatória")
    @Column(name = "data_comparecimento_inicial", nullable = false)
    private LocalDate dataComparecimentoInicial;

    @Column(name = "proximo_comparecimento")
    private LocalDate proximoComparecimento;

    // === RELACIONAMENTO ===
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_monitorada_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_regime_pessoa"))
    private PessoaMonitorada pessoaMonitorada;

    // === CONSTRUTORES ===
    public RegimeComparecimento() {
        super();
    }

    public RegimeComparecimento(Integer periodicidadeDias, LocalDate dataComparecimentoInicial) {
        this();
        this.periodicidadeDias = periodicidadeDias;
        this.dataComparecimentoInicial = dataComparecimentoInicial;
        this.proximoComparecimento = dataComparecimentoInicial;
    }

    // === MÉTODOS DE NEGÓCIO ===
    public void calcularProximoComparecimento() {
        if (dataComparecimentoInicial != null && periodicidadeDias != null) {
            this.proximoComparecimento = dataComparecimentoInicial.plusDays(periodicidadeDias);

            // Atualizar o status da pessoa automaticamente
            if (pessoaMonitorada != null) {
                pessoaMonitorada.atualizarStatusAutomatico();
            }
        }
    }

    public long getDiasAtraso() {
        if (proximoComparecimento == null) return 0;
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(proximoComparecimento) ?
                ChronoUnit.DAYS.between(proximoComparecimento, hoje) : 0;
    }

    public boolean isAtrasado() {
        return proximoComparecimento != null &&
                proximoComparecimento.isBefore(LocalDate.now());
    }

    public boolean isComparecimentoHoje() {
        return proximoComparecimento != null &&
                proximoComparecimento.equals(LocalDate.now());
    }

    public String getPeriodicidadeDescricao() {
        if (periodicidadeDias == null) return "Não definida";
        if (periodicidadeDias == 7) return "Semanal";
        if (periodicidadeDias == 15) return "Quinzenal";
        if (periodicidadeDias == 30) return "Mensal";
        if (periodicidadeDias == 60) return "Bimensal";
        if (periodicidadeDias == 90) return "Trimestral";
        if (periodicidadeDias == 180) return "Semestral";
        return periodicidadeDias + " dias";
    }

    // === GETTERS E SETTERS ===
    public Integer getPeriodicidadeDias() {
        return periodicidadeDias;
    }

    public void setPeriodicidadeDias(Integer periodicidadeDias) {
        this.periodicidadeDias = periodicidadeDias;
        calcularProximoComparecimento();
    }

    public LocalDate getDataComparecimentoInicial() {
        return dataComparecimentoInicial;
    }

    public void setDataComparecimentoInicial(LocalDate dataComparecimentoInicial) {
        this.dataComparecimentoInicial = dataComparecimentoInicial;
        calcularProximoComparecimento();
    }

    public LocalDate getProximoComparecimento() {
        return proximoComparecimento;
    }

    public void setProximoComparecimento(LocalDate proximoComparecimento) {
        this.proximoComparecimento = proximoComparecimento;
    }

    public PessoaMonitorada getPessoaMonitorada() {
        return pessoaMonitorada;
    }

    public void setPessoaMonitorada(PessoaMonitorada pessoaMonitorada) {
        this.pessoaMonitorada = pessoaMonitorada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegimeComparecimento that = (RegimeComparecimento) o;
        return Objects.equals(pessoaMonitorada, that.pessoaMonitorada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoaMonitorada);
    }

    @Override
    public String toString() {
        return "RegimeComparecimento{" +
                "id=" + getId() +
                ", periodicidadeDias=" + periodicidadeDias +
                ", dataComparecimentoInicial=" + dataComparecimentoInicial +
                ", proximoComparecimento=" + proximoComparecimento +
                '}';
    }
}