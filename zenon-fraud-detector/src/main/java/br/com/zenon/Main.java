package br.com.zenon;

public class Main {
    void main(String[] args) {
        TransactionIngestor ingestor = new TransactionIngestor("data/PS_20174392719_1491204439457_log.csv", 100000);

        TransactionRepository transactionRepository = new TransactionListRepository(ingestor.getTransactions());

        transactionRepository.getTransactionByOriginName("C12345")
                .ifPresentOrElse(IO::println, () -> IO.println("Customer not found"));
        transactionRepository.getTransactionByOriginName("C1231006815")
                .ifPresentOrElse(IO::println, () -> IO.println("Customer not found"));

        var before = System.nanoTime();

        transactionRepository.getTransactionByOriginName("C1868032458")
                .ifPresentOrElse(IO::println, () -> IO.println("Customer not found"));

        var after = System.nanoTime();
        IO.println("Time: " + (after - before) / 1000000.0 + "ms");

        transactionRepository = new TransactionMapRepository(ingestor.getTransactions());

        before = System.nanoTime();

        transactionRepository.getTransactionByOriginName("C1868032458")
                .ifPresentOrElse(IO::println, () -> IO.println("Customer not found"));

        after = System.nanoTime();
        IO.println("Time: " + (after - before) / 1000000.0 + "ms");
    }
}