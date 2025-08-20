package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade que representa um processo judicial relacionado à pessoa monitorada.
 */
@Entity
@Table(name = "processos_judiciais",
        indexes = {
                @Index(name = "idx_processo_numero", columnList = "numero_processo", unique = true),
                @Index(name = "idx_processo_vara", columnList = "vara"),
                @Index(name = "idx_processo_comarca", columnList = "comarca")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_processo_numero", columnNames = "numero_processo")
        }
)
public class ProcessoJudicial extends BaseEntity {

    @NotBlank(message = "Número do processo é obrigatório")
    @Pattern(regexp = "\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4}",
            message = "Número do processo deve seguir o padrão CNJ (0000000-00.0000.0.00.0000)")
    @Column(name = "numero_processo", nullable = false, unique = true, length = 25)
    private String numeroProcesso;

    @NotBlank(message = "Vara é obrigatória")
    @Size(max = 100, message = "Vara deve ter no máximo 100 caracteres")
    @Column(name = "vara", nullable = false, length = 100)
    private String vara;

    @NotBlank(message = "Comarca é obrigatória")
    @Size(max = 100, message = "Comarca deve ter no máximo 100 caracteres")
    @Column(name = "comarca", nullable = false, length = 100)
    private String comarca;

    @NotNull(message = "Data da decisão é obrigatória")
    @PastOrPresent(message = "Data da decisão não pode ser futura")
    @Column(name = "data_decisao", nullable = false)
    private LocalDate dataDecisao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    // === RELACIONAMENTO ===
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_monitorada_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_processo_pessoa"))
    private PessoaMonitorada pessoaMonitorada;

    // === CONSTRUTORES ===
    public ProcessoJudicial() {
        super();
    }

    public ProcessoJudicial(String numeroProcesso, String vara, String comarca, LocalDate dataDecisao) {
        this();
        this.numeroProcesso = numeroProcesso;
        this.vara = vara;
        this.comarca = comarca;
        this.dataDecisao = dataDecisao;
    }

    // === MÉTODOS UTILITÁRIOS ===
    public String getResumoProcesso() {
        return String.format("%s - %s/%s", numeroProcesso, vara, comarca);
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    public void inativar() {
        this.ativo = Boolean.FALSE;
    }

    public void reativar() {
        this.ativo = Boolean.TRUE;
    }

    // === GETTERS E SETTERS ===
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getVara() {
        return vara;
    }

    public void setVara(String vara) {
        this.vara = vara;
    }

    public String getComarca() {
        return comarca;
    }

    public void setComarca(String comarca) {
        this.comarca = comarca;
    }

    public LocalDate getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDate dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
        ProcessoJudicial that = (ProcessoJudicial) o;
        return Objects.equals(numeroProcesso, that.numeroProcesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroProcesso);
    }

    @Override
    public String toString() {
        return "ProcessoJudicial{" +
                "id=" + getId() +
                ", numeroProcesso='" + numeroProcesso + '\'' +
                ", vara='" + vara + '\'' +
                ", comarca='" + comarca + '\'' +
                '}';
    }
}