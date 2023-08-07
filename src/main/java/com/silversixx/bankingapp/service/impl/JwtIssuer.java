package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.security.principal.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtIssuer {
    String issueAccessToken(UserPrincipal userPrincipal);
    String issueRefreshToken(UserPrincipal userPrincipal);
    void refresh(HttpServletRequest request, HttpServletResponse response);
}
