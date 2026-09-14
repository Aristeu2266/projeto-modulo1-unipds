package br.com.zenon.fraud;

import br.com.zenon.enums.TransactionType;
import br.com.zenon.models.TransactionCustomer;

import java.math.BigDecimal;

public record Transaction(int step, TransactionType transactionType, BigDecimal amount, TransactionCustomer orig,
                          TransactionCustomer dest, boolean isFraud, boolean isFlaggedFraud) {
    @Override
    public String toString() {
        return "Transaction{" + "\n" +
                "step=" + step + "\n" +
                "transactionType=" + transactionType + "\n" +
                "amount=" + amount + "\n" +
                "orig=" + orig + "\n" +
                "dest=" + dest + "\n" +
                "isFraud=" + isFraud + "\n" +
                "isFlaggedFraud=" + isFlaggedFraud + "\n" +
                '}';
    }
}
