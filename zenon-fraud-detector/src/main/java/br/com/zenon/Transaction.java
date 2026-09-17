package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustomer origin,
                          TransactionCustomer recipient, boolean isFraud, boolean isFlaggedFraud) {
    public Transaction {
        Objects.requireNonNull(type);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(recipient);

        if (step < 1) throw new IllegalArgumentException("step should be positive: " + step);
        if (amount.signum() < 0)
            throw new IllegalArgumentException("amount should be positive: " + amount);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                ", step=" + step +
                ", type=" + type +
                ", amount=" + amount +
                ", origin=" + origin +
                ", recipient=" + recipient +
                ", isFraud=" + isFraud +
                ", isFlaggedFraud=" + isFlaggedFraud +
                '}';
    }
}
