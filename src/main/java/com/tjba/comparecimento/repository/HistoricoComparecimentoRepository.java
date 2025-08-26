package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.HistoricoComparecimento;
import com.tjba.comparecimento.entity.enums.TipoValidacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade HistoricoComparecimento.
 */
@Repository
public interface HistoricoComparecimentoRepository extends JpaRepository<HistoricoComparecimento, Long> {

    /**
     * Buscar histórico por pessoa (paginado)
     */
    Page<HistoricoComparecimento> findByPessoaMonitoradaId(Long pessoaId, Pageable pageable);

    /**
     * Buscar histórico por pessoa ordenado por data descendente
     */
    List<HistoricoComparecimento> findByPessoaMonitoradaIdOrderByDataComparecimentoDesc(Long pessoaId);

    /**
     * Buscar último comparecimento de uma pessoa
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.pessoaMonitorada.id = :pessoaId " +
            "ORDER BY h.dataComparecimento DESC, h.horaComparecimento DESC")
    Optional<HistoricoComparecimento> findLastComparecimentoByPessoa(@Param("pessoaId") Long pessoaId);

    /**
     * Verificar se existe comparecimento na data para pessoa
     */
    boolean existsByPessoaMonitoradaIdAndDataComparecimento(Long pessoaId, LocalDate data);

    /**
     * Verificar se existe comparecimento na data para pessoa com tipo específico
     */
    boolean existsByPessoaMonitoradaIdAndDataComparecimentoAndTipoValidacao(Long pessoaId, LocalDate data, TipoValidacao tipo);

    /**
     * Buscar comparecimentos por período
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim")
    Page<HistoricoComparecimento> findByPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                                @Param("dataFim") LocalDate dataFim,
                                                Pageable pageable);

    /**
     * Buscar comparecimentos por período com filtros
     */
    @Query("SELECT h FROM HistoricoComparecimento h " +
            "JOIN h.pessoaMonitorada p " +
            "JOIN p.processoJudicial pj " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "AND (:tipoValidacao IS NULL OR h.tipoValidacao = :tipoValidacao) " +
            "AND (:comarca IS NULL OR pj.comarca = :comarca)")
    Page<HistoricoComparecimento> findByPeriodoWithFilters(@Param("dataInicio") LocalDate dataInicio,
                                                           @Param("dataFim") LocalDate dataFim,
                                                           @Param("tipoValidacao") TipoValidacao tipoValidacao,
                                                           @Param("comarca") String comarca,
                                                           Pageable pageable);

    /**
     * Buscar comparecimentos por período para relatório (sem paginação)
     */
    @Query("SELECT h FROM HistoricoComparecimento h " +
            "JOIN FETCH h.pessoaMonitorada p " +
            "JOIN FETCH p.processoJudicial pj " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "AND (:comarca IS NULL OR pj.comarca = :comarca) " +
            "ORDER BY h.dataComparecimento DESC, h.horaComparecimento DESC")
    List<HistoricoComparecimento> findByPeriodoWithFiltersForReport(@Param("dataInicio") LocalDate dataInicio,
                                                                    @Param("dataFim") LocalDate dataFim,
                                                                    @Param("comarca") String comarca);

    /**
     * Contar comparecimentos por período
     */
    @Query("SELECT COUNT(h) FROM HistoricoComparecimento h " +
            "JOIN h.pessoaMonitorada p " +
            "JOIN p.processoJudicial pj " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "AND (:comarca IS NULL OR pj.comarca = :comarca) " +
            "AND (:tipoValidacao IS NULL OR h.tipoValidacao = :tipoValidacao)")
    Long countByPeriodo(@Param("dataInicio") LocalDate dataInicio,
                        @Param("dataFim") LocalDate dataFim,
                        @Param("comarca") String comarca,
                        @Param("tipoValidacao") TipoValidacao tipoValidacao);

    /**
     * Contar comparecimentos por período e tipo
     */
    @Query("SELECT COUNT(h) FROM HistoricoComparecimento h " +
            "JOIN h.pessoaMonitorada p " +
            "JOIN p.processoJudicial pj " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "AND h.tipoValidacao = :tipoValidacao " +
            "AND (:comarca IS NULL OR pj.comarca = :comarca)")
    Long countByPeriodoAndTipo(@Param("dataInicio") LocalDate dataInicio,
                               @Param("dataFim") LocalDate dataFim,
                               @Param("comarca") String comarca,
                               @Param("tipoValidacao") TipoValidacao tipoValidacao);

    /**
     * Contar comparecimentos excluindo justificativas
     */
    @Query("SELECT COUNT(h) FROM HistoricoComparecimento h " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "AND h.tipoValidacao != :tipoExcluido")
    Long countByPeriodoExcluindoJustificativas(@Param("dataInicio") LocalDate dataInicio,
                                               @Param("dataFim") LocalDate dataFim,
                                               @Param("tipoExcluido") TipoValidacao tipoExcluido);

