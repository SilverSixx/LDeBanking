package com.silversixx.bankingapp.security.authorities;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.silversixx.bankingapp.security.authorities.Permission.*;

@Getter
public enum Role {
    USER(new HashSet<>()),
    ADMIN(new HashSet<>(Arrays.asList(USER_WRITE, USER_READ, TRANSACTIONS_READ))),
    ADMINTRAINEE(new HashSet<>(Arrays.asList(USER_READ, TRANSACTIONS_READ)));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();

        for (Permission permission : getPermissions()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getPermission()));
        }

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }
}
