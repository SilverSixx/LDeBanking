package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.entity.ConfirmToken;

import java.util.Optional;

public interface ConfirmTokenService {
    void saveToken(ConfirmToken token);
    Optional<ConfirmToken> getToken(String token);
    void setConfirmedAt(String token);
}
