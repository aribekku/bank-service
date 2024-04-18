package application.controllers;

import application.DTO.SetNewLimitDTO;
import application.services.MonthlyLimitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/limits")
public class MonthlyLimitController {

    private final MonthlyLimitService limitService;

    public MonthlyLimitController(MonthlyLimitService limitService) {
        this.limitService = limitService;
    }

    @PostMapping()
    public ResponseEntity<?> setNewLimit(@RequestBody SetNewLimitDTO newLimitDTO) {
        limitService.setNewLimit(newLimitDTO);
        return ResponseEntity.ok("New limit was successfully created!");
    }

}
