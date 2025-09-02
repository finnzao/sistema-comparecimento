package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.ProcessoJudicial;
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
 * Repository para entidade ProcessoJudicial.
 */
@Repository
public interface ProcessoJudicialRepository extends JpaRepository<ProcessoJudicial, Long> {

    /**
     * Buscar processo por número
     */
    Optional<ProcessoJudicial> findByNumeroProcesso(String numeroProcesso);

    /**
     * Verificar se existe processo com número
     */
    boolean existsByNumeroProcesso(String numeroProcesso);

    /**
     * Verificar se existe processo com número exceto ID específico
     */
    boolean existsByNumeroProcessoAndPessoaMonitoradaIdNot(String numeroProcesso, Long pessoaId);

    /**
     * Buscar processos por comarca
     */
    Page<ProcessoJudicial> findByComarca(String comarca, Pageable pageable);

    /**
     * Buscar processos por vara
     */
    Page<ProcessoJudicial> findByVara(String vara, Pageable pageable);

    /**
     * Buscar processos ativos
     */
    Page<ProcessoJudicial> findByAtivoTrue(Pageable pageable);

    /**
     * Buscar processos inativos
     */
    Page<ProcessoJudicial> findByAtivoFalse(Pageable pageable);

    /**
     * Buscar comarcas distintas
     */
    @Query("SELECT DISTINCT p.comarca FROM ProcessoJudicial p WHERE p.ativo = true ORDER BY p.comarca")
    List<String> findDistinctComarcas();

    /**
     * Buscar varas distintas
     */
    @Query("SELECT DISTINCT p.vara FROM ProcessoJudicial p WHERE p.ativo = true ORDER BY p.vara")
    List<String> findDistinctVaras();

    /**
     * Buscar varas por comarca
     */
    @Query("SELECT DISTINCT p.vara FROM ProcessoJudicial p WHERE p.comarca = :comarca AND p.ativo = true ORDER BY p.vara")
    List<String> findDistinctVarasByComarca(@Param("comarca") String comarca);

    /**
     * Contar processos por comarca
     */
    Long countByComarcaAndAtivoTrue(String comarca);

    /**
     * Contar processos por vara
     */
    Long countByVaraAndAtivoTrue(String vara);

    /**
     * Buscar processos por período de decisão
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.dataDecisao BETWEEN :dataInicio AND :dataFim AND p.ativo = true")
    Page<ProcessoJudicial> findByDataDecisaoBetween(@Param("dataInicio") LocalDate dataInicio,
                                                    @Param("dataFim") LocalDate dataFim,
                                                    Pageable pageable);

    /**
     * Buscar processos com filtros
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE " +
            "(:numeroProcesso IS NULL OR LOWER(p.numeroProcesso) LIKE LOWER(CONCAT('%', :numeroProcesso, '%'))) AND " +
            "(:comarca IS NULL OR LOWER(p.comarca) LIKE LOWER(CONCAT('%', :comarca, '%'))) AND " +
            "(:vara IS NULL OR LOWER(p.vara) LIKE LOWER(CONCAT('%', :vara, '%'))) AND " +
            "(:ativo IS NULL OR p.ativo = :ativo)")
    Page<ProcessoJudicial> findWithFilters(@Param("numeroProcesso") String numeroProcesso,
                                           @Param("comarca") String comarca,
                                           @Param("vara") String vara,
                                           @Param("ativo") Boolean ativo,
                                           Pageable pageable);

    /**
     * Buscar estatísticas por comarca
     */
    @Query("SELECT p.comarca, COUNT(p) FROM ProcessoJudicial p WHERE p.ativo = true GROUP BY p.comarca ORDER BY COUNT(p) DESC")
    List<Object[]> countByComarca();

    /**
     * Buscar estatísticas por vara
     */
    @Query("SELECT p.vara, COUNT(p) FROM ProcessoJudicial p WHERE p.ativo = true GROUP BY p.vara ORDER BY COUNT(p) DESC")
    List<Object[]> countByVara();

    /**
     * Buscar processos por ano da decisão
     */
    @Query("SELECT EXTRACT(YEAR FROM p.dataDecisao), COUNT(p) FROM ProcessoJudicial p " +
            "WHERE p.ativo = true " +
            "GROUP BY EXTRACT(YEAR FROM p.dataDecisao) " +
            "ORDER BY EXTRACT(YEAR FROM p.dataDecisao) DESC")
    List<Object[]> countByAnoDecisao();

    /**
     * Buscar processos mais antigos
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.ativo = true ORDER BY p.dataDecisao ASC")
    Page<ProcessoJudicial> findOldestProcesses(Pageable pageable);

    /**
     * Buscar processos mais recentes
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.ativo = true ORDER BY p.dataDecisao DESC")
    Page<ProcessoJudicial> findNewestProcesses(Pageable pageable);

    /**
     * Contar processos ativos
     */
    Long countByAtivoTrue();

    /**
     * Contar processos inativos
     */
    Long countByAtivoFalse();

    /**
     * Buscar processos sem pessoa associada (órfãos)
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.pessoaMonitorada IS NULL")
    List<ProcessoJudicial> findProcessosOrfaos();

    /**
     * Buscar processos por padrão de numeração CNJ
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE p.numeroProcesso LIKE :padrao AND p.ativo = true")
    Page<ProcessoJudicial> findByPadraoNumeracao(@Param("padrao") String padrao, Pageable pageable);

    /**
     * Validar formato do número do processo (CNJ) - versão corrigida usando SQL nativo
     */
    @Query(value = "SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM processos_judiciais p " +
            "WHERE p.numero_processo = :numeroProcesso AND p.numero_processo ~ '^[0-9]{7}-[0-9]{2}\\.[0-9]{4}\\.[0-9]{1}\\.[0-9]{2}\\.[0-9]{4}$'",
            nativeQuery = true)
    boolean isNumeroProcessoValidFormat(@Param("numeroProcesso") String numeroProcesso);

    /**
     * Buscar processos por tribunal (baseado no código do número CNJ)
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE SUBSTRING(p.numeroProcesso, 14, 2) = :codigoTribunal AND p.ativo = true")
    Page<ProcessoJudicial> findByCodigoTribunal(@Param("codigoTribunal") String codigoTribunal, Pageable pageable);

    /**
     * Buscar processos por órgão julgador (baseado no código do número CNJ)
     */
    @Query("SELECT p FROM ProcessoJudicial p WHERE SUBSTRING(p.numeroProcesso, 17, 4) = :codigoOrgao AND p.ativo = true")
    Page<ProcessoJudicial> findByCodigoOrgaoJulgador(@Param("codigoOrgao") String codigoOrgao, Pageable pageable);
}