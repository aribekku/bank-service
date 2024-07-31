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
import java.util.Optional;

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
            GetTransactionDTO transactionDTO = mapToDTO(transaction);
            transactions.add(transactionDTO);
        }

        return transactions;
    }

    private GetTransactionDTO mapToDTO(Transaction transaction) {
        return GetTransactionDTO
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
    }

    @Override
    public void create(CreateTransactionDTO transactionDTO) {
        Transaction transaction = mapToTransaction(transactionDTO);

        BigDecimal rate = getCurrencyRate(transactionDTO.getCurrencyShortName());

        Optional<MonthlyLimit> previousLimit = limitRepository.findByExpenseCategoryAndActiveTrue(transactionDTO.getExpenseCategory());
        MonthlyLimit limit = createLimit(transactionDTO, previousLimit, rate);

        transaction.setLimit(limit);
        transaction.setLimitExceeded(limit.getLimitBalance().compareTo(BigDecimal.ZERO) < 0);

        limitRepository.save(limit);
        transactionRepository.save(transaction);
    }

    private Transaction mapToTransaction(CreateTransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom(transactionDTO.getAccountFrom());
        transaction.setAccountTo(transactionDTO.getAccountTo());
        transaction.setCurrencyShortName(transactionDTO.getCurrencyShortName());
        transaction.setSum(transactionDTO.getSum());
        transaction.setExpenseCategory(transactionDTO.getExpenseCategory());
        return transaction;
    }

    private MonthlyLimit createLimit(CreateTransactionDTO transactionDTO, Optional<MonthlyLimit> previousLimitOpt, BigDecimal rate) {
        MonthlyLimit limit = new MonthlyLimit();

        BigDecimal convertedSum = transactionDTO.getSum()
                .divide(rate, 2, RoundingMode.HALF_UP);

        if (previousLimitOpt.isEmpty()) {
            limit.setLimitAmount(new BigDecimal(1000));
            limit.setCurrencyShortName("USD");
            limit.setLimitBalance((limit.getLimitAmount().subtract(convertedSum)));
            limit.setLimitSettingDate(LocalDateTime.now());
        }
        else {
            MonthlyLimit previousLimit = previousLimitOpt.get();
            limit.setLimitAmount(previousLimit.getLimitAmount());
            limit.setLimitBalance((previousLimit.getLimitBalance().subtract(convertedSum)));
            limit.setCurrencyShortName(previousLimit.getCurrencyShortName());
            limit.setLimitSettingDate(previousLimit.getLimitSettingDate());
            previousLimit.setActive(false);
        }

        limit.setActive(true);
        limit.setExpenseCategory(transactionDTO.getExpenseCategory());

        return limit;
    }

    private BigDecimal getCurrencyRate(String currencyShortName) {
        BigDecimal rate = currencyRateServiceImpl.getCurrencyRate(currencyShortName);
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid currency rate");
        }
        return rate;
    }
}
