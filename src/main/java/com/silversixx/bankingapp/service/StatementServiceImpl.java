package com.silversixx.bankingapp.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.entity.Transaction;
import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.dao.TransactionRepo;
import com.silversixx.bankingapp.service.impl.BankStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatementServiceImpl implements BankStatementService {
    private final TransactionRepo transactionRepo;
    private final UserRepository userRepo;
    private static final String FILE_TEMPLATE = "C:\\Users\\ADMIN\\IdeaProjects\\LDeBankingApp\\src\\main\\resources\\template\\statement-template.pdf";
    public List<Transaction> createStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactions = transactionRepo.findAll()
                .stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> {
                    LocalDate transactionDate = transaction.getCreatedAt().toLocalDate();
                    return !transactionDate.isBefore(start) && !transactionDate.isAfter(end);
                })
                .collect(Collectors.toList());
        UserModel user = userRepo.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("user not exist."));
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        OutputStream outputStream = new FileOutputStream(FILE_TEMPLATE);
        PdfWriter.getInstance(document, outputStream);
        log.info("Writing to statement template.");
        document.open();
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Font subHeadingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // Bank Info Table
        PdfPTable bankInfoTable = new PdfPTable(1);
        bankInfoTable.setWidthPercentage(100);

        Paragraph bankNameParagraph = new Paragraph("LDBank", headingFont);
        PdfPCell bankNameCell = new PdfPCell(bankNameParagraph);
        bankNameCell.setBorder(0);
        bankNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bankNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankInfoTable.addCell(bankNameCell);

        Paragraph bankAddressParagraph = new Paragraph("1st Ngo Thi Nham, Ha Dong, Ha Noi", subHeadingFont);
        PdfPCell bankAddressCell = new PdfPCell(bankAddressParagraph);
        bankAddressCell.setBorder(0);
        bankAddressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bankAddressCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankInfoTable.addCell(bankAddressCell);

        // statement
        PdfPTable statementInfoTable = new PdfPTable(1);
        statementInfoTable.setWidthPercentage(100);

        PdfPCell statementCell = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT", subHeadingFont));
        statementCell.setBorder(0);
        statementCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        statementCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        statementCell.setColspan(2);
        statementInfoTable.addCell(statementCell);
        PdfPCell beginDateCell = new PdfPCell(new Phrase("Start date: " + startDate, normalFont));
        beginDateCell.setBorder(0);
        statementInfoTable.addCell(beginDateCell);

        PdfPCell stopDateCell = new PdfPCell(new Phrase("End date: " + endDate, normalFont));
        stopDateCell.setBorder(0);
        statementInfoTable.addCell(stopDateCell);

        // Transaction Table
        PdfPTable transactionTable = new PdfPTable(4);
        transactionTable.setWidthPercentage(100);

        PdfPCell dateHeaderCell = new PdfPCell(new Phrase("DATE/TIME", subHeadingFont));
        dateHeaderCell.setBackgroundColor(BaseColor.CYAN);
        dateHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionTable.addCell(dateHeaderCell);

        PdfPCell transactionTypeHeaderCell = new PdfPCell(new Phrase("TYPE", subHeadingFont));
        transactionTypeHeaderCell.setBackgroundColor(BaseColor.CYAN);
        transactionTypeHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionTable.addCell(transactionTypeHeaderCell);

        PdfPCell transactionAmountHeaderCell = new PdfPCell(new Phrase("TRANSACTION AMOUNT", subHeadingFont));
        transactionAmountHeaderCell.setBackgroundColor(BaseColor.CYAN);
        transactionAmountHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionTable.addCell(transactionAmountHeaderCell);

        PdfPCell transactionStatusHeaderCell = new PdfPCell(new Phrase("TRANSACTION STATUS", subHeadingFont));
        transactionStatusHeaderCell.setBackgroundColor(BaseColor.CYAN);
        transactionStatusHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionTable.addCell(transactionStatusHeaderCell);

// Add transaction data
        transactions.forEach(transaction -> {
            transactionTable.addCell(new Phrase(extractDateTime(transaction.getCreatedAt().toString()), normalFont));
            transactionTable.addCell(new Phrase(transaction.getTransactionType(), normalFont));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString(), normalFont));
            transactionTable.addCell(new Phrase(transaction.getStatus(), normalFont));
        });

// Add tables to the document
        document.add(bankInfoTable);
        document.add(customerInfoTable(user));
        document.add(statementInfoTable);
        document.add(transactionTable);

// Close the document
        document.close();
        log.info("writing successfully to "+FILE_TEMPLATE);
        return transactions;
    }
    private PdfPTable customerInfoTable(UserModel user) {
        // Define subHeadingFont and normalFont if not defined already
        Font subHeadingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        // Customer Info Table

        PdfPTable customerInfoTable = new PdfPTable(1);
        customerInfoTable.setWidthPercentage(100);
        PdfPCell headingCell = new PdfPCell(new Phrase("Customer Info:", subHeadingFont));
        headingCell.setBorder(0);
        customerInfoTable.addCell(headingCell);
        PdfPCell customerNameCell = new PdfPCell(new Phrase("Name: " + user.getFullName(), normalFont));
        customerNameCell.setBorder(0);
        customerInfoTable.addCell(customerNameCell);
        PdfPCell emailCell = new PdfPCell(new Phrase("Email: " + user.getEmail(), normalFont));
        emailCell.setBorder(0);
        customerInfoTable.addCell(emailCell);
        PdfPCell dobCell = new PdfPCell(new Phrase("Date of Birth: " + user.getDob(), normalFont));
        dobCell.setBorder(0);
        customerInfoTable.addCell(dobCell);
        PdfPCell genderCell = new PdfPCell(new Phrase("Gender: " + user.getGender(), normalFont));
        genderCell.setBorder(0);
        customerInfoTable.addCell(genderCell);
        PdfPCell addressCell = new PdfPCell(new Phrase("Address: " + user.getAddress(), normalFont));
        addressCell.setBorder(0);
        customerInfoTable.addCell(addressCell);
        PdfPCell phoneNumberCell = new PdfPCell(new Phrase("Phone Number: " + user.getPhoneNumber(), normalFont));
        phoneNumberCell.setBorder(0);
        customerInfoTable.addCell(phoneNumberCell);
        PdfPCell accountNumberCell = new PdfPCell(new Phrase("Account Number: " + user.getAccountNumber(), normalFont));
        accountNumberCell.setBorder(0);
        customerInfoTable.addCell(accountNumberCell);
        PdfPCell accountBalanceCell = new PdfPCell(new Phrase("Account Balance: $" + user.getAccountBalance(), normalFont));
        accountBalanceCell.setBorder(0);
        customerInfoTable.addCell(accountBalanceCell);
        PdfPCell createdAtCell = new PdfPCell(new Phrase("Created At: " + extractDateTime(user.getCreateAt().toString()), normalFont));
        createdAtCell.setBorder(0);
        customerInfoTable.addCell(createdAtCell);
        return customerInfoTable;
    }
    private String extractDateTime(String timestamp) {
        LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
