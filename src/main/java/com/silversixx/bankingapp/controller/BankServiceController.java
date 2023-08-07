package com.silversixx.bankingapp.controller;

import com.silversixx.bankingapp.dto.BankResponse;
import com.silversixx.bankingapp.dto.CreditDebitRequest;
import com.silversixx.bankingapp.dto.EnquiryRequest;
import com.silversixx.bankingapp.dto.TransferRequest;
import com.silversixx.bankingapp.service.BankServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/service")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BankServiceController {
    private final BankServiceImpl bankService;
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return bankService.balanceEnquiry(request);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return bankService.nameEnquiry(request);
    }
    @PostMapping("/credit/forOtp")
    public Mono<Object> creditRequestForOtp(@RequestBody CreditDebitRequest creditRequest){
        return bankService.creditRequestForOtp(creditRequest.getAccountNumber());
    }
    @PostMapping("/credit/withOtp")
    public BankResponse creditRequestWithOtp(@RequestBody CreditDebitRequest creditRequest){
        return bankService.creditRequestWithOtp(creditRequest);
    }
    @PostMapping("/debit/forOtp")
    public Mono<Object> debitRequestForOtp(@RequestBody CreditDebitRequest debitRequest){
        return bankService.debitRequestForOtp(debitRequest.getAccountNumber(), debitRequest.getAmount());
    }
    @PostMapping("/debit/withOtp")
    public BankResponse debitRequestWithOtp(@RequestBody CreditDebitRequest debitRequest){
        return bankService.debitRequestWithOtp(debitRequest);
    }
    @PostMapping("/transfer/forOtp")
    public Mono<Object> transferRequestForOtp(@RequestBody TransferRequest transferRequest){
        return bankService.transferRequestForOtp(transferRequest.getAccountTransfer(), transferRequest.getAccountBenefit(), transferRequest.getAmount());
    }
    @PostMapping("/transfer/withOtp")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest){
        return bankService.transferRequestWithOtp(transferRequest);
    }
}
