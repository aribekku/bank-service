package application.services;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;
import application.models.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAll();
    List<GetTransactionDTO> getTransactionsExceededLimit();
    void create(CreateTransactionDTO transactionDTO);
}
