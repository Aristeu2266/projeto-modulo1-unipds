package br.com.zenon;

import br.com.zenon.TransactionReport.Statistics;

public class Main {
    void main(String[] args) {
        var transactionReport = new TransactionReport();

        Statistics statistics = transactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");

        IO.print("""
                Total de linhas: %d
                Total de fraudes: %d
                valor total transacionado: %.2f""".formatted(statistics.totalTransactions(), statistics.totalFrauds(), statistics.totalAmount()));
    }
}