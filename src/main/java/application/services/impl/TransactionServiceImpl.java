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
    public List<GetTransactionDTO> getTransactionsExceededLimit() {
        List<MonthlyLimit> limitsExceeded = limitRepository.findAllByLimitExceeded(true);
        List<GetTransactionDTO> transactions = new ArrayList<>();

        for (MonthlyLimit limit : limitsExceeded) {
            GetTransactionDTO transactionDTO = GetTransactionDTO
                    .builder()
                    .accountFrom(limit.getTransaction().getAccountFrom())
                    .accountTo(limit.getTransaction().getAccountTo())
                    .currencyShortName(limit.getTransaction().getCurrencyShortName())
                    .sum(limit.getTransaction().getSum())
                    .expenseCategory(limit.getExpenseCategory())
                    .transactionDateTime(limit.getTransaction().getCreated())
                    .limitSum(limit.getLimitAmount())
                    .limitDateTime(limit.getLimitSettingDate())
                    .limitCurrencyShortName(limit.getCurrencyShortName())
                    .build();

            transactions.add(transactionDTO);

        }
        return transactions;
    }

    @Override
    public void save(CreateTransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();

        transaction.setAccountFrom(transactionDTO.getAccountFrom());
        transaction.setAccountTo(transactionDTO.getAccountTo());
        transaction.setCurrencyShortName(transactionDTO.getCurrencyShortName());
        transaction.setSum(transactionDTO.getSum());

        transactionRepository.save(transaction);

        Double rate = currencyRateServiceImpl.getCurrencyRate(transactionDTO.getCurrencyShortName());

        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTO.getExpenseCategory());

        MonthlyLimit limit = new MonthlyLimit();

        if (previousLimit == null) {
            limit.setLimitAmount(1000.0);
            limit.setCurrencyShortName("USD");
            limit.setLimitBalance(Math.round((limit.getLimitAmount() - (transaction.getSum()) / rate)*100)/100.0);
            limit.setLimitSettingDate(LocalDateTime.now());
        }
        else {
            limit.setLimitAmount(previousLimit.getLimitAmount());
            limit.setLimitBalance(Math.round((previousLimit.getLimitBalance() - (transaction.getSum())/rate)*100)/100.0);
            limit.setCurrencyShortName(previousLimit.getCurrencyShortName());
            limit.setLimitSettingDate(previousLimit.getLimitSettingDate());
        }

        limit.setExpenseCategory(transactionDTO.getExpenseCategory());
        limit.setTransaction(transaction);

        limit.setLimitExceeded(limit.getLimitBalance() < 0);

        limitRepository.save(limit);
    }
}
