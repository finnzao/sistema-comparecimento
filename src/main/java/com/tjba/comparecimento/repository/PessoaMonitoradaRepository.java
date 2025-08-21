package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.PessoaMonitorada;
import com.tjba.comparecimento.entity.enums.StatusComparecimento;
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
 * Repository para entidade PessoaMonitorada.
 */
@Repository
public interface PessoaMonitoradaRepository extends JpaRepository<PessoaMonitorada, Long> {

    /**
     * Buscar pessoa por CPF
     */
    Optional<PessoaMonitorada> findByCpf(String cpf);

    /**
     * Buscar pessoa por RG
     */
    Optional<PessoaMonitorada> findByRg(String rg);

    /**
     * Verificar se existe pessoa com CPF
     */
    boolean existsByCpf(String cpf);

    /**
     * Verificar se existe pessoa com RG
     */
    boolean existsByRg(String rg);

    /**
     * Verificar se existe pessoa com CPF exceto ID específico
     */
    boolean existsByCpfAndIdNot(String cpf, Long id);

    /**
     * Verificar se existe pessoa com RG exceto ID específico
     */
    boolean existsByRgAndIdNot(String rg, Long id);

    /**
     * Buscar pessoa por número do processo
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.processoJudicial pj WHERE pj.numeroProcesso = :numeroProcesso")
    Optional<PessoaMonitorada> findByProcessoNumero(@Param("numeroProcesso") String numeroProcesso);

    /**
     * Buscar pessoa por ID com todos os detalhes (eager loading)
     */
    @Query("SELECT p FROM PessoaMonitorada p " +
            "LEFT JOIN FETCH p.processoJudicial " +
            "LEFT JOIN FETCH p.regimeComparecimento " +
            "LEFT JOIN FETCH p.endereco " +
            "WHERE p.id = :id")
    Optional<PessoaMonitorada> findByIdWithDetails(@Param("id") Long id);

    /**
     * Buscar pessoas com filtros
     */
    @Query("SELECT p FROM PessoaMonitorada p " +
            "LEFT JOIN p.processoJudicial pj " +
            "LEFT JOIN p.regimeComparecimento rc " +
            "WHERE (:nome IS NULL OR LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:cpf IS NULL OR p.cpf = :cpf) AND " +
            "(:rg IS NULL OR p.rg = :rg) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:comarca IS NULL OR LOWER(pj.comarca) LIKE LOWER(CONCAT('%', :comarca, '%'))) AND " +
            "(:proximoComparecimento IS NULL OR rc.proximoComparecimento = :proximoComparecimento)")
    Page<PessoaMonitorada> findAllWithFilters(@Param("nome") String nome,
                                              @Param("cpf") String cpf,
                                              @Param("rg") String rg,
                                              @Param("status") StatusComparecimento status,
                                              @Param("comarca") String comarca,
                                              @Param("proximoComparecimento") LocalDate proximoComparecimento,
                                              Pageable pageable);

    /**
     * Buscar pessoas por status
     */
    List<PessoaMonitorada> findByStatus(StatusComparecimento status);

    /**
     * Contar pessoas por status
     */
    Long countByStatus(StatusComparecimento status);

