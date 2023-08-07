package com.silversixx.bankingapp.controller;

import com.itextpdf.text.DocumentException;
import com.silversixx.bankingapp.entity.Transaction;
import com.silversixx.bankingapp.service.StatementServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statement")
@CrossOrigin("*")
@RequiredArgsConstructor
public class StatementController {
    private final StatementServiceImpl statementServiceImpl;
    @GetMapping
    @PreAuthorize("hasAuthority('transaction:read')")
    public List<Transaction> getStatementServiceImpl(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return statementServiceImpl.createStatement(accountNumber, startDate,endDate);
    }
}
