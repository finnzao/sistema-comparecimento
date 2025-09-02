package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.EnderecoVinculado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para entidade EnderecoVinculado.
 */
@Repository
public interface EnderecoVinculadoRepository extends JpaRepository<EnderecoVinculado, Long> {

    /**
     * Buscar endereço por pessoa
     */
    Optional<EnderecoVinculado> findByPessoaMonitoradaId(Long pessoaId);

    /**
     * Buscar endereços por CEP
     */
    List<EnderecoVinculado> findByCep(String cep);

    /**
     * Buscar endereços por cidade (case insensitive)
     */
    List<EnderecoVinculado> findByCidadeIgnoreCase(String cidade);

    /**
     * Buscar endereços por estado
     */
    List<EnderecoVinculado> findByEstado(String estado);

    /**
     * Buscar endereços por bairro
     */
    List<EnderecoVinculado> findByBairroIgnoreCase(String bairro);

    /**
     * Buscar endereços por logradouro (partial match)
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :logradouro, '%'))")
    List<EnderecoVinculado> findByLogradouroContainingIgnoreCase(@Param("logradouro") String logradouro);

    /**
     * Buscar endereços por cidade e estado
     */
    List<EnderecoVinculado> findByCidadeIgnoreCaseAndEstado(String cidade, String estado);

    /**
     * Buscar endereços por bairro e cidade
     */
    List<EnderecoVinculado> findByBairroIgnoreCaseAndCidadeIgnoreCase(String bairro, String cidade);

    /**
     * Contar endereços por estado
     */
    @Query("SELECT e.estado, COUNT(e) FROM EnderecoVinculado e GROUP BY e.estado ORDER BY COUNT(e) DESC")
    List<Object[]> countByEstado();

    /**
     * Contar endereços por cidade em um estado
     */
    @Query("SELECT e.cidade, COUNT(e) FROM EnderecoVinculado e WHERE e.estado = :estado GROUP BY e.cidade ORDER BY COUNT(e) DESC")
    List<Object[]> countByCidadeAndEstado(@Param("estado") String estado);

    /**
     * Contar endereços por bairro em uma cidade
     */
    @Query("SELECT e.bairro, COUNT(e) FROM EnderecoVinculado e WHERE e.cidade = :cidade AND e.estado = :estado GROUP BY e.bairro ORDER BY COUNT(e) DESC")
    List<Object[]> countByBairroInCidade(@Param("cidade") String cidade, @Param("estado") String estado);

    /**
     * Buscar endereços com filtros
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE " +
            "(:cep IS NULL OR e.cep = :cep) AND " +
            "(:cidade IS NULL OR LOWER(e.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND " +
            "(:estado IS NULL OR e.estado = :estado) AND " +
            "(:bairro IS NULL OR LOWER(e.bairro) LIKE LOWER(CONCAT('%', :bairro, '%'))) AND " +
            "(:logradouro IS NULL OR LOWER(e.logradouro) LIKE LOWER(CONCAT('%', :logradouro, '%')))")
    Page<EnderecoVinculado> findWithFilters(@Param("cep") String cep,
                                            @Param("cidade") String cidade,
                                            @Param("estado") String estado,
                                            @Param("bairro") String bairro,
                                            @Param("logradouro") String logradouro,
                                            Pageable pageable);

    /**
     * Buscar endereços por range de CEP
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE e.cep BETWEEN :cepInicio AND :cepFim ORDER BY e.cep")
    List<EnderecoVinculado> findByCepRange(@Param("cepInicio") String cepInicio, @Param("cepFim") String cepFim);

    /**
     * Buscar CEPs distintos
     */
    @Query("SELECT DISTINCT e.cep FROM EnderecoVinculado e ORDER BY e.cep")
    List<String> findDistinctCeps();

    /**
     * Buscar cidades distintas por estado
     */
    @Query("SELECT DISTINCT e.cidade FROM EnderecoVinculado e WHERE e.estado = :estado ORDER BY e.cidade")
    List<String> findDistinctCidadesByEstado(@Param("estado") String estado);

    /**
     * Buscar bairros distintos por cidade
     */
    @Query("SELECT DISTINCT e.bairro FROM EnderecoVinculado e WHERE e.cidade = :cidade AND e.estado = :estado ORDER BY e.bairro")
    List<String> findDistinctBairrosByCidade(@Param("cidade") String cidade, @Param("estado") String estado);

    /**
     * Buscar endereços duplicados (mesmo CEP, logradouro e número)
     */
    @Query("SELECT e1 FROM EnderecoVinculado e1, EnderecoVinculado e2 WHERE " +
            "e1.id < e2.id AND " +
            "e1.cep = e2.cep AND " +
            "e1.logradouro = e2.logradouro AND " +
            "(e1.numero = e2.numero OR (e1.numero IS NULL AND e2.numero IS NULL))")
    List<EnderecoVinculado> findEnderecosDuplicados();

    /**
     * Buscar endereços sem número
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE e.numero IS NULL OR e.numero = ''")
    List<EnderecoVinculado> findSemNumero();

    /**
     * Buscar endereços sem complemento
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE e.complemento IS NULL OR e.complemento = ''")
    List<EnderecoVinculado> findSemComplemento();

    /**
     * Buscar endereços por padrão de CEP (região)
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE e.cep LIKE :padrao")
    List<EnderecoVinculado> findByPadraoCep(@Param("padrao") String padrao);

    /**
     * Validar formato de CEP usando validação em Java ao invés de REGEXP
     * Removida a query REGEXP que causava erro e substituída por validação programática
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EnderecoVinculado e WHERE e.cep = :cep")
    boolean existsByCep(@Param("cep") String cep);

    /**
     * Método auxiliar para validação de formato de CEP
     * Este método deve ser usado junto com validação programática
     */
    default boolean isCepValidFormat(String cep) {
        if (cep == null) {
            return false;
        }
        // Validação do formato CEP brasileiro: XXXXX-XXX
        return cep.matches("^[0-9]{5}-[0-9]{3}$");
    }

    /**
     * Buscar endereços mais utilizados (por logradouro)
     */
    @Query("SELECT e.logradouro, e.bairro, e.cidade, e.estado, COUNT(e) " +
            "FROM EnderecoVinculado e " +
            "GROUP BY e.logradouro, e.bairro, e.cidade, e.estado " +
            "ORDER BY COUNT(e) DESC")
    List<Object[]> findLogradourosMaisUtilizados(Pageable pageable);

    /**
     * Buscar endereços por região metropolitana (baseado no CEP)
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE " +
            "(:salvador = true AND e.cep BETWEEN '40000-000' AND '42999-999') OR " +
            "(:feiraSantana = true AND e.cep BETWEEN '44000-000' AND '44999-999') OR " +
            "(:vitoriaDaConquista = true AND e.cep BETWEEN '45000-000' AND '45999-999') OR " +
            "(:ilheus = true AND e.cep BETWEEN '45650-000' AND '45999-999')")
    Page<EnderecoVinculado> findByRegiaoMetropolitana(@Param("salvador") boolean salvador,
                                                      @Param("feiraSantana") boolean feiraSantana,
                                                      @Param("vitoriaDaConquista") boolean vitoriaDaConquista,
                                                      @Param("ilheus") boolean ilheus,
                                                      Pageable pageable);

    /**
     * Buscar estatísticas de distribuição geográfica
     */
    @Query("SELECT " +
            "e.estado, " +
            "e.cidade, " +
            "COUNT(e) as total, " +
            "COUNT(DISTINCT e.bairro) as totalBairros, " +
            "COUNT(DISTINCT e.cep) as totalCeps " +
            "FROM EnderecoVinculado e " +
            "GROUP BY e.estado, e.cidade " +
            "ORDER BY COUNT(e) DESC")
    List<Object[]> getEstatisticasDistribuicaoGeografica();

    /**
     * Buscar endereços que precisam de validação (possíveis inconsistências)
     */
    @Query("SELECT e FROM EnderecoVinculado e WHERE " +
            "e.cep IS NULL OR " +
            "e.logradouro IS NULL OR " +
            "e.bairro IS NULL OR " +
            "e.cidade IS NULL OR " +
            "e.estado IS NULL OR " +
            "LENGTH(e.estado) != 2")
    List<EnderecoVinculado> findEnderecosIncompletos();
}