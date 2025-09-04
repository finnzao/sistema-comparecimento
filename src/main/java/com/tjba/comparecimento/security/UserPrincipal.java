package com.tjba.comparecimento.security;

import com.tjba.comparecimento.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementação de UserDetails para o Spring Security.
 */
public class UserPrincipal implements UserDetails {

    private Long id;
    private String nome;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean ativo;

    public UserPrincipal(Long id, String nome, String email, String password,
                         Collection<? extends GrantedAuthority> authorities, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.ativo = ativo;
    }

    public static UserPrincipal create(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new UserPrincipal(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority),
                user.getAtivo()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ativo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}