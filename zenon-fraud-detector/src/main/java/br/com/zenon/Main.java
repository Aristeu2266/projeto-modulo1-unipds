package br.com.zenon;

import java.math.BigDecimal;

public class Main {
    void main(String[] args) {
//        TransactionCustomer cOrig1 = new TransactionCustomer("C1231006815", 170136.0, 160296.36);
//        TransactionCustomer cDest1 = new TransactionCustomer("M1979787155", 0.0, 0.0);
//
//        Transaction t1 = new Transaction(1, TransactionType.PAYMENT, BigDecimal.valueOf(9839.64), cOrig1, cDest1, false, false);
//
//        TransactionCustomer cOrig2 = new TransactionCustomer("C1280323807", 850002.52, 0.0);
//        TransactionCustomer cDest2 = new TransactionCustomer("C873221189", 6510099.11, 7360101.63);
//
//        Transaction t2 = new Transaction(743, TransactionType.CASH_OUT, BigDecimal.valueOf(850002.52), cOrig2, cDest2, true, false);

        TransactionIngestor ingestor = new TransactionIngestor("data/PS_20174392719_1491204439457_log.csv", 1000);

        for (int i = 0; i < 10; i ++) {
            System.out.println(ingestor.getTransactions().get(i));
        }
    }
}