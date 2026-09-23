package br.com.zenon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TransactionSQLRepository implements TransactionRepository {

    public static final int JDB_BATCH_SIZE = 1000;
    Logger logger = Logger.getLogger("TransactionSQLRepository");

    @Override
    public Optional<Transaction> getTransactionByOriginName(String originName) {
        String sql = """
                SELECT id, step, `type`, amount, name_origin, old_balance_origin, new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud
                FROM transactions
                where name_origin = ?
                ORDER BY step ASC
                LIMIT 1
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, originName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transação de origem: " + originName, e);
        }
    }

    @Override
    public void save(Transaction transaction) {
        String sql = """
                insert into zenon_frauds.transactions
                (step, `type`, amount, name_origin, old_balance_origin, new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                values (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?);
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareTransaction(ps, transaction);
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar nova transação: " + transaction, e);
        }
    }

    public void saveAll(List<Transaction> transactions) {
        String sql = """
                insert into zenon_frauds.transactions
                (step, `type`, amount, name_origin, old_balance_origin, new_balance_origin, name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                values (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?);
                """;

        try (Connection conn = ConnectionFactory.getConnection()) {
            logger.info("Criando conexão");

            conn.setAutoCommit(false);

            int count = 0;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Transaction transaction : transactions) {
                    prepareTransaction(ps, transaction);

                    ps.addBatch();
                    count++;

                    if (count % JDB_BATCH_SIZE == 0) {

                        ps.executeBatch();
                        conn.commit();
                    }
                }

                ps.executeBatch();
                conn.commit();

                conn.setAutoCommit(true);

            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao tentar rollback", ex);
                }
                throw new RuntimeException("Erro ao salvar transações", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar conexão", e);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) {
        try {
            long step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");

            String nameOrigin = rs.getString("name_origin");
            BigDecimal oldBalanceOrigin = rs.getBigDecimal("old_balance_origin");
            BigDecimal newBalanceOrigin = rs.getBigDecimal("new_balance_origin");
            TransactionCustomer origin = new TransactionCustomer(nameOrigin, oldBalanceOrigin, newBalanceOrigin);

            String nameRecipient = rs.getString("name_recipient");
            BigDecimal oldBalanceRecipient = rs.getBigDecimal("old_balance_recipient");
            BigDecimal newBalanceRecipient = rs.getBigDecimal("new_balance_recipient");
            TransactionCustomer recipient = new TransactionCustomer(nameRecipient, oldBalanceRecipient, newBalanceRecipient);

            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTransaction(PreparedStatement ps, Transaction transaction) throws SQLException {
        ps.setLong(1, transaction.step());
        ps.setString(2, transaction.type().name());
        ps.setBigDecimal(3, transaction.amount());

        ps.setString(4, transaction.origin().name());
        ps.setBigDecimal(5, transaction.origin().oldBalance());
        ps.setBigDecimal(6, transaction.origin().newBalance());

        ps.setString(7, transaction.recipient().name());
        ps.setBigDecimal(8, transaction.recipient().oldBalance());
        ps.setBigDecimal(9, transaction.recipient().newBalance());

        ps.setBoolean(10, transaction.isFraud());
        ps.setBoolean(11, transaction.isFlaggedFraud());
    }
}
