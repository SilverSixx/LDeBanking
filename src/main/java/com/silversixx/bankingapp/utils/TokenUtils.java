package com.silversixx.bankingapp.utils;

import com.silversixx.bankingapp.entity.ConfirmToken;
import com.silversixx.bankingapp.service.ConfirmTokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    public static final String TOKEN_NOT_FOUND_CODE = "001";
    public static final String TOKEN_NOT_FOUND_MESSAGE = "Token is not found.";
    public static final String TOKEN_ALREADY_CONFIRMED_CODE = "010";
    public static final String TOKEN_ALREADY_CONFIRMED_MESSAGE = "Token is already confirmed.";
    public static final String TOKEN_EXPIRED_CODE = "100";
    public static final String TOKEN_EXPIRED_MESSAGE = "Token is expired.";
    public final ConfirmTokenServiceImpl tokenService;
    public static String validateTokenForConfirmation(ConfirmToken token){
        if(token == null) return TOKEN_NOT_FOUND_CODE;
        if(token.getConfirmedAt() != null)   return TOKEN_ALREADY_CONFIRMED_CODE;
        if(token.getCreatedAt().isAfter(LocalDateTime.now().plusMinutes(5))) return TOKEN_EXPIRED_CODE;
        return "ok";
    }
}
