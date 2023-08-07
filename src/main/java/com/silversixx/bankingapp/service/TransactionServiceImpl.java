package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.dto.TransactionDto;
import com.silversixx.bankingapp.entity.Transaction;
import com.silversixx.bankingapp.dao.TransactionRepo;
import com.silversixx.bankingapp.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepo transactionRepo;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();

        transactionRepo.save(transaction);
        log.info("Transaction saved.");
    }
}
