package application.services.impl;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.repositories.MonthlyLimitRepository;
import application.services.MonthlyLimitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MonthlyLimitServiceImpl implements MonthlyLimitService {

    private final MonthlyLimitRepository limitRepository;

    @Override
    public List<MonthlyLimit> getAllLimits() {
        return limitRepository.findAll();
    }

    @Override
    public void setNewLimit(SetNewLimitDTO newLimitDTO) {
        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByLimitSettingDateDesc(
                                                                                    newLimitDTO.getExpenseCategory());

        BigDecimal newLimitAmount = newLimitDTO.getLimit();
        MonthlyLimit newLimit = new MonthlyLimit();
        newLimit.setLimitSettingDate(LocalDateTime.now());
        newLimit.setLimitAmount(newLimitAmount);

        if (previousLimit == null) {
            newLimit.setLimitBalance(newLimitAmount);
        }
        else {
            BigDecimal previousLimitAmount = previousLimit.getLimitAmount();
            BigDecimal previousLimitBalance = previousLimit.getLimitBalance();
            BigDecimal roundedLimitBalance = ((newLimitAmount.subtract(previousLimitAmount.subtract(previousLimitBalance)).setScale(2, RoundingMode.HALF_UP)));
            newLimit.setLimitBalance(roundedLimitBalance);
        }

        newLimit.setCurrencyShortName("USD");
        newLimit.setExpenseCategory(newLimitDTO.getExpenseCategory());

        limitRepository.save(newLimit);
    }
}
