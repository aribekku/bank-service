package application.services.impl;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.repositories.MonthlyLimitRepository;
import application.services.MonthlyLimitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MonthlyLimitServiceImpl implements MonthlyLimitService {

    private final MonthlyLimitRepository limitRepository;

    @Override
    public void setNewLimit(SetNewLimitDTO newLimitDTO) {
        MonthlyLimit previousLimit = limitRepository.findFirstByExpenseCategoryOrderByCreatedDesc(newLimitDTO.getExpenseCategory());

        MonthlyLimit newLimit = new MonthlyLimit();
        newLimit.setLimitSettingDate(LocalDateTime.now());
        newLimit.setLimitAmount(newLimitDTO.getLimit());

        if (previousLimit == null) {
            newLimit.setLimitBalance(newLimitDTO.getLimit());
        }
        else {
            Double limitBalance = newLimitDTO.getLimit() - (previousLimit.getLimitAmount() - previousLimit.getLimitBalance());
            newLimit.setLimitBalance(limitBalance);
        }

        newLimit.setCurrencyShortName("USD");
        newLimit.setExpenseCategory(newLimitDTO.getExpenseCategory());

        limitRepository.save(newLimit);
    }
}
