package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.dto.*;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.service.impl.BankService;
import com.silversixx.bankingapp.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
@Component
@Slf4j
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private final UserRepository userRepo;
    private final EmailServiceImpl emailService;
    private final TransactionServiceImpl transactionService;
    private final BankResponseUtils bankResponseUtils;
    private final OtpService otpService;
    private final OtpUtils otpUtils;
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        Optional<UserModel> user = userRepo.findByAccountNumber(request.getAccountNumber());
        if(user.isPresent())
            return bankResponseUtils.actionResponse(AccountUtils.ACCOUNT_FOUND_CODE, AccountUtils.ACCOUNT_FOUND_MESSAGE, user.get().getFullName(), user.get().getAccountNumber(), user.get().getAccountBalance());
        return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }
    @Override
    public String nameEnquiry(EnquiryRequest request) {
        Optional<UserModel> user = userRepo.findByAccountNumber(request.getAccountNumber());
        return user.map(UserModel::getFullName).orElse(null);
    }
    @Override
    public Mono<Object> creditRequestForOtp(String accountNumber) {
        return Mono.fromCallable(
        () -> {
                Optional<UserModel> userOpt = userRepo.findByAccountNumber(accountNumber);
                if (userOpt.isPresent())
                    return otpService.sendOTP(accountNumber);
                else return new OtpResponse(OtpStatus.DELIVERED, "Otp sent successfully");
            }
        ).switchIfEmpty(Mono.defer(() -> Mono.just(new OtpResponse(OtpStatus.FAILED, "User not found"))));
    }
    @Override
    public Mono<Object> debitRequestForOtp(String accountNumber, BigDecimal amount) {
        return Mono.fromCallable(
        () -> {
                Optional<UserModel> userOpt = userRepo.findByAccountNumber(accountNumber);
                if (userOpt.isPresent()) {
                    UserModel user = userOpt.get();
                    if (user.getAccountBalance().compareTo(amount) < 0)
                        return new OtpResponse(OtpStatus.FAILED, "Insufficient balance");
                    else return otpService.sendOTP(accountNumber);
                } else return new OtpResponse(OtpStatus.DELIVERED, "Otp sent successfully");
            }
        ).switchIfEmpty(Mono.defer(() -> Mono.just(new OtpResponse(OtpStatus.FAILED, "User not found"))));
    }
    @Override
    public Mono<Object> transferRequestForOtp(String accountTransfer, String accountBenefit, BigDecimal amount) {
        return Mono.fromCallable(() -> {
            Optional<UserModel> userTransferOpt = userRepo.findByAccountNumber(accountTransfer);
            Optional<UserModel> userBenefitOpt = userRepo.findByAccountNumber(accountBenefit);
            if (userTransferOpt.isPresent()) {
                UserModel userTransfer = userTransferOpt.get();
                if (userTransfer.getAccountBalance().compareTo(amount) < 0)
                    return new OtpResponse(OtpStatus.FAILED, "Insufficient balance in transfer account");
            } else return new OtpResponse(OtpStatus.FAILED, "User not found for transfer account");
            if (userBenefitOpt.isPresent())
                return otpService.sendOTP(accountTransfer);
            else return new OtpResponse(OtpStatus.FAILED, "User not found for benefited account");
        }).switchIfEmpty(Mono.defer(() -> Mono.just(new OtpResponse(OtpStatus.FAILED, "User not found"))));
    }
    @Override
    public BankResponse creditRequestWithOtp(CreditDebitRequest request) {
        Optional<UserModel> userOpt = userRepo.findByAccountNumber(request.getAccountNumber());
        if (!userOpt.isPresent()) // Use isPresent() instead of isEmpty()
            return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
        UserModel user = userOpt.get();
        if (!otpUtils.verifyOTP(request.getOtp(), user.getAccountNumber()))
            return bankResponseUtils.exceptionResponse(AccountUtils.INVALID_OTP_CODE, AccountUtils.INVALID_OTP_MESSAGE);
        user.setAccountBalance(user.getAccountBalance().add(request.getAmount()));
        userRepo.save(user);
        emailService.send(
                EmailDetails.builder()
                        .recipientMail(user.getEmail())
                        .subject("ACCOUNT CREDIT")
                        .build(),
                EmailUtils.buildCreditDebitEmail(
                        user.getEmail(),
                        EmailUtils.buildCreditDebitDetails(
                                LocalDateTime.now(),
                                "CREDIT",
                                user.getAccountNumber(),
                                user.getAccountBalance(),
                                request.getAmount()
                        )
                )
        );
        transactionService.saveTransaction(
                TransactionDto.builder()
                        .transactionType("CREDIT")
                        .transactionTimestamp(LocalDateTime.now())
                        .accountNumber(request.getAccountNumber())
                        .amount(request.getAmount())
                        .status("SUCCESS")
                        .build()
        );
        return bankResponseUtils.actionResponse(AccountUtils.ACCOUNT_CREDIT_CODE, AccountUtils.ACCOUNT_CREDIT_MESSAGE, user.getFullName(), user.getAccountNumber(), user.getAccountBalance());
    }

    @Override
    public BankResponse debitRequestWithOtp(CreditDebitRequest request) {
        Optional<UserModel> userOpt = userRepo.findByAccountNumber(request.getAccountNumber());
        if (!userOpt.isPresent())
            return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
        UserModel user = userOpt.get();
        if(!otpUtils.verifyOTP(request.getOtp(), user.getAccountNumber()))
            return bankResponseUtils.exceptionResponse(AccountUtils.INVALID_OTP_CODE, AccountUtils.INVALID_OTP_MESSAGE);
        if(user.getAccountBalance().compareTo(request.getAmount()) < 0)
            return bankResponseUtils.exceptionResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE, AccountUtils.INSUFFICIENT_BALANCE_MESSAGE);
        user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));
        userRepo.save(user);
        emailService.send(
                EmailDetails.builder()
                        .recipientMail(user.getEmail())
                        .subject("ACCOUNT DEBIT")
                        .build()
                , EmailUtils.buildCreditDebitEmail(
                        user.getEmail(),
                        EmailUtils.buildCreditDebitDetails(LocalDateTime.now(), "DEBIT", user.getAccountNumber(), user.getAccountBalance(), request.getAmount())
                )
        );
        transactionService.saveTransaction(
                TransactionDto.builder()
                        .transactionType("DEBIT")
                        .transactionTimestamp(LocalDateTime.now())
                        .accountNumber(request.getAccountNumber())
                        .amount(request.getAmount())
                        .status("SUCCESS")
                        .build()
        );
        return bankResponseUtils.actionResponse(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE, AccountUtils.ACCOUNT_DEBIT_SUCCESS_MESSAGE, user.getFullName(), user.getAccountNumber(), user.getAccountBalance());
    }
    @Override
    public BankResponse transferRequestWithOtp(TransferRequest request) {
        Optional<UserModel> userSource = userRepo.findByAccountNumber(request.getAccountTransfer());
        Optional<UserModel> userDestination = userRepo.findByAccountNumber(request.getAccountBenefit());
        if (!userSource.isPresent())
            return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
        UserModel userTransfer = userSource.get();
        if (!userDestination.isPresent())
            return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
        UserModel userBenefit = userDestination.get();
        if(!otpUtils.verifyOTP(request.getOtp(), userTransfer.getAccountNumber()))
            return bankResponseUtils.exceptionResponse(AccountUtils.INVALID_OTP_CODE, AccountUtils.INVALID_OTP_MESSAGE);
        if(userTransfer.getAccountBalance().compareTo(request.getAmount()) < 0)
            return bankResponseUtils.exceptionResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE, AccountUtils.INSUFFICIENT_BALANCE_MESSAGE);
        userTransfer.setAccountBalance(userTransfer.getAccountBalance().subtract(request.getAmount()));
        userRepo.save(userTransfer);
        emailService.send(
                EmailDetails.builder()
                        .subject("TRANSACTION NOTIFICATION")
                        .recipientMail(userTransfer.getEmail())
                        .build(),
                EmailUtils.buildTransactionEmail(
                        userTransfer.getEmail(),
                        EmailUtils.buildTransactionalDetails(
                                LocalDateTime.now(),
                                "Internal transference",
                                request.getAccountTransfer(),
                                userTransfer.getAccountBalance(),
                                request.getAccountBenefit(),
                                request.getAmount(),
                                request.getMessage()
                        )
                )
        );
        userBenefit.setAccountBalance(userBenefit.getAccountBalance().add(request.getAmount()));
        userRepo.save(userBenefit);
        emailService.send(
                EmailDetails.builder()
                        .subject("TRANSACTION NOTIFICATION")
                        .recipientMail(userBenefit.getEmail())
                        .build(),
                EmailUtils.buildBenefitedTransactionsEmail(
                        userBenefit.getEmail(),
                        EmailUtils.buildBenefitedDetails(
                                LocalDateTime.now(),
                                "Internal transference",
                                request.getAccountTransfer(),
                                userBenefit.getAccountBalance(),
                                request.getAccountBenefit(),
                                request.getAmount(),
                                request.getMessage()
                        )
                )
        );
        transactionService.saveTransaction(
                TransactionDto.builder()
                        .transactionType("TRANSFER")
                        .transactionTimestamp(LocalDateTime.now())
                        .accountNumber(request.getAccountTransfer())
                        .amount(request.getAmount())
                        .status("SUCCESS")
                        .build()
        );
        return bankResponseUtils.actionResponse(
                AccountUtils.SUCCESSFUL_TRANSACTION_CODE,
                String.format(
                        AccountUtils.SUCCESSFUL_TRANSACTION_MESSAGE,
                        request.getAmount().toString(),
                        userBenefit.getAccountNumber(),
                        userBenefit.getFullName(),
                        request.getMessage()
                ),
                userTransfer.getFullName(),userTransfer.getAccountNumber(),userTransfer.getAccountBalance()
        );
    }
}
