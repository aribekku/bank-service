package application.controllers;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;
import application.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionDTO transactionDTO) {
        transactionService.save(transactionDTO);
        return ResponseEntity.ok("Transaction was successfully created!");
    }

    @GetMapping
    public ResponseEntity<?> getTransactionsExceededLimit() {
        return ResponseEntity.ok(transactionService.getTransactionsExceededLimit());
    }
}
