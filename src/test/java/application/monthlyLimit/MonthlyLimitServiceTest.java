package application.monthlyLimit;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.repositories.MonthlyLimitRepository;
import application.services.impl.MonthlyLimitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class MonthlyLimitServiceTest {

    @Mock
    private MonthlyLimitRepository limitRepository;
    @InjectMocks
    private MonthlyLimitServiceImpl limitService;
    private SetNewLimitDTO validNewLimitDTO;
    private MonthlyLimit currentLimit;
    private MonthlyLimit limitOne;
    private MonthlyLimit limitTwo;

    @BeforeEach
    void setup() {
        validNewLimitDTO = new SetNewLimitDTO();
        validNewLimitDTO.setLimit(new BigDecimal("2000"));
        validNewLimitDTO.setExpenseCategory("product");

        currentLimit = new MonthlyLimit();
        currentLimit.setLimitSettingDate(LocalDateTime.now().minusDays(30));
        currentLimit.setLimitAmount(new BigDecimal("1000"));
        currentLimit.setLimitBalance(new BigDecimal("500"));
        currentLimit.setExpenseCategory("product");
        currentLimit.setActive(true);

        limitOne = new MonthlyLimit();
        limitOne.setLimitSettingDate(LocalDateTime.now().minusDays(10));
        limitOne.setLimitAmount(new BigDecimal("1000"));
        limitOne.setLimitBalance(new BigDecimal("1000"));
        limitOne.setCurrencyShortName("USD");
        limitOne.setExpenseCategory("product");
        limitOne.setActive(true);

        limitTwo = new MonthlyLimit();
        limitTwo.setLimitSettingDate(LocalDateTime.now().minusDays(20));
        limitTwo.setLimitAmount(new BigDecimal("2000"));
        limitTwo.setLimitBalance(new BigDecimal("2000"));
        limitTwo.setCurrencyShortName("USD");
        limitTwo.setExpenseCategory("service");
        limitTwo.setActive(false);
    }

    @Test
    void test_setNewLimit_withValidDTO() {
        Mockito.when(limitRepository.findByExpenseCategoryAndActiveTrue("product")).thenReturn(Optional.of(currentLimit));

        limitService.setNewLimit(validNewLimitDTO);

        verify(limitRepository, times(1)).save(currentLimit);
        Assertions.assertFalse(currentLimit.isActive());

        verify(limitRepository, times(2)).save(any(MonthlyLimit.class));
    }

    @Test
    public void testSetNewLimit_withInvalidDTO() {
        SetNewLimitDTO invalidDTO = new SetNewLimitDTO();
        invalidDTO.setLimit(BigDecimal.ZERO);

        IllegalArgumentException exception = Assertions
                .assertThrows(IllegalArgumentException.class, () -> limitService.setNewLimit(invalidDTO));

        Assertions.assertEquals("Limit amount must be positive.", exception.getMessage());
        verify(limitRepository, times(0)).save(any(MonthlyLimit.class));
    }

    @Test
    public void testGetAllLimits() {
        List<MonthlyLimit> expectedLimits = Arrays.asList(limitOne, limitTwo);
        when(limitRepository.findAll()).thenReturn(expectedLimits);

        List<MonthlyLimit> actualLimits = limitService.getAllLimits();

        Assertions.assertEquals(expectedLimits, actualLimits);
        verify(limitRepository, times(1)).findAll();
    }

}