package com.silversixx.bankingapp.service.impl;

import com.itextpdf.text.DocumentException;
import com.silversixx.bankingapp.entity.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

public interface BankStatementService {
    List<Transaction> createStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException;
}