    /**
     * Buscar pessoas com próximo comparecimento na data
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.regimeComparecimento rc WHERE rc.proximoComparecimento = :data")
    List<PessoaMonitorada> findByProximoComparecimento(@Param("data") LocalDate data);

    /**
     * Contar pessoas com próximo comparecimento na data
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p JOIN p.regimeComparecimento rc WHERE rc.proximoComparecimento = :data")
    Long countByProximoComparecimento(@Param("data") LocalDate data);

    /**
     * Buscar pessoas em atraso (próximo comparecimento vencido)
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.regimeComparecimento rc WHERE rc.proximoComparecimento < :data")
    List<PessoaMonitorada> findAtrasadas(@Param("data") LocalDate data);

    /**
     * Contar pessoas com próximo comparecimento vencido
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p JOIN p.regimeComparecimento rc WHERE rc.proximoComparecimento < :data")
    Long countByProximoComparecimentoVencido(@Param("data") LocalDate data);

    /**
     * Buscar pessoas com próximo comparecimento vencido (para atualização automática)
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.regimeComparecimento rc " +
            "WHERE rc.proximoComparecimento < :data AND p.status = :status")
    List<PessoaMonitorada> findComProximoComparecimentoVencido(@Param("data") LocalDate data,
                                                               @Param("status") StatusComparecimento status);

    /**
     * Buscar pessoas com próximo comparecimento entre datas
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p JOIN p.regimeComparecimento rc " +
            "WHERE rc.proximoComparecimento BETWEEN :dataInicio AND :dataFim")
    Long countByProximoComparecimentoEntre(@Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar próximos vencimentos
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.regimeComparecimento rc " +
            "WHERE rc.proximoComparecimento BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY rc.proximoComparecimento ASC")
    List<PessoaMonitorada> findProximosVencimentos(@Param("dataInicio") LocalDate dataInicio,
                                                   @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar pessoas por comarca
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.processoJudicial pj WHERE pj.comarca = :comarca")
    Page<PessoaMonitorada> findByComarca(@Param("comarca") String comarca, Pageable pageable);

    /**
     * Contar pessoas por comarca
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p JOIN p.processoJudicial pj WHERE pj.comarca = :comarca")
    Long countByComarca(@Param("comarca") String comarca);

    /**
     * Contar pessoas por comarca e status
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p JOIN p.processoJudicial pj " +
            "WHERE pj.comarca = :comarca AND p.status = :status")
    Long countByComarcaAndStatus(@Param("comarca") String comarca, @Param("status") StatusComparecimento status);

    /**
     * Buscar comarcas distintas
     */
    @Query("SELECT DISTINCT pj.comarca FROM PessoaMonitorada p JOIN p.processoJudicial pj ORDER BY pj.comarca")
    List<String> findDistinctComarcas();

    /**
     * Contar pessoas criadas em período
     */
    @Query("SELECT COUNT(p) FROM PessoaMonitorada p WHERE p.criadoEm BETWEEN :dataInicio AND :dataFim")
    Long countByPeriodoCriacao(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Buscar últimas pessoas cadastradas
     */
    @Query("SELECT p.nomeCompleto, p.criadoEm FROM PessoaMonitorada p ORDER BY p.criadoEm DESC")
    List<Object[]> findUltimasPessoas(Pageable pageable);

    /**
     * Buscar pessoas para relatório
     */
    @Query("SELECT p FROM PessoaMonitorada p " +
            "LEFT JOIN FETCH p.processoJudicial pj " +
            "LEFT JOIN FETCH p.regimeComparecimento rc " +
            "LEFT JOIN FETCH p.endereco e " +
            "WHERE (:comarca IS NULL OR pj.comarca = :comarca) AND " +
            "(:status IS NULL OR p.status = :status) " +
            "ORDER BY p.nomeCompleto")
    List<PessoaMonitorada> findForRelatorio(@Param("comarca") String comarca, @Param("status") String status);

    /**
     * Contar comparecimentos esperados em período
     */
    @Query("SELECT COUNT(DISTINCT p) FROM PessoaMonitorada p JOIN p.regimeComparecimento rc " +
            "WHERE rc.proximoComparecimento BETWEEN :dataInicio AND :dataFim")
    Long countComparecimentosEsperados(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    /**
     * Buscar pessoas por vara
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.processoJudicial pj WHERE pj.vara = :vara")
    Page<PessoaMonitorada> findByVara(@Param("vara") String vara, Pageable pageable);

    /**
     * Buscar estatísticas por estado (baseado no endereço)
     */
    @Query("SELECT e.estado, COUNT(p) FROM PessoaMonitorada p JOIN p.endereco e GROUP BY e.estado ORDER BY COUNT(p) DESC")
    List<Object[]> countByEstado();

    /**
     * Buscar pessoas por periodicidade de comparecimento
     */
    @Query("SELECT p FROM PessoaMonitorada p JOIN p.regimeComparecimento rc WHERE rc.periodicidadeDias = :periodicidade")
    List<PessoaMonitorada> findByPeriodicidade(@Param("periodicidade") Integer periodicidade);

    /**
     * Buscar pessoas com regime de comparecimento não configurado
     */
    @Query("SELECT p FROM PessoaMonitorada p WHERE p.regimeComparecimento IS NULL")
    List<PessoaMonitorada> findSemRegimeComparecimento();

    /**
     * Buscar pessoas com endereço não configurado
     */
    @Query("SELECT p FROM PessoaMonitorada p WHERE p.endereco IS NULL")
    List<PessoaMonitorada> findSemEndereco();
}