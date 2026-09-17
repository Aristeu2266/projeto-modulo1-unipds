package br.com.zenon;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TransactionIngestor {
    private final String path;
    private List<Transaction> transactions;
    private int numberOfLines;

    private final Logger logger = Logger.getLogger("TransactionIngestor");

    public TransactionIngestor(String path) {
        this.path = path;
        this.numberOfLines = 0;
        loadTransactions();
    }

    public TransactionIngestor(String path, int numberOfLines) {
        this.path = path;
        this.numberOfLines = numberOfLines;
        loadTransactions();
    }

    private void loadTransactions() {
        Path path = Path.of(this.path);
        transactions = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;

            br.readLine();

            int lineCount = 0;
            while (((line = br.readLine()) != null) && ((numberOfLines == 0) || (lineCount++ < numberOfLines))) {
                parseTransaction(line).ifPresent(transactions::add);
            }

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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }
}
