package br.com.zenon;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    public static List<Transaction> filterFrauds(List<Transaction> transactions) {
        return transactions.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.toList());
    }

    public static List<Transaction> highestAmountsTransactions(List<Transaction> transactions, long n) {
        if (n < 0) {
            return transactions.stream()
                    .sorted(Comparator.comparing(Transaction::amount).reversed())
                    .collect(Collectors.toList());
        }
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public static List<String> topSuspects(List<Transaction> transactions, long n) {
        return highestAmountsTransactions(filterFrauds(transactions), -1).stream()
                .map(t -> t.origin().name())
                .distinct()
                .limit(n)
                .collect(Collectors.toList());
    }

    public static BigDecimal totalAmount(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Map<TransactionType, Long> countFraudsByType(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }
}
