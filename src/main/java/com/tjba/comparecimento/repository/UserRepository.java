package com.tjba.comparecimento.repository;

import com.tjba.comparecimento.entity.User;
import com.tjba.comparecimento.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para entidade User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Buscar usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar se existe usuário com email
     */
    boolean existsByEmail(String email);

    /**
     * Verificar se existe usuário ativo com email
     */
    boolean existsByEmailAndAtivoTrue(String email);

    /**
     * Buscar usuário por ID incluindo inativos
     */
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdIncludingInactive(@Param("id") Long id);

    /**
     * Contar usuários por role e status ativo
     */
    long countByRoleAndAtivo(UserRole role, Boolean ativo);

    /**
     * Buscar usuários ativos por departamento
     */
    Page<User> findByDepartamentoAndAtivoTrue(String departamento, Pageable pageable);

    /**
     * Buscar usuários com filtros
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:departamento IS NULL OR LOWER(u.departamento) LIKE LOWER(CONCAT('%', :departamento, '%'))) AND " +
            "u.ativo = true")
    Page<User> findAllWithFilters(@Param("nome") String nome,
                                  @Param("email") String email,
                                  @Param("role") UserRole role,
                                  @Param("departamento") String departamento,
                                  Pageable pageable);

    /**
     * Buscar usuários por role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.ativo = true ORDER BY u.nome")
    Page<User> findByRoleAndAtivoTrue(@Param("role") UserRole role, Pageable pageable);

    /**
     * Buscar usuários criados em período
     */
    @Query("SELECT u FROM User u WHERE u.criadoEm BETWEEN :dataInicio AND :dataFim AND u.ativo = true")
    Page<User> findByCriadoEmBetweenAndAtivoTrue(@Param("dataInicio") java.time.LocalDateTime dataInicio,
                                                 @Param("dataFim") java.time.LocalDateTime dataFim,
                                                 Pageable pageable);

    /**
     * Buscar últimos usuários cadastrados
     */
    @Query("SELECT u.nome, u.criadoEm FROM User u WHERE u.ativo = true ORDER BY u.criadoEm DESC")
    java.util.List<Object[]> findUltimosUsuarios(Pageable pageable);

    /**
     * Contar usuários ativos
     */
    long countByAtivoTrue();

    /**
     * Buscar usuários que nunca fizeram login
     */
    @Query("SELECT u FROM User u WHERE u.ultimoLogin IS NULL AND u.ativo = true")
    Page<User> findUsersNeverLoggedIn(Pageable pageable);

    /**
     * Buscar usuários por último login anterior a data
     */
    @Query("SELECT u FROM User u WHERE u.ultimoLogin < :data AND u.ativo = true")
    Page<User> findByUltimoLoginBefore(@Param("data") java.time.LocalDateTime data, Pageable pageable);

    /**
     * Buscar estatísticas de usuários por role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.ativo = true GROUP BY u.role")
    java.util.List<Object[]> countUsersByRole();

    /**
     * Buscar estatísticas de usuários por departamento
     */
    @Query("SELECT u.departamento, COUNT(u) FROM User u WHERE u.ativo = true AND u.departamento IS NOT NULL GROUP BY u.departamento ORDER BY COUNT(u) DESC")
    java.util.List<Object[]> countUsersByDepartamento();
}