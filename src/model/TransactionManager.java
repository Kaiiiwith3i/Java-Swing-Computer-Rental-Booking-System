package model;

import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public List<Transaction> getTransactionsByUser(String username) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.user.equals(username)) {
                result.add(t);
            }
        }
        return result;
    }
}
