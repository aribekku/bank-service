package application.services;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;

import java.util.List;

public interface MonthlyLimitService {

    List<MonthlyLimit> getAllLimits();
    List<MonthlyLimit> getAllSetLimits();
    void setNewLimit(SetNewLimitDTO newLimitDTO);

}
