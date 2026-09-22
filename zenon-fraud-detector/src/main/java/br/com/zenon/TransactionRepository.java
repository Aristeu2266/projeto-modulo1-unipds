package br.com.zenon;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<Transaction> getTransactionByOriginName(String origin);

    void save(Transaction transaction);
}
