package com.silversixx.bankingapp.utils;

import com.silversixx.bankingapp.dto.AccountInfo;
import com.silversixx.bankingapp.dto.BankResponse;
import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.dto.RegisterRequest;
import com.silversixx.bankingapp.entity.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BankResponseUtils {
    private final UserRepository userRepository;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;
    public BankResponse handleAccountValidationResponse(String isValidCredentials) {
        Map<String, String> codeToMessageMap = new HashMap<>();
        codeToMessageMap.put(AccountUtils.EMAIL_INVALID_CODE, AccountUtils.EMAIL_INVALID_MESSAGE);
        codeToMessageMap.put(AccountUtils.INVALID_PHONE_NUMBER_CODE, AccountUtils.INVALID_PHONE_NUMBER_MESSAGE);
        codeToMessageMap.put(AccountUtils.ACCOUNT_EXISTS_CODE, AccountUtils.ACCOUNT_EXISTS_MESSAGE);
        codeToMessageMap.put(AccountUtils.ACCOUNT_REGISTERED_NOT_ENABLED_CODE, AccountUtils.ACCOUNT_REGISTERED_NOT_ENABLED_MESSAGE);
        codeToMessageMap.put(AccountUtils.ACCOUNT_EXISTS_EMAIL_CODE, AccountUtils.ACCOUNT_EXISTS_EMAIL_MESSAGE);
        String errorMessage = codeToMessageMap.getOrDefault(isValidCredentials, "Unknown error");
        return exceptionResponse(isValidCredentials, errorMessage);
    }
    public BankResponse handleTokenValidationResponse(String isValidConfirmToken) {
        Map<String, String> codeToMessageMap = new HashMap<>();
        codeToMessageMap.put(TokenUtils.TOKEN_NOT_FOUND_CODE, TokenUtils.TOKEN_NOT_FOUND_MESSAGE);
        codeToMessageMap.put(TokenUtils.TOKEN_ALREADY_CONFIRMED_CODE, TokenUtils.TOKEN_ALREADY_CONFIRMED_MESSAGE);
        codeToMessageMap.put(TokenUtils.TOKEN_EXPIRED_CODE, TokenUtils.TOKEN_EXPIRED_MESSAGE);
        String errorMessage = codeToMessageMap.getOrDefault(isValidConfirmToken, "Unknown error");
        return exceptionResponse(isValidConfirmToken, errorMessage);
    }
    public String validateCredentialsForNewAccount(RegisterRequest requestCredentials) {
        if(!emailUtils.test(requestCredentials.getEmail()))
            return AccountUtils.EMAIL_INVALID_CODE;
        Optional<UserModel> existingUser = userRepository.findUserByEmail(requestCredentials.getEmail());
        if (existingUser.isPresent()) {
            UserModel user = existingUser.get();
            if (!AccountUtils.testPhoneNumber(requestCredentials.getPhoneNumber())) {
                return AccountUtils.INVALID_PHONE_NUMBER_CODE;
            }
            if (credentialsMatch(requestCredentials, user)) {
                if (user.isEnabled())
                    return AccountUtils.ACCOUNT_EXISTS_CODE;
                else
                    return AccountUtils.ACCOUNT_REGISTERED_NOT_ENABLED_CODE;
            }
            return AccountUtils.ACCOUNT_EXISTS_EMAIL_CODE;
        }
        return "ok";
    }
    private boolean credentialsMatch(RegisterRequest requestCredentials, UserModel user) {
        return passwordEncoder.matches(requestCredentials.getPassword(), user.getPassword()) &&
                requestCredentials.getFullName().equals(user.getFullName()) &&
                requestCredentials.getDob().equals(user.getDob()) &&
                requestCredentials.getGender().equals(user.getGender()) &&
                requestCredentials.getAddress().equals(user.getAddress());
    }
    public BankResponse exceptionResponse(String exceptionCode, String exceptionMessage) {
        return actionResponse(exceptionCode, exceptionMessage, null, null, null);
    }
    public BankResponse actionResponse(String codeResponse, String messageResponse, String fullName, String accountNumber, BigDecimal amount) {
        return BankResponse.builder()
                .responseCode(codeResponse)
                .responseMessage(messageResponse)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(fullName)
                                .accountNumber(accountNumber)
                                .accountBalance(amount)
                                .build()
                )
                .build();
    }
}
