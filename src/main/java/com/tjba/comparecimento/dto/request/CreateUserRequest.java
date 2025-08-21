package com.tjba.comparecimento.dto.request;

import com.tjba.comparecimento.entity.enums.UserRole;
import jakarta.validation.constraints.*;

/**
 * DTO para criação de usuário.
 */
public class CreateUserRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s'.-]+$", message = "Nome deve conter apenas letras, espaços e caracteres válidos")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "Senha deve conter pelo menos uma letra e um número")
    private String password;

    @NotNull(message = "Role é obrigatório")
    private UserRole role;

    @Size(max = 100, message = "Departamento deve ter no máximo 100 caracteres")
    private String departamento;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$|^$", message = "Telefone deve ter formato válido")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    // Constructors
    public CreateUserRequest() {}

    public CreateUserRequest(String nome, String email, String password, UserRole role) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome != null ? nome.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase().trim() : null;
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
        this.departamento = departamento != null ? departamento.trim() : null;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone != null ? telefone.trim() : null;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", departamento='" + departamento + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}