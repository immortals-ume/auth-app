package com.immortals.authapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.immortals.authapp.model.entity.Permissions;
import com.immortals.authapp.model.entity.Roles;
import com.immortals.authapp.model.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String userName;

    @Getter
    private final String email;

    @JsonIgnore
    private final String password;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;
    // Optional attributes map for extensibility
    private final Map<String, Object> attributes = new HashMap<>();

    private Collection<String> permissions;

    public UserPrincipal(Long userId,
                         String userName,
                         String password,
                         String email,
                         Collection<? extends GrantedAuthority> authorities,
                         Collection<String> permissions,
                         boolean accountNonExpired,
                         boolean accountNonLocked,
                         boolean credentialsNonExpired,
                         boolean enabled) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.permissions = permissions;
    }

    public static UserPrincipal create(User user, Collection<Roles> roles, Collection<String> permissions) {
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(roles);

        return new UserPrincipal(
                user.getUserId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                authorities,
                permissions,
                user.getAccountNonExpired() != null ? user.getAccountNonExpired() : Boolean.TRUE,
                user.getAccountNonLocked() != null ? user.getAccountNonLocked() : Boolean.TRUE,
                user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : Boolean.TRUE,
                user.getActiveInd() != null ? user.getActiveInd() : Boolean.TRUE
        );
    }

    public static Collection<? extends GrantedAuthority> extractAuthorities(Collection<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities != null ? authorities : Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
