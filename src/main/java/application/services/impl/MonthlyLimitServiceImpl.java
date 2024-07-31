package application.services.impl;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;
import application.repositories.MonthlyLimitRepository;
import application.services.MonthlyLimitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        if (newLimitDTO.getLimit() == null || newLimitDTO.getLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Limit amount must be positive.");
        }

        deactivateCurrentLimit(newLimitDTO.getExpenseCategory());

        MonthlyLimit newLimit = createNewLimit(newLimitDTO);

        limitRepository.save(newLimit);
    }

    private MonthlyLimit createNewLimit(SetNewLimitDTO newLimitDTO) {
        MonthlyLimit newLimit = new MonthlyLimit();
        newLimit.setLimitSettingDate(LocalDateTime.now());
        newLimit.setLimitAmount(newLimitDTO.getLimit());
        newLimit.setLimitBalance(newLimitDTO.getLimit());
        newLimit.setCurrencyShortName("USD");
        newLimit.setExpenseCategory(newLimitDTO.getExpenseCategory());
        newLimit.setActive(true);

        return newLimit;
    }

    private void deactivateCurrentLimit(String expenseCategory) {
        Optional<MonthlyLimit> activeLimitOpt = limitRepository.findByExpenseCategoryAndActive(expenseCategory, true);

        activeLimitOpt.ifPresent(activeLimit -> {
            activeLimit.setActive(false);
            limitRepository.save(activeLimit);
        });
    }
}
