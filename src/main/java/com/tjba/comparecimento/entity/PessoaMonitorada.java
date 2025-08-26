package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa uma pessoa em liberdade provisória sob monitoramento judicial.
 * Contém apenas os dados pessoais básicos conforme formulário.
 */
@Entity
@Table(name = "pessoas_monitoradas",
        indexes = {
                @Index(name = "idx_pessoa_cpf", columnList = "cpf"),
                @Index(name = "idx_pessoa_rg", columnList = "rg"),
                @Index(name = "idx_pessoa_status", columnList = "status"),
                @Index(name = "idx_pessoa_nome", columnList = "nome_completo")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_pessoa_rg", columnNames = "rg")
        }
)
public class PessoaMonitorada extends BaseEntity {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s'.-]+$", message = "Nome deve conter apenas letras e caracteres válidos")
    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF deve ter formato válido")
    @Column(name = "cpf", unique = true, length = 14)
    private String cpf;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @Column(name = "rg", unique = true, length = 20)
    private String rg;

    @NotBlank(message = "Contato é obrigatório")
    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Contato deve ter formato válido")
    @Column(name = "contato", nullable = false, length = 20)
    private String contato;

    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Contato de emergência deve ter formato válido")
    @Column(name = "contato_emergencia", length = 20)
    private String contatoEmergencia;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusComparecimento status;

    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    // === RELACIONAMENTOS ===
    @Valid
    @OneToOne(mappedBy = "pessoaMonitorada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProcessoJudicial processoJudicial;

    @Valid
    @OneToOne(mappedBy = "pessoaMonitorada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RegimeComparecimento regimeComparecimento;

    @Valid
    @OneToOne(mappedBy = "pessoaMonitorada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EnderecoVinculado endereco;

    @OneToMany(mappedBy = "pessoaMonitorada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("dataComparecimento DESC")
    private List<HistoricoComparecimento> historicoComparecimentos = new ArrayList<>();


    // === VALIDAÇÕES PERSONALIZADAS ===
    @AssertTrue(message = "Pelo menos CPF ou RG deve ser informado")
    public boolean isDocumentosValidos() {
        return (cpf != null && !cpf.trim().isEmpty()) ||
                (rg != null && !rg.trim().isEmpty());
    }

    // === CONSTRUTORES ===
    public PessoaMonitorada() {
        super();
        this.status = StatusComparecimento.EM_CONFORMIDADE;
    }

    public PessoaMonitorada(String nomeCompleto, String contato) {
        this();
        this.nomeCompleto = nomeCompleto;
        this.contato = contato;
    }

    // === MÉTODOS UTILITÁRIOS ===
    public String getIdentificacaoCompleta() {
        StringBuilder sb = new StringBuilder(nomeCompleto);
        if (cpf != null && !cpf.trim().isEmpty()) {
            sb.append(" - CPF: ").append(cpf);
        } else if (rg != null && !rg.trim().isEmpty()) {
            sb.append(" - RG: ").append(rg);
        }
        return sb.toString();
    }

    public boolean temProcessoAtivo() {
        return processoJudicial != null && processoJudicial.isAtivo();
    }

    /**
     * Método para atualizar automaticamente o status baseado no regime de comparecimento
     */
    public void atualizarStatusAutomatico() {
        if (regimeComparecimento != null && regimeComparecimento.getProximoComparecimento() != null) {
            LocalDate hoje = LocalDate.now();
            LocalDate proximoComparecimento = regimeComparecimento.getProximoComparecimento();

            if (proximoComparecimento.isBefore(hoje)) {
                this.status = StatusComparecimento.INADIMPLENTE;
            } else {
                this.status = StatusComparecimento.EM_CONFORMIDADE;
            }
        }
    }

    // === FORMATAÇÃO AUTOMÁTICA ===
    public void setCpf(String cpf) {
        if (cpf != null) {
            String digits = cpf.replaceAll("[^\\d]", "");
            if (digits.length() == 11) {
                this.cpf = digits.substring(0, 3) + "." +
                        digits.substring(3, 6) + "." +
                        digits.substring(6, 9) + "-" +
                        digits.substring(9);
            } else {
                this.cpf = cpf;
            }
        } else {
            this.cpf = null;
        }
    }

    // === GETTERS E SETTERS ===
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : null;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public StatusComparecimento getStatus() {
        return status;
    }

    public void setStatus(StatusComparecimento status) {
        this.status = status;
    }

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public RegimeComparecimento getRegimeComparecimento() {
        return regimeComparecimento;
    }

    public void setRegimeComparecimento(RegimeComparecimento regimeComparecimento) {
        this.regimeComparecimento = regimeComparecimento;
    }

    public EnderecoVinculado getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoVinculado endereco) {
        this.endereco = endereco;
    }

    public List<HistoricoComparecimento> getHistoricoComparecimentos() {
        return historicoComparecimentos;
    }

    public void setHistoricoComparecimentos(List<HistoricoComparecimento> historicoComparecimentos) {
        this.historicoComparecimentos = historicoComparecimentos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PessoaMonitorada that = (PessoaMonitorada) o;
        return Objects.equals(cpf, that.cpf) || Objects.equals(rg, that.rg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf, rg);
    }

    @Override
    public String toString() {
        return "PessoaMonitorada{" +
                "id=" + getId() +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", status=" + status +
                '}';
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }
}