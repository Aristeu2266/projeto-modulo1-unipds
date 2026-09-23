package br.com.zenon;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BetterTransactionIngestor {

    private final String path;
    private final Logger logger = Logger.getLogger("TransactionIngestor");

    public BetterTransactionIngestor(String path) {
        this.path = path;
    }

    public void readAsBatch(Consumer<List<Transaction>> consumer) {
        Path path = Path.of(this.path);

        try (ExecutorService executor = Executors.newFixedThreadPool(10);
             BufferedReader br = Files.newBufferedReader(path)) {
            var iterator = br.lines().iterator();

            if (iterator.hasNext()) iterator.next();

            List<String> lineBatch = new ArrayList<>();
            while (iterator.hasNext()) {
                String line = iterator.next();
                lineBatch.add(line);

                if (lineBatch.size() >= 5000) {
                    final List<String> currentLineBatch = List.copyOf(lineBatch);
                    executor.submit(() -> executeBatch(currentLineBatch, consumer));
                    lineBatch.clear();
                }
            }

            if (!lineBatch.isEmpty()) {
                final List<String> currentLineBatch = List.copyOf(lineBatch);
                executor.submit(() -> executeBatch(currentLineBatch, consumer));
            }

        } catch (Exception e) {
            throw new RuntimeException("File could not be read:\n" + e);
        }
    }

    private void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> consumer) {
        List<Transaction> transactionBatch = lineBatch.stream()
                .map(this::parseTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();
        consumer.accept(transactionBatch);
    }

    public void readAsStream(Consumer<Transaction> consumer) {
        Path path = Path.of(this.path);

        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.lines().
                    skip(1)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);

        } catch (Exception e) {
            throw new RuntimeException("File could not be read:\n" + e);
        }
    }

    private Optional<Transaction> parseTransaction(String line) {
        try {
            String[] fields = line.split(",");

            int step = Integer.parseInt(fields[0]);
            TransactionType type = TransactionType.valueOf(fields[1]);
            BigDecimal amount = new BigDecimal(fields[2]);
            String nameOrig = fields[3];
            BigDecimal oldBalanceOrigin = new BigDecimal(fields[4]);
            BigDecimal newBalanceOrigin = new BigDecimal(fields[5]);
            String nameDest = fields[6];
            BigDecimal oldBalanceDest = new BigDecimal(fields[7]);
            BigDecimal newBalanceDest = new BigDecimal(fields[8]);
            boolean isFraud = "1".equals(fields[9]);
            boolean isFlaggedFraud = "1".equals(fields[10]);

            var customer = new TransactionCustomer(nameOrig, oldBalanceOrigin, newBalanceOrigin);
            var recipient = new TransactionCustomer(nameDest, oldBalanceDest, newBalanceDest);

            return Optional.of(new Transaction(step, type, amount, customer, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            this.logger.severe(() -> line + " | " + e);
            return Optional.empty();
        }
    }
}
