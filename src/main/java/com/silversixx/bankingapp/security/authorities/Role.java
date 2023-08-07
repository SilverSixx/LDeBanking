package com.silversixx.bankingapp.security.authorities;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.silversixx.bankingapp.security.authorities.Permission.*;

@Getter
public enum Role {
    USER(Set.of()),
    ADMIN(Set.of(USER_WRITE, USER_READ, TRANSACTIONS_READ)),
    ADMINTRAINEE(Set.of(USER_READ, TRANSACTIONS_READ));
    private final Set<Permission> permissions;
    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }
    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}