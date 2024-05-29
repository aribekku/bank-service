package application.services.impl;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.repositories.MonthlyLimitRepository;
import application.services.MonthlyLimitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<MonthlyLimit> getAllSetLimits() {
        return limitRepository.findAllByTransactionNull();
    }

    @Override
    public void setNewLimit(SetNewLimitDTO newLimitDTO) {
        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(
                                                                                    newLimitDTO.getExpenseCategory());

        Double newLimitAmount = newLimitDTO.getLimit();
        MonthlyLimit newLimit = new MonthlyLimit();
        newLimit.setLimitSettingDate(LocalDateTime.now());
        newLimit.setLimitAmount(newLimitAmount);

        if (previousLimit == null) {
            newLimit.setLimitBalance(newLimitAmount);
        }
        else {
            Double previousLimitAmount = previousLimit.getLimitAmount();
            Double previousLimitBalance = previousLimit.getLimitBalance();
            Double limitBalance = Math.round((newLimitAmount - (previousLimitAmount - previousLimitBalance))*100)/100.0;
            newLimit.setLimitBalance(limitBalance);
        }

        newLimit.setCurrencyShortName("USD");
        newLimit.setExpenseCategory(newLimitDTO.getExpenseCategory());

        limitRepository.save(newLimit);
    }
}
