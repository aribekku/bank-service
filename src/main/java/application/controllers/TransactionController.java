package application.controllers;

import application.DTO.CreateTransactionDTO;
import application.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "Transactions API")
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(summary = "Endpoint for saving transaction to database")
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionDTO transactionDTO) {
        transactionService.create(transactionDTO);
        return ResponseEntity.ok("Transaction was successfully created!");
    }

    @GetMapping
    @Operation(summary = "Endpoint for getting all transactions")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/limit-exceeded")
    @Operation(summary = "Endpoint for getting transactions that exceeded limit")
    public ResponseEntity<?> getTransactionsExceededLimit() {
        return ResponseEntity.ok(transactionService.getTransactionsExceededLimit());
    }
}
