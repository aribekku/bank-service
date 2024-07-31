package application.services;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;

import java.util.List;

public interface MonthlyLimitService {

    List<MonthlyLimit> getAllLimits();
    MonthlyLimit getActiveLimit(String expenseCategory);
    void setNewLimit(SetNewLimitDTO newLimitDTO);
}
