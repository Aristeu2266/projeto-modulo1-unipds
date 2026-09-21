package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud) {}

    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount) {

        private final static Statistics EMPTY = new Statistics(0, 0, BigDecimal.ZERO);

        private Statistics addReportTransaction(ReportTransaction t) {
            return new Statistics(this.totalTransactions + 1,
                    this.totalFrauds + (t.isFraud ? 1 : 0),
                    this.totalAmount.add(t.amount));
        }

        private Statistics add(Statistics other) {
            return new Statistics(this.totalTransactions + other.totalTransactions,
                    this.totalFrauds + other.totalFrauds,
                    this.totalAmount.add(other.totalAmount));
        }

    }

    public Statistics generateReport(String filename) {
        Path path = Path.of(filename);

        try (Stream<String> lines = Files.lines(path)){

            return lines.skip(1)
                    .map(this::parseReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(
                            Statistics.EMPTY,
                            Statistics::addReportTransaction,
                            Statistics::add
                    )
                    ;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + filename, e);
        }
    }

    private Optional<ReportTransaction> parseReportTransaction(String line) {
        try {
            String[] fields = line.split(",");

            BigDecimal amount = new BigDecimal(fields[2]);
            boolean isFraud = "1".equals(fields[9]);

            return Optional.of(new ReportTransaction(amount, isFraud));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
