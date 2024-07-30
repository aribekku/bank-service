package application.services;

import application.DTO.SetNewLimitDTO;
import application.models.MonthlyLimit;

import java.util.List;

public interface MonthlyLimitService {

    List<MonthlyLimit> getAllLimits();
    void setNewLimit(SetNewLimitDTO newLimitDTO);

}
