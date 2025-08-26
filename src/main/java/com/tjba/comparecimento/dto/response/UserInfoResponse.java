package com.tjba.comparecimento.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para resposta de informações do usuário autenticado.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {

    private Long id;
    private String nome;
    private String email;
    private String role;
    private String departamento;
    private String telefone;
    private String avatar;
    private Boolean ativo;

    // Constructors
    public UserInfoResponse() {}

    public UserInfoResponse(Long id, String nome, String email, String role, String departamento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.departamento = departamento;
    }

    public UserInfoResponse(Long id, String nome, String email, String role, String departamento, 
                           String telefone, String avatar, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.departamento = departamento;
        this.telefone = telefone;
        this.avatar = avatar;
        this.ativo = ativo;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", departamento='" + departamento + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}