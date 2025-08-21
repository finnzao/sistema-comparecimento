package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.RegimeComparecimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade RegimeComparecimento.
 */
@Repository
public interface RegimeComparecimentoRepository extends JpaRepository<RegimeComparecimento, Long> {

    /**
     * Buscar regime por pessoa
     */
    Optional<RegimeComparecimento> findByPessoaMonitoradaId(Long pessoaId);

    /**
     * Buscar regimes por periodicidade
     */
    List<RegimeComparecimento> findByPeriodicidadeDias(Integer periodicidade);

    /**
     * Buscar regimes com próximo comparecimento na data
     */
    List<RegimeComparecimento> findByProximoComparecimento(LocalDate data);

    /**
     * Buscar regimes com próximo comparecimento vencido
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.proximoComparecimento < :data")
    List<RegimeComparecimento> findComProximoComparecimentoVencido(@Param("data") LocalDate data);

    /**
     * Buscar regimes com próximo comparecimento entre datas
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.proximoComparecimento BETWEEN :dataInicio AND :dataFim")
    List<RegimeComparecimento> findByProximoComparecimentoBetween(@Param("dataInicio") LocalDate dataInicio,
                                                                  @Param("dataFim") LocalDate dataFim);

    /**
     * Contar regimes por periodicidade
     */
    Long countByPeriodicidadeDias(Integer periodicidade);

    /**
     * Buscar estatísticas de periodicidade
     */
    @Query("SELECT rc.periodicidadeDias, COUNT(rc) FROM RegimeComparecimento rc " +
            "GROUP BY rc.periodicidadeDias ORDER BY rc.periodicidadeDias")
    List<Object[]> countByPeriodicidade();

    /**
     * Buscar regimes com periodicidade mais comum
     */
    @Query("SELECT rc.periodicidadeDias FROM RegimeComparecimento rc " +
            "GROUP BY rc.periodicidadeDias " +
            "ORDER BY COUNT(rc) DESC")
    List<Integer> findPeriodicidadesMaisComuns(Pageable pageable);

    /**
     * Buscar regimes por data de comparecimento inicial
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.dataComparecimentoInicial BETWEEN :dataInicio AND :dataFim")
    Page<RegimeComparecimento> findByDataComparecimentoInicialBetween(@Param("dataInicio") LocalDate dataInicio,
                                                                      @Param("dataFim") LocalDate dataFim,
                                                                      Pageable pageable);

    /**
     * Buscar regimes sem próximo comparecimento definido
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.proximoComparecimento IS NULL")
    List<RegimeComparecimento> findSemProximoComparecimento();

    /**
     * Buscar regimes por comarca da pessoa
     */
    @Query("SELECT rc FROM RegimeComparecimento rc " +
            "JOIN rc.pessoaMonitorada pm " +
            "JOIN pm.processoJudicial pj " +
            "WHERE pj.comarca = :comarca")
    Page<RegimeComparecimento> findByComarca(@Param("comarca") String comarca, Pageable pageable);

    /**
     * Atualizar próximo comparecimento em lote para periodicidade específica
     */
    @Query("UPDATE RegimeComparecimento rc SET rc.proximoComparecimento = :novaData " +
            "WHERE rc.periodicidadeDias = :periodicidade AND rc.proximoComparecimento = :dataAtual")
    int updateProximoComparecimentoByPeriodicidade(@Param("novaData") LocalDate novaData,
                                                   @Param("periodicidade") Integer periodicidade,
                                                   @Param("dataAtual") LocalDate dataAtual);

    /**
     * Buscar regimes com periodicidade irregular (fora dos padrões)
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.periodicidadeDias NOT IN (7, 15, 30, 60, 90, 180)")
    List<RegimeComparecimento> findPeriodicidadeIrregular();

    /**
     * Buscar próximos vencimentos por comarca
     */
    @Query("SELECT rc FROM RegimeComparecimento rc " +
            "JOIN rc.pessoaMonitorada pm " +
            "JOIN pm.processoJudicial pj " +
            "WHERE pj.comarca = :comarca " +
            "AND rc.proximoComparecimento BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY rc.proximoComparecimento ASC")
    List<RegimeComparecimento> findProximosVencimentosByComarca(@Param("comarca") String comarca,
                                                                @Param("dataInicio") LocalDate dataInicio,
                                                                @Param("dataFim") LocalDate dataFim);

    /**
     * Calcular dias médios de periodicidade
     */
    @Query("SELECT AVG(rc.periodicidadeDias) FROM RegimeComparecimento rc")
    Double calcularPeriodicidadeMedia();

    /**
     * Buscar regimes por vara
     */
    @Query("SELECT rc FROM RegimeComparecimento rc " +
            "JOIN rc.pessoaMonitorada pm " +
            "JOIN pm.processoJudicial pj " +
            "WHERE pj.vara = :vara")
    Page<RegimeComparecimento> findByVara(@Param("vara") String vara, Pageable pageable);

    /**
     * Contar regimes vencidos por comarca
     */
    @Query("SELECT pj.comarca, COUNT(rc) FROM RegimeComparecimento rc " +
            "JOIN rc.pessoaMonitorada pm " +
            "JOIN pm.processoJudicial pj " +
            "WHERE rc.proximoComparecimento < :data " +
            "GROUP BY pj.comarca")
    List<Object[]> countVencidosByComarca(@Param("data") LocalDate data);

    /**
     * Buscar regimes criados recentemente
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.criadoEm >= :dataLimite ORDER BY rc.criadoEm DESC")
    List<RegimeComparecimento> findRegimesCriadosRecentemente(@Param("dataLimite") java.time.LocalDateTime dataLimite);

    /**
     * Buscar regimes que precisam de recalculo (data inicial muito antiga)
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE rc.dataComparecimentoInicial < :dataLimite")
    List<RegimeComparecimento> findRegimesParaRecalculo(@Param("dataLimite") LocalDate dataLimite);

    /**
     * Buscar regimes por padrão de periodicidade
     */
    @Query("SELECT rc FROM RegimeComparecimento rc WHERE " +
            "(:semanal = true AND rc.periodicidadeDias = 7) OR " +
            "(:quinzenal = true AND rc.periodicidadeDias = 15) OR " +
            "(:mensal = true AND rc.periodicidadeDias = 30) OR " +
            "(:bimensal = true AND rc.periodicidadeDias = 60) OR " +
            "(:trimestral = true AND rc.periodicidadeDias = 90) OR " +
            "(:semestral = true AND rc.periodicidadeDias = 180)")
    Page<RegimeComparecimento> findByPadraoPeriodicidade(@Param("semanal") boolean semanal,
                                                         @Param("quinzenal") boolean quinzenal,
                                                         @Param("mensal") boolean mensal,
                                                         @Param("bimensal") boolean bimensal,
                                                         @Param("trimestral") boolean trimestral,
                                                         @Param("semestral") boolean semestral,
                                                         Pageable pageable);
}