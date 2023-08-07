package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.dto.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BankService {
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    Mono<Object> creditRequestForOtp(String accountNumber);
    Mono<Object> debitRequestForOtp(String accountNumber, BigDecimal amount);
    Mono<Object> transferRequestForOtp(String accountTransfer, String accountBenefit, BigDecimal amount);
    BankResponse creditRequestWithOtp(CreditDebitRequest creditRequest);
    BankResponse debitRequestWithOtp(CreditDebitRequest debitRequest);
    BankResponse transferRequestWithOtp(TransferRequest transferRequest);
}
