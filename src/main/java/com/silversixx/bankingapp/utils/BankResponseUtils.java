package com.silversixx.bankingapp.utils;

import com.silversixx.bankingapp.dto.AccountInfo;
import com.silversixx.bankingapp.dto.BankResponse;
import com.silversixx.bankingapp.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BankResponseUtils {
    private final UserRepository userRepository;
    private final EmailUtils emailUtils;

    public String validateEmailForNewAccount(String email) {
        if(!emailUtils.test(email))
            return AccountUtils.EMAIL_INVALID_CODE;
        if(userRepository.findUserByEmail(email).isPresent())
            return AccountUtils.ACCOUNT_EXISTS_EMAIL_CODE;
        return "ok";
    }
    public String validatePhoneForNewAccount(String phoneNumber) {
        if (!AccountUtils.testPhoneNumber(phoneNumber))
            return AccountUtils.INVALID_PHONE_NUMBER_CODE;
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent())
            return AccountUtils.ACCOUNT_EXISTS_PHONE_NUMBER_CODE;
        return "ok";
    }
    public BankResponse exceptionResponse(String exceptionCode, String exceptionMessage) {
        return actionResponse(exceptionCode, exceptionMessage, null, null, null);
    }
     //any bank response call this
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
