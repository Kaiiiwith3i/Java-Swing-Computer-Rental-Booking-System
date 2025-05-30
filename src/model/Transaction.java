package model;

import java.time.LocalDate;

public class Transaction {
    public String user;
    public String computerId;
    public String timeSlot;
    public double amountPaid;
    public String timestamp;
    public LocalDate bookingDate;

    public Transaction(String user, String computerId, String timeSlot, double amountPaid, String timestamp, LocalDate bookingDate) {
        this.user = user;
        this.computerId = computerId;
        this.timeSlot = timeSlot;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
        this.bookingDate = bookingDate;
    }

    public String toReceiptString() {
         return "----- RECEIPT -----\n" +
           "Customer: " + user + "\n" +
           "Computer: " + computerId + "\n" +
           "Booked Date: " + bookingDate + "\n" +
           "Time Slot: " + timeSlot + "\n" +
           "Amount Paid: ₱" + String.format("%.2f", amountPaid) + "\n" +
           "Date Booked: " + timestamp + "\n" +
           "-------------------";
    }
}
