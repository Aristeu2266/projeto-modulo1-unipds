package br.com.zenon;

import java.util.List;

public class Main {
    void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor("data/PS_20174392719_1491204439457_log.csv", 50000);

        List<Transaction> frauds = FraudAnalyzer.filterFrauds(ingestor.getTransactions());
        IO.println("1. Total de Fraudes: " + frauds.size());

        IO.println("2. Top 3 Fraudes de Maior Valor:");
        FraudAnalyzer.highestAmountsTransactions(frauds, 3).forEach(t -> IO.println(t.amount().setScale(2).toPlainString()));

        IO.println("3. Clientes Suspeitos:");
        FraudAnalyzer.topSuspects(frauds, 5).forEach(IO::println);

        IO.println("4. Prejuízo Total: " + FraudAnalyzer.totalAmount(frauds).toPlainString());

        IO.println("5. Fraudes por Tipo:");
        FraudAnalyzer.countFraudsByType(frauds).forEach((k, v) -> IO.println(" - " + k + ": " + v));

    }
}