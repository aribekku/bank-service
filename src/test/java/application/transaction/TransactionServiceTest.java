package application.transaction;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;
import application.models.MonthlyLimit;
import application.models.Transaction;
import application.repositories.MonthlyLimitRepository;
import application.repositories.TransactionRepository;
import application.services.impl.CurrencyRateServiceImpl;
import application.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private CreateTransactionDTO createTransactionDTO;
    private Transaction transactionOne;
    private Transaction transactionTwo;
    private MonthlyLimit limit;

    @BeforeEach
    public void setUp() {
        createTransactionDTO = new CreateTransactionDTO();
        createTransactionDTO.setAccountFrom(1000000009L);
        createTransactionDTO.setAccountTo(9999999999L);
        createTransactionDTO.setCurrencyShortName("KZT");
        createTransactionDTO.setSum(new BigDecimal(700000));
        createTransactionDTO.setExpenseCategory("product");

        limit = new MonthlyLimit();
        limit.setLimitSettingDate(LocalDateTime.now().minusDays(30));
        limit.setLimitAmount(new BigDecimal(1000));
        limit.setLimitBalance(new BigDecimal(1000));
        limit.setCurrencyShortName("USD");
        limit.setExpenseCategory("product");
        limit.setActive(true);

        transactionOne = new Transaction();
        transactionOne.setAccountFrom(1000000009L);
        transactionOne.setAccountTo(9999999999L);
        transactionOne.setCurrencyShortName("KZT");
        transactionOne.setSum(new BigDecimal(50000));
        transactionOne.setExpenseCategory("product");
        transactionOne.setLimit(limit);
        transactionOne.setLimitExceeded(false);

        transactionTwo = new Transaction();
        transactionTwo.setAccountFrom(1000000009L);
        transactionTwo.setAccountTo(7777777777L);
        transactionTwo.setCurrencyShortName("KZT");
        transactionTwo.setSum(new BigDecimal(100000));
        transactionTwo.setExpenseCategory("service");
        transactionTwo.setLimit(limit);
        transactionTwo.setLimitExceeded(false);
    }

    @Test
    public void test_Get_All() {
        List<Transaction> transactions = Arrays.asList(transactionOne, transactionTwo);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAll();

        Assertions.assertEquals(transactions, result);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void test_Get_Transactions_ExceededLimit() {
        Transaction limitExceededTransaction = new Transaction();
        limitExceededTransaction.setCreated(LocalDateTime.now());
        limitExceededTransaction.setAccountFrom(1000000009L);
        limitExceededTransaction.setAccountTo(9999999999L);
        limitExceededTransaction.setSum(new BigDecimal(1500));
        limitExceededTransaction.setCurrencyShortName("USD");
        limitExceededTransaction.setExpenseCategory("product");
        limitExceededTransaction.setLimit(limit);
        limitExceededTransaction.setLimitExceeded(true);

        List<Transaction> transactions = List.of(limitExceededTransaction);
        when(transactionRepository.findAllByLimitExceeded(true)).thenReturn(transactions);

        List<GetTransactionDTO> result = transactionService.getTransactionsExceededLimit();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(new BigDecimal(1500), result.get(0).getSum());
        verify(transactionRepository, times(1)).findAllByLimitExceeded(true);
    }

    @Test
    public void test_Create_Transaction_ExceedsLimit() {
        when(currencyRateService.getCurrencyRate("KZT")).thenReturn(new BigDecimal(475));
        when(limitRepository.findByExpenseCategoryAndActiveTrue("product")).thenReturn(Optional.of(limit));

        transactionService.create(createTransactionDTO);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        Assertions.assertTrue(savedTransaction.isLimitExceeded());
    }

    @Test
    public void test_Create_Transaction_DoesNot_ExceedLimit() {
        when(currencyRateService.getCurrencyRate("KZT")).thenReturn(new BigDecimal(475));
        when(limitRepository.findByExpenseCategoryAndActiveTrue("product")).thenReturn(Optional.of(limit));

        limit.setLimitBalance(new BigDecimal(1500));

        transactionService.create(createTransactionDTO);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        Assertions.assertFalse(savedTransaction.isLimitExceeded());
    }

    @Test
    public void testCreate_withInvalidCurrencyRate() {
        when(currencyRateService.getCurrencyRate("KZT")).thenReturn(BigDecimal.ZERO);

        IllegalArgumentException thrown = Assertions
                .assertThrows(IllegalArgumentException.class, () -> transactionService.create(createTransactionDTO));

        Assertions.assertEquals("Invalid currency rate", thrown.getMessage());
        verify(transactionRepository, times(0)).save(any(Transaction.class));
        verify(limitRepository, times(0)).save(any(MonthlyLimit.class));
    }
}
