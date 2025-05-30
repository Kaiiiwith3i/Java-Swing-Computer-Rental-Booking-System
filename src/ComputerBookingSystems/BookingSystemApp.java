package ComputerBookingSystems;

import javax.swing.SwingUtilities;

import manager.SlotManager;
import model.TransactionManager;
import ui.LoginScreen;

public class BookingSystemApp {
    public static void main(String[] args) {
        SlotManager slotManager = new SlotManager();
        TransactionManager transactionManager = new TransactionManager();

        SwingUtilities.invokeLater(() -> {
            new LoginScreen(slotManager, transactionManager).display();
        });
    }
}
