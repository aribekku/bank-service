package application.controllers;

import application.DTO.SetNewLimitDTO;
import application.services.MonthlyLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "Limits API")
@RequestMapping("/limits")
public class MonthlyLimitController {

    private final MonthlyLimitService limitService;

    public MonthlyLimitController(MonthlyLimitService limitService) {
        this.limitService = limitService;
    }

    @PostMapping()
    @Operation(summary = "Endpoint for setting new limit")
    public ResponseEntity<?> setNewLimit(@RequestBody SetNewLimitDTO newLimitDTO) {
        limitService.setNewLimit(newLimitDTO);
        return ResponseEntity.ok("New limit was successfully created!");
    }

    @GetMapping
    @Operation(summary = "Endpoint for getting all limits from database")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(limitService.getAllLimits());
    }

    @GetMapping("/set")
    @Operation(summary = "Endpoint for getting only new limits that were set manually")
    public ResponseEntity<?> getAllSetLimits() {
        return ResponseEntity.ok(limitService.getAllSetLimits());
    }

}