    /**
     * Buscar comparecimentos por pessoa em período
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.pessoaMonitorada.id = :pessoaId " +
            "AND h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY h.dataComparecimento DESC")
    List<HistoricoComparecimento> findByPessoaAndPeriodo(@Param("pessoaId") Long pessoaId,
                                                         @Param("dataInicio") LocalDate dataInicio,
                                                         @Param("dataFim") LocalDate dataFim);

    /**
     * Contar comparecimentos por pessoa e tipo
     */
    @Query("SELECT COUNT(h) FROM HistoricoComparecimento h WHERE h.pessoaMonitorada.id = :pessoaId " +
            "AND h.tipoValidacao = :tipoValidacao")
    Long countByPessoaAndTipo(@Param("pessoaId") Long pessoaId, @Param("tipoValidacao") TipoValidacao tipoValidacao);

    /**
     * Buscar últimos comparecimentos registrados
     */
    @Query("SELECT p.nomeCompleto, h.tipoValidacao, h.validadoPor, h.criadoEm " +
            "FROM HistoricoComparecimento h JOIN h.pessoaMonitorada p " +
            "ORDER BY h.criadoEm DESC")
    List<Object[]> findUltimosComparecimentos(Pageable pageable);

    /**
     * Buscar comparecimentos por validador
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.validadoPor = :validador " +
            "ORDER BY h.dataComparecimento DESC")
    Page<HistoricoComparecimento> findByValidadoPor(@Param("validador") String validador, Pageable pageable);

    /**
     * Contar comparecimentos por validador
     */
    Long countByValidadoPor(String validador);

    /**
     * Buscar estatísticas de comparecimentos por tipo
     */
    @Query("SELECT h.tipoValidacao, COUNT(h) FROM HistoricoComparecimento h " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY h.tipoValidacao")
    List<Object[]> countByTipoInPeriod(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar estatísticas de comparecimentos por mês
     */
    @Query("SELECT EXTRACT(YEAR FROM h.dataComparecimento), EXTRACT(MONTH FROM h.dataComparecimento), COUNT(h) " +
            "FROM HistoricoComparecimento h " +
            "WHERE h.dataComparecimento >= :dataInicio " +
            "GROUP BY EXTRACT(YEAR FROM h.dataComparecimento), EXTRACT(MONTH FROM h.dataComparecimento) " +
            "ORDER BY EXTRACT(YEAR FROM h.dataComparecimento), EXTRACT(MONTH FROM h.dataComparecimento)")
    List<Object[]> countByMes(@Param("dataInicio") LocalDate dataInicio);

    /**
     * Buscar estatísticas de comparecimentos por comarca
     */
    @Query("SELECT pj.comarca, COUNT(h) FROM HistoricoComparecimento h " +
            "JOIN h.pessoaMonitorada p " +
            "JOIN p.processoJudicial pj " +
            "WHERE h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY pj.comarca " +
            "ORDER BY COUNT(h) DESC")
    List<Object[]> countByComarcaInPeriod(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    /**
     * Calcular tempo médio entre comparecimentos
     */
    @Query(value = "SELECT AVG(CAST(h2.data_comparecimento - h1.data_comparecimento AS INTEGER)) " +
            "FROM historico_comparecimentos h1, historico_comparecimentos h2 " +
            "WHERE h1.pessoa_monitorada_id = h2.pessoa_monitorada_id " +
            "AND h1.data_comparecimento < h2.data_comparecimento " +
            "AND h1.tipo_validacao != ?1 " +
            "AND h2.tipo_validacao != ?1",
            nativeQuery = true)
    Double calcularTempoMedioEntreComparecimentos(String tipoExcluido);

    /**
     * Buscar comparecimentos virtuais com detalhes
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.tipoValidacao = :tipoVirtual " +
            "AND h.dataComparecimento BETWEEN :dataInicio AND :dataFim")
    List<HistoricoComparecimento> findComparecimentosVirtuais(@Param("tipoVirtual") TipoValidacao tipoVirtual,
                                                              @Param("dataInicio") LocalDate dataInicio,
                                                              @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar justificativas por período
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.tipoValidacao = :tipoJustificado " +
            "AND h.dataComparecimento BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY h.dataComparecimento DESC")
    List<HistoricoComparecimento> findJustificativas(@Param("tipoJustificado") TipoValidacao tipoJustificado,
                                                     @Param("dataInicio") LocalDate dataInicio,
                                                     @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar comparecimentos com observações específicas
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.observacoes IS NOT NULL " +
            "AND LOWER(h.observacoes) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<HistoricoComparecimento> findByObservacoesContaining(@Param("termo") String termo, Pageable pageable);

    /**
     * Buscar primeiro comparecimento de uma pessoa
     */
    @Query("SELECT h FROM HistoricoComparecimento h WHERE h.pessoaMonitorada.id = :pessoaId " +
            "ORDER BY h.dataComparecimento ASC, h.horaComparecimento ASC")
    Optional<HistoricoComparecimento> findFirstComparecimentoByPessoa(@Param("pessoaId") Long pessoaId);

    /**
     * Contar total de comparecimentos por pessoa
     */
    Long countByPessoaMonitoradaId(Long pessoaId);

    /**
     * Buscar comparecimentos por hora do dia (para análise de padrões)
     */
    @Query("SELECT EXTRACT(HOUR FROM h.horaComparecimento), COUNT(h) " +
            "FROM HistoricoComparecimento h " +
            "WHERE h.horaComparecimento IS NOT NULL " +
            "GROUP BY EXTRACT(HOUR FROM h.horaComparecimento) " +
            "ORDER BY EXTRACT(HOUR FROM h.horaComparecimento)")
    List<Object[]> countByHoraDoDia();
}