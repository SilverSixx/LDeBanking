package com.silversixx.bankingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AppUserModel {
    public String username;
    public String password;
    public boolean isAccountNonExpired;
    public boolean isAccountNonLocked;
    public boolean isCredentialsNonExpired;
    public boolean isEnabled;

}
