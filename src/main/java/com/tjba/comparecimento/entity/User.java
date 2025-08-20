package com.tjba.comparecimento.entity;

import com.tjba.comparecimento.entity.base.BaseEntity;
import com.tjba.comparecimento.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um usuário do sistema.
 */
@Entity
@Table(name = "usuarios",
        indexes = {
                @Index(name = "idx_usuario_email", columnList = "email"),
                @Index(name = "idx_usuario_role", columnList = "role"),
                @Index(name = "idx_usuario_ativo", columnList = "ativo")
        }
)
@SQLDelete(sql = "UPDATE usuarios SET ativo = false WHERE id = ? AND version = ?")
@SQLRestriction("ativo = true")
@NamedQueries({
        @NamedQuery(
                name = "User.findByRole",
                query = "SELECT u FROM User u WHERE u.role = :role AND u.ativo = true"
        ),
        @NamedQuery(
                name = "User.countByRole",
                query = "SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.ativo = true"
        )
})
public class User extends BaseEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Role é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Size(max = 100, message = "Departamento deve ter no máximo 100 caracteres")
    @Column(name = "departamento", length = 100)
    private String departamento;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "configuracoes", columnDefinition = "jsonb")
    private String configuracoes;

    // === CONSTRUTORES ===
    public User() {
        super();
    }

    public User(String nome, String email, String password, UserRole role) {
        this();
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.role = role;
        this.ativo = Boolean.TRUE;
    }

    // === MÉTODOS UTILITÁRIOS ===
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    public boolean isUsuario() {
        return role != null && role.isUsuario();
    }

    public void registrarLogin() {
        this.ultimoLogin = LocalDateTime.now();
    }

    public String getNomeCompleto() {
        return nome + (departamento != null ? " (" + departamento + ")" : "");
    }

    // === GETTERS E SETTERS ===
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(String configuracoes) {
        this.configuracoes = configuracoes;
    }

    // === EQUALS E HASHCODE ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", ativo=" + ativo +
                '}';
    }
}