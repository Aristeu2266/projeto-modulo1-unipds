package br.com.zenon;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransactionIngestor {
    private final String path;
    private List<Transaction> transactions;
    private int numberOfLines;

    public TransactionIngestor(String path) {
        this.path = path;
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

        try(BufferedReader br = Files.newBufferedReader(path)) {
            String line;

            br.readLine();

            if (numberOfLines > 0) {
                for (int i = 0; i < numberOfLines; i++) {
                    line = br.readLine();
                    String[] fields = line.split(",");

                    transactions.add(parseTransaction(fields));
                }
            } else {
                while((line = br.readLine()) != null){
                    String[] fields = line.split(",");

                    transactions.add(parseTransaction(fields));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Transaction parseTransaction(String[] fields) {
        int step = Integer.parseInt(fields[0]);
        TransactionType type = TransactionType.valueOf(fields[1]);
        BigDecimal amount = new BigDecimal(fields[2]);
        String nameOrig = fields[3];
        BigDecimal oldBalanceOrg = new BigDecimal(fields[4]);
        BigDecimal newBalanceOrig = new BigDecimal(fields[5]);
        String nameDest = fields[6];
        BigDecimal oldBalanceDest = new BigDecimal(fields[7]);
        BigDecimal newBalanceDest = new BigDecimal(fields[8]);
        boolean isFraud = "1".equals(fields[9]);
        boolean isFlaggedFraud = "1".equals(fields[10]);

        var customer = new TransactionCustomer(nameOrig, oldBalanceOrg, newBalanceOrig);
        var recipient  = new TransactionCustomer(nameDest, oldBalanceDest, newBalanceDest);

        return new Transaction(step, type, amount, customer, recipient, isFraud, isFlaggedFraud);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
