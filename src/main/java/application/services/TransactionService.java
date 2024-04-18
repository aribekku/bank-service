package application.services;

import application.DTO.CreateTransactionDTO;
import application.DTO.GetTransactionDTO;

import java.util.List;

public interface TransactionService {

    List<GetTransactionDTO> getTransactionsExceededLimit();

    void save(CreateTransactionDTO transactionDTO);
}
