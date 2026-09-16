package br.com.zenon;

import java.math.BigDecimal;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
    public TransactionCustomer(String name, double oldBalance, double newBalance) {
        this(name, BigDecimal.valueOf(oldBalance), BigDecimal.valueOf(newBalance));
    }

    @Override
    public String toString() {
        return "TransactionCustomer{" + 
                ", name=" + name +
                ", oldBalance=" + oldBalance +
                ", newBalance=" + newBalance +
                '}';
    }
}
