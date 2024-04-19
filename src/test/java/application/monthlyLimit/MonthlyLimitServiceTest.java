package application.monthlyLimit;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.models.Transaction;
import application.repositories.MonthlyLimitRepository;
import application.services.impl.MonthlyLimitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class MonthlyLimitServiceTest {

    @Mock
    private MonthlyLimitRepository limitRepository;

    @InjectMocks
    private MonthlyLimitServiceImpl limitService;

    private MonthlyLimit previousLimit;

    @BeforeEach
    void setup() {
        Transaction transaction = new Transaction(
                                        1000000123L,
                                          9999999999L,
                                    "KZT",
                                              50000.0);

        previousLimit = new MonthlyLimit(LocalDateTime.parse(
                                                            "2024-04-19T00:00:00"),
                                                            1000.0,
                                                            1000.0,
                                                            "USD",
                                                            "product",
                                                            transaction,
                                                            false);
    }

    @Test
    void set_new_limit_test() {
        SetNewLimitDTO newLimitDTO = new SetNewLimitDTO(1200.0, "product");
        Mockito.when(limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(newLimitDTO.getExpenseCategory())).thenReturn(previousLimit);

        ArgumentCaptor<MonthlyLimit> limitCaptor = ArgumentCaptor.forClass(MonthlyLimit.class);

        limitService.setNewLimit(newLimitDTO);

        verify(limitRepository, Mockito.times(1)).save(limitCaptor.capture());

        MonthlyLimit capturedLimit = limitCaptor.getValue();

        Assertions.assertEquals(1200.0, capturedLimit.getLimitAmount());
        Assertions.assertEquals("product", capturedLimit.getExpenseCategory());
    }

    @Test
    void get_all_test() {
        List<MonthlyLimit> mockLimits = getMockLimits();

        when(limitRepository.findAll()).thenReturn(mockLimits);

        List<MonthlyLimit> result = limitService.getAllLimits();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockLimits, result);
    }

    private List<MonthlyLimit> getMockLimits() {
        Transaction transactionOne = new Transaction(
                1234567890L,
                9999999999L,
                "KZT",
                50000.0);

        MonthlyLimit limitOne = new MonthlyLimit(LocalDateTime.parse(
                "2024-04-19T00:00:00"),
                1000.0,
                888.18,
                "USD",
                "product",
                transactionOne,
                false);

        Transaction transactionTwo = new Transaction(
                1234567890L,
                9999999999L,
                "KZT",
                70000.0);

        MonthlyLimit limitTwo = new MonthlyLimit(LocalDateTime.parse(
                "2024-04-20T00:00:00"),
                1000.0,
                731.63,
                "USD",
                "product",
                transactionTwo,
                false);

        return List.of(limitOne, limitTwo);
    }

}