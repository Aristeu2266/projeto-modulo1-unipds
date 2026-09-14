package br.com.zenon.models;

import java.math.BigDecimal;

public record TransactionCustomer(String name, double oldBalance, double newBalance) {

    @Override
    public String toString() {
        return "TransactionCustomer{" + "\n" +
                "name=" + name + "\n" +
                "oldBalance=" + oldBalance + "\n" +
                "newBalance=" + newBalance + "\n" +
                '}';
    }
}
