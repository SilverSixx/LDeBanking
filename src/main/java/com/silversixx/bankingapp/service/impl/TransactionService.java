package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
