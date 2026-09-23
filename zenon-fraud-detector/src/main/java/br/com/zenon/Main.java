package br.com.zenon;

public class Main {
    void main() {
        var ingestor = new BetterTransactionIngestor("data/PS_20174392719_1491204439457_log.csv");
        var repository = new TransactionSQLRepository();

        long start = System.nanoTime();
        ingestor.readAsBatch(repository::saveAll);
        long end = System.nanoTime();
        IO.println("Time: " + (end - start) / 1000000 + "ms");

        repository.getTransactionByOriginName("C1231006815")
                .ifPresentOrElse(IO::println, () -> System.out.println("Not found"));
    }
}