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

        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTO.getExpenseCategory());

        MonthlyLimit limit = new MonthlyLimit();

        if (previousLimit == null) {
            limit.setLimitAmount(1000D);
            limit.setCurrencyShortName("USD");
            limit.setLimitBalance((limit.getLimitAmount() - transaction.getSum()) * 100 / 100);
            limit.setLimitSettingDate(LocalDateTime.now());
        }
        else {
            limit.setLimitAmount(previousLimit.getLimitAmount());
            limit.setLimitBalance((previousLimit.getLimitBalance() - transaction.getSum()) * 100 / 100);
            limit.setCurrencyShortName(transactionDTO.getCurrencyShortName());
            limit.setLimitSettingDate(previousLimit.getLimitSettingDate());
        }

        limit.setExpenseCategory(transactionDTO.getExpenseCategory());
        limit.setTransaction(transaction);

        if (limit.getLimitBalance() < 0) {
            limit.setLimitExceeded(true);
        }

        limitRepository.save(limit);
    }
}
