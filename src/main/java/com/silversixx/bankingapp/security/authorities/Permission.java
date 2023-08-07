package com.silversixx.bankingapp.security.authorities;

import lombok.Getter;

@Getter
public enum Permission {
    USER_READ("user:read"),
    TRANSACTIONS_READ("transactions:read"),
    USER_WRITE("student:write");
    private final String permission;
    Permission(String permission){
        this.permission = permission;
    }
}
