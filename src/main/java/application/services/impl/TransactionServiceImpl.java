package application.services.impl;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;
import application.models.MonthlyLimit;
import application.models.Transaction;
import application.repositories.MonthlyLimitRepository;
import application.repositories.TransactionRepository;
import application.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MonthlyLimitRepository limitRepository;
    private final CurrencyRateServiceImpl currencyRateServiceImpl;

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<GetTransactionDTO> getTransactionsExceededLimit() {

        List<GetTransactionDTO> transactions = new ArrayList<>();

        List<Transaction> limitExceededTransactions = transactionRepository.findAllByLimitExceeded(true);

        for (Transaction transaction : limitExceededTransactions) {
            GetTransactionDTO transactionDTO = GetTransactionDTO
                    .builder()
                    .accountFrom(transaction.getAccountFrom())
                    .accountTo(transaction.getAccountTo())
                    .currencyShortName(transaction.getCurrencyShortName())
                    .sum(transaction.getSum())
                    .expenseCategory(transaction.getExpenseCategory())
                    .transactionDateTime(transaction.getCreated())
                    .limitSum(transaction.getLimit().getLimitAmount())
                    .limitDateTime(transaction.getLimit().getLimitSettingDate())
                    .limitCurrencyShortName(transaction.getLimit().getCurrencyShortName())
                    .build();

            transactions.add(transactionDTO);
        }

        return transactions;
    }

    @Override
    public void create(CreateTransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom(transactionDTO.getAccountFrom());
        transaction.setAccountTo(transactionDTO.getAccountTo());
        transaction.setCurrencyShortName(transactionDTO.getCurrencyShortName());
        transaction.setSum(transactionDTO.getSum());
        transaction.setExpenseCategory(transactionDTO.getExpenseCategory());

        double rate = currencyRateServiceImpl.getCurrencyRate(transactionDTO.getCurrencyShortName());

        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByLimitSettingDateDesc(
                                                                                transactionDTO.getExpenseCategory());
        MonthlyLimit limit = new MonthlyLimit();

        if (previousLimit == null) {
            limit.setLimitAmount(new BigDecimal(1000));
            limit.setCurrencyShortName("USD");
            BigDecimal convertedSum = transaction.getSum().divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP);
            BigDecimal roundedLimitBalance = ((limit.getLimitAmount().subtract(convertedSum)));
            limit.setLimitBalance(roundedLimitBalance);
            limit.setLimitSettingDate(LocalDateTime.now());
        }
        else {
            limit.setLimitAmount(previousLimit.getLimitAmount());
            BigDecimal convertedSum = transaction.getSum().divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP);
            BigDecimal roundedLimitBalance = ((previousLimit.getLimitBalance().subtract(convertedSum)));
            limit.setLimitBalance(roundedLimitBalance);
            limit.setCurrencyShortName(previousLimit.getCurrencyShortName());
            limit.setLimitSettingDate(previousLimit.getLimitSettingDate());
        }

        limit.setExpenseCategory(transactionDTO.getExpenseCategory());

        limitRepository.save(limit);

        transaction.setLimit(limit);
        transaction.setLimitExceeded(limit.getLimitBalance().compareTo(BigDecimal.ZERO) < 0);
        transactionRepository.save(transaction);
    }
}
