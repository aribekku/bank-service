package application.controllers;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;
import application.services.TransactionService;
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
    public void createTransaction(@RequestBody CreateTransactionDTO transactionDTO) {
        transactionService.save(transactionDTO);
    }

    @GetMapping
    public List<GetTransactionDTO> getTransactionsExceededLimit() {
        return transactionService.getTransactionsExceededLimit();
    }
}
