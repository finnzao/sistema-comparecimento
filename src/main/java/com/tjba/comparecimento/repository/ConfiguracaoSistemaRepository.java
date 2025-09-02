package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.ConfiguracaoSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade ConfiguracaoSistema.
 */
@Repository
public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistema, Long> {

    /**
     * Buscar configuração por chave
     */
    Optional<ConfiguracaoSistema> findByChave(String chave);

    /**
     * Verificar se existe configuração com chave
     */
    boolean existsByChave(String chave);

    /**
     * Buscar todas as configurações ordenadas por chave
     */
    @Query("SELECT c FROM ConfiguracaoSistema c ORDER BY c.chave ASC")
    List<ConfiguracaoSistema> findAllOrderByChave();

    /**
     * Buscar configurações ativas
     */
    List<ConfiguracaoSistema> findByAtivoTrueOrderByChave();

    /**
     * Buscar configurações inativas
     */
    List<ConfiguracaoSistema> findByAtivoFalseOrderByChave();

    /**
     * Buscar configurações por categoria (baseado no prefixo da chave)
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE CONCAT(:categoria, '.%') ORDER BY c.chave")
    List<ConfiguracaoSistema> findByCategoria(@Param("categoria") String categoria);

    /**
     * Buscar configurações de sistema
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'sistema.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesSistema();

    /**
     * Buscar configurações de notificação
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'notificacao.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesNotificacao();

    /**
     * Buscar configurações de segurança
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'seguranca.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesSeguranca();

    /**
     * Buscar configurações de backup
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'backup.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesBackup();

    /**
     * Buscar configurações de integração
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'integracao.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesIntegracao();

    /**
     * Buscar configurações de comparecimento
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE 'comparecimento.%' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesComparecimento();

    /**
     * Buscar configurações por valor
     */
    List<ConfiguracaoSistema> findByValor(String valor);

    /**
     * Buscar configurações com filtros
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE " +
            "(:chave IS NULL OR LOWER(c.chave) LIKE LOWER(CONCAT('%', :chave, '%'))) AND " +
            "(:valor IS NULL OR LOWER(c.valor) LIKE LOWER(CONCAT('%', :valor, '%'))) AND " +
            "(:descricao IS NULL OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))) AND " +
            "(:ativo IS NULL OR c.ativo = :ativo)")
    Page<ConfiguracaoSistema> findWithFilters(@Param("chave") String chave,
                                              @Param("valor") String valor,
                                              @Param("descricao") String descricao,
                                              @Param("ativo") Boolean ativo,
                                              Pageable pageable);

    /**
     * Contar configurações ativas
     */
    Long countByAtivoTrue();

    /**
     * Contar configurações inativas
     */
    Long countByAtivoFalse();

    /**
     * Buscar configurações modificadas recentemente
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.atualizadoEm >= :dataLimite ORDER BY c.atualizadoEm DESC")
    List<ConfiguracaoSistema> findModificadasRecentemente(@Param("dataLimite") java.time.LocalDateTime dataLimite);

    /**
     * Buscar configurações nunca modificadas
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.atualizadoEm IS NULL OR c.atualizadoEm = c.criadoEm")
    List<ConfiguracaoSistema> findNuncaModificadas();

    /**
     * Buscar configurações com valores padrão específicos
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.valor IN ('true', 'false') ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesBooleanas();

    /**
     * Buscar configurações numéricas
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE LENGTH(c.valor) > 0 AND " +
            "SUBSTRING(c.valor, 1, 1) IN ('0','1','2','3','4','5','6','7','8','9') ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesNumericas();

    /**
     * Buscar configurações de tempo/horário
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE LENGTH(c.valor) = 5 AND " +
            "SUBSTRING(c.valor, 3, 1) = ':' ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesHorario();

    /**
     * Buscar configurações críticas (que afetam funcionamento do sistema)
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave IN (" +
            "'sistema.manutencao', " +
            "'seguranca.sessao_timeout_minutos', " +
            "'backup.automatico', " +
            "'audit.log_ativo'" +
            ") ORDER BY c.chave")
    List<ConfiguracaoSistema> findConfiguracoesCriticas();

    /**
     * Buscar estatísticas de configurações por categoria
     */
    @Query("SELECT " +
            "SUBSTRING(c.chave, 1, LOCATE('.', c.chave) - 1) as categoria, " +
            "COUNT(c) as total, " +
            "SUM(CASE WHEN c.ativo = true THEN 1 ELSE 0 END) as ativas " +
            "FROM ConfiguracaoSistema c " +
            "WHERE LOCATE('.', c.chave) > 0 " +
            "GROUP BY SUBSTRING(c.chave, 1, LOCATE('.', c.chave) - 1) " +
            "ORDER BY categoria")
    List<Object[]> getEstatisticasPorCategoria();

    /**
     * Buscar configurações com descrição vazia
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.descricao IS NULL OR c.descricao = '' ORDER BY c.chave")
    List<ConfiguracaoSistema> findSemDescricao();

    /**
     * Buscar configurações por padrão de chave
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE :padrao ORDER BY c.chave")
    List<ConfiguracaoSistema> findByPadraoChave(@Param("padrao") String padrao);

    /**
     * Verificar se todas as configurações essenciais existem
     */
    @Query("SELECT COUNT(c) FROM ConfiguracaoSistema c WHERE c.chave IN (" +
            "'sistema.nome', " +
            "'sistema.versao', " +
            "'sistema.manutencao', " +
            "'notificacao.email.ativo', " +
            "'seguranca.sessao_timeout_minutos'" +
            ")")
    Long countConfiguracoesEssenciais();

    /**
     * Buscar configurações que precisam de validação
     */
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE " +
            "(c.chave LIKE '%.timeout%') OR " +
            "(c.chave LIKE '%.ativo' AND c.valor NOT IN ('true', 'false')) OR " +
            "(c.chave LIKE '%.horario' AND LENGTH(c.valor) != 5)")
    List<ConfiguracaoSistema> findConfiguracoesInvalidas();
}