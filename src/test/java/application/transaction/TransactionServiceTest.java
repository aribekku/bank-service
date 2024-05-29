package application.transaction;

import application.DTO.CreateTransactionDTO;
import application.models.MonthlyLimit;
import application.models.Transaction;
import application.repositories.MonthlyLimitRepository;
import application.repositories.TransactionRepository;
import application.services.impl.CurrencyRateServiceImpl;
import application.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TransactionServiceTest {

    @Mock
    private MonthlyLimitRepository limitRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrencyRateServiceImpl currencyRateService;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void limit_exceeded_false_test() {
        CreateTransactionDTO transactionDTOMock = new CreateTransactionDTO(
                1000000123L,
                9999999999L,
                "KZT",
                100000.0,
                "product");

        Transaction transactionMock = new Transaction(
                transactionDTOMock.getAccountFrom(),
                transactionDTOMock.getAccountTo(),
                transactionDTOMock.getCurrencyShortName(),
                transactionDTOMock.getSum()
        );

        MonthlyLimit previousLimitMock = new MonthlyLimit(
                LocalDateTime.parse("2024-04-19T00:00:00"),
                1000.0,
                1000.0,
                "USD",
                "product",
                transactionMock,
                false);

        when(currencyRateService.getCurrencyRate(transactionDTOMock.getCurrencyShortName())).thenReturn(447.141886);
        when(limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTOMock.getExpenseCategory()))
                .thenReturn(previousLimitMock);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<MonthlyLimit> limitCaptor = ArgumentCaptor.forClass(MonthlyLimit.class);

        transactionService.save(transactionDTOMock);

        verify(transactionRepository, Mockito.times(1)).save(transactionCaptor.capture());
        verify(limitRepository, Mockito.times(1)).save(limitCaptor.capture());

        MonthlyLimit capturedLimit = limitCaptor.getValue();

        Assertions.assertFalse(capturedLimit.isLimitExceeded());
    }

    @Test
    void limit_exceeded_true_test() {
        CreateTransactionDTO transactionDTOMock = new CreateTransactionDTO(
                1000000123L,
                9999999999L,
                "RUB",
                100000.0,
                "product");

        Transaction transactionMock = new Transaction(
                transactionDTOMock.getAccountFrom(),
                transactionDTOMock.getAccountTo(),
                transactionDTOMock.getCurrencyShortName(),
                transactionDTOMock.getSum()
        );

        MonthlyLimit previousLimitMock = new MonthlyLimit(
                LocalDateTime.parse("2024-04-19T00:00:00"),
                1000.0,
                1000.0,
                "USD",
                "product",
                transactionMock,
                false);

        when(currencyRateService.getCurrencyRate(transactionDTOMock.getCurrencyShortName())).thenReturn(94.161959);
        when(limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTOMock.getExpenseCategory()))
                .thenReturn(previousLimitMock);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<MonthlyLimit> limitCaptor = ArgumentCaptor.forClass(MonthlyLimit.class);

        transactionService.save(transactionDTOMock);

        verify(transactionRepository, Mockito.times(1)).save(transactionCaptor.capture());
        verify(limitRepository, Mockito.times(1)).save(limitCaptor.capture());

        MonthlyLimit capturedLimit = limitCaptor.getValue();

        Assertions.assertTrue(capturedLimit.isLimitExceeded());
    }

    @Test
    void limit_exceeded_false_when_limit_is_not_set_null_test() {
        CreateTransactionDTO transactionDTOMock = new CreateTransactionDTO(
                1000000123L,
                9999999999L,
                "KZT",
                100000.0,
                "product");

        when(currencyRateService.getCurrencyRate(transactionDTOMock.getCurrencyShortName())).thenReturn(447.141886);
        when(limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTOMock.getExpenseCategory()))
                .thenReturn(null);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<MonthlyLimit> limitCaptor = ArgumentCaptor.forClass(MonthlyLimit.class);

        transactionService.save(transactionDTOMock);

        verify(transactionRepository, Mockito.times(1)).save(transactionCaptor.capture());
        verify(limitRepository, Mockito.times(1)).save(limitCaptor.capture());

        MonthlyLimit capturedLimit = limitCaptor.getValue();

        Assertions.assertFalse(capturedLimit.isLimitExceeded());
    }

    @Test
    void limit_exceeded_true_when_limit_is_not_set_null_test() {
        CreateTransactionDTO transactionDTOMock = new CreateTransactionDTO(
                1000000123L,
                9999999999L,
                "KZT",
                10000000.0,
                "product");

        when(currencyRateService.getCurrencyRate(transactionDTOMock.getCurrencyShortName())).thenReturn(447.141886);
        when(limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(transactionDTOMock.getExpenseCategory()))
                .thenReturn(null);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<MonthlyLimit> limitCaptor = ArgumentCaptor.forClass(MonthlyLimit.class);

        transactionService.save(transactionDTOMock);

        verify(transactionRepository, Mockito.times(1)).save(transactionCaptor.capture());
        verify(limitRepository, Mockito.times(1)).save(limitCaptor.capture());

        MonthlyLimit capturedLimit = limitCaptor.getValue();

        Assertions.assertTrue(capturedLimit.isLimitExceeded());
    }

    @Test
    void get_all_test() {
        Transaction transactionOne = new Transaction(
                1234567890L,
                9999999999L,
                "KZT",
                50000.0);

        Transaction transactionTwo = new Transaction(
                1234567890L,
                9999999999L,
                "KZT",
                70000.0);

        List<Transaction> mockTransactions = List.of(transactionOne, transactionTwo);

        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        List<Transaction> result = transactionService.getAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockTransactions, result);
    }
}
