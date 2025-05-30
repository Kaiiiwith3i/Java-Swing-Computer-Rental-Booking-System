package utils;

import model.Transaction;
import java.io.FileWriter;
import java.io.IOException;

public class ReceiptGenerator {
    public static void generateReceipt(Transaction transaction) {
        try {
            FileWriter writer = new FileWriter("receipt_" + System.currentTimeMillis() + ".txt");
            writer.write(transaction.toReceiptString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
