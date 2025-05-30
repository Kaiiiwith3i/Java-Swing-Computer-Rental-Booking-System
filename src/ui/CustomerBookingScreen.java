package ui;

import manager.SlotManager;
import model.TransactionManager;
import model.Transaction;
import model.User;
import utils.ReceiptGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerBookingScreen {
    private final User user;
    private final SlotManager slotManager;
    private final TransactionManager transactionManager;

    public CustomerBookingScreen(User user, SlotManager slotManager, TransactionManager transactionManager) {
        this.user = user;
        this.slotManager = slotManager;
        this.transactionManager = transactionManager;
    }

    public void display() {
        JFrame frame = new JFrame("Customer Booking - " + user.username);
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Background panel without image to keep clarity
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(new Color(250, 244, 236)); // Light warm beige
        frame.setContentPane(backgroundPanel);

        // --- Menu Bar ---
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("Menu");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem historyItem = new JMenuItem("My Bookings");

        logoutItem.addActionListener(e -> {
            frame.dispose();
            new LoginScreen(slotManager, transactionManager).display();
        });

        historyItem.addActionListener(e -> {
            List<Transaction> transactions = transactionManager.getAllTransactions().stream()
                    .filter(t -> t.user.equals(user.username))
                    .sorted(Comparator.comparing(t -> t.bookingDate))
                    .collect(Collectors.toList());

            JTextArea historyArea = new JTextArea(20, 60);
            for (Transaction t : transactions) {
                historyArea.append("Date: " + t.bookingDate + ", Computer: " + t.computerId +
                        ", Time: " + t.timeSlot + ", Paid: ₱" + t.amountPaid + ", Booked On: " + t.timestamp + "\n");
            }
            JOptionPane.showMessageDialog(frame, new JScrollPane(historyArea), "My Bookings", JOptionPane.INFORMATION_MESSAGE);
        });

        userMenu.add(logoutItem);
        userMenu.add(historyItem);
        menuBar.add(userMenu);
        frame.setJMenuBar(menuBar);

        // --- Table Setup ---
        String[] columnNames = new String[SlotManager.NUM_TIME_SLOTS + 1];
        columnNames[0] = "Computer #";
        for (int i = 0; i < SlotManager.NUM_TIME_SLOTS; i++) {
            columnNames[i + 1] = (i + 1) + " PM";
        }

        String[][] tableData = new String[SlotManager.NUM_COMPUTERS][SlotManager.NUM_TIME_SLOTS + 1];
        for (int i = 0; i < SlotManager.NUM_COMPUTERS; i++) {
            tableData[i][0] = "PC-" + (i + 1);
            for (int j = 0; j < SlotManager.NUM_TIME_SLOTS; j++) {
                tableData[i][j + 1] = "Available";
            }
        }

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Comic Sans MS", Font.BOLD, 16));

        // Custom cell renderer for coloring
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component comp = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                String cellValue = value != null ? value.toString() : "";

                if (col == 0) {
                    comp.setBackground(new Color(245, 238, 224)); // soft cream
                    comp.setForeground(new Color(70, 50, 30));
                    setHorizontalAlignment(LEFT);
                } else if (cellValue.toLowerCase().contains("booked")) {
                    comp.setBackground(new Color(248, 215, 218)); // warm coral/pink
                    comp.setForeground(new Color(115, 0, 15));
                    setHorizontalAlignment(CENTER);
                } else if (cellValue.toLowerCase().contains("available")) {
                    comp.setBackground(new Color(212, 237, 218)); // soft mint green
                    comp.setForeground(new Color(21, 87, 36));
                    setHorizontalAlignment(CENTER);
                } else {
                    comp.setBackground(Color.WHITE);
                    comp.setForeground(Color.BLACK);
                    setHorizontalAlignment(CENTER);
                }

                if (isSelected) {
                    comp.setBackground(new Color(255, 235, 205)); // warm highlight
                }

                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 130, 90), 3, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Fix column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Computer # column
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(60); // Time slots
        }

        // Set scroll pane preferred size and scrollbar policies
        scrollPane.setPreferredSize(new Dimension(750, 500));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // --- Controls Panel (Left Side) ---
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 130, 90), 3, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        controlsPanel.setBackground(new Color(250, 244, 236)); // light warm beige

        controlsPanel.setPreferredSize(new Dimension(300, 600)); // fix controls panel width

        JLabel titleLabel = new JLabel("Reserve Your Spot");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        titleLabel.setForeground(new Color(115, 70, 15));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        controlsPanel.add(titleLabel);

        JTextField dateField = new JTextField(10);
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dateField.setText(LocalDate.now().toString());
        dateField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 70, 20));

        JButton refreshButton = new JButton("Refresh the Burrow");
        refreshButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        refreshButton.setBackground(new Color(194, 159, 94));
        refreshButton.setForeground(new Color(60, 30, 5));
        refreshButton.setFocusPainted(false);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> computerBox = new JComboBox<>();
        for (int i = 0; i < SlotManager.NUM_COMPUTERS; i++) {
            computerBox.addItem("PC-" + (i + 1));
        }
        computerBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        computerBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        JLabel computerLabel = new JLabel("Computer:");
        computerLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        computerLabel.setForeground(new Color(100, 70, 20));

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        fromLabel.setForeground(new Color(100, 70, 20));

        JComboBox<String> fromSlotBox = new JComboBox<>();
        for (int i = 0; i < SlotManager.NUM_TIME_SLOTS; i++) {
            fromSlotBox.addItem((i + 1) + " PM");
        }
        fromSlotBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        fromSlotBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        toLabel.setForeground(new Color(100, 70, 20));

        JComboBox<String> toSlotBox = new JComboBox<>();
        for (int i = 0; i < SlotManager.NUM_TIME_SLOTS; i++) {
            toSlotBox.addItem((i + 1) + " PM");
        }
        toSlotBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        toSlotBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        JButton bookButton = new JButton("Reserve Your Spot");
        bookButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        bookButton.setBackground(new Color(194, 159, 94));
        bookButton.setForeground(new Color(60, 30, 5));
        bookButton.setFocusPainted(false);
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 13));
        messageLabel.setForeground(new Color(115, 30, 30));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Add components to controls panel
        controlsPanel.add(dateLabel);
        controlsPanel.add(dateField);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(refreshButton);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlsPanel.add(computerLabel);
        controlsPanel.add(computerBox);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(fromLabel);
        controlsPanel.add(fromSlotBox);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(toLabel);
        controlsPanel.add(toSlotBox);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlsPanel.add(bookButton);
        controlsPanel.add(messageLabel);

        // Add capybara icon image below controls
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/capybara_iconUI.jpg"));
Image originalImage = originalIcon.getImage();

// Scale image smoothly to desired size (e.g., 150x150)
Image scaledImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
ImageIcon scaledIcon = new ImageIcon(scaledImage);

// Set scaled icon on JLabel
JLabel imageLabel = new JLabel(scaledIcon);
imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
controlsPanel.add(imageLabel);
        // --- Action Listeners ---
        refreshButton.addActionListener(e -> {
            try {
                LocalDate selectedDate = LocalDate.parse(dateField.getText());
                slotManager.populateTable(selectedDate, model);
                messageLabel.setText("Table updated.");
            } catch (Exception ex) {
                messageLabel.setText("Invalid date format.");
            }
        });

        bookButton.addActionListener(e -> {
            int compIndex = computerBox.getSelectedIndex();
            int from = fromSlotBox.getSelectedIndex();
            int to = toSlotBox.getSelectedIndex();

            LocalDate today = LocalDate.now();
            int currentHour = LocalDateTime.now().getHour();

            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(dateField.getText());
                if (bookingDate.isBefore(today)) {
                    messageLabel.setText("Cannot book for past dates.");
                    return;
                }
            } catch (Exception ex) {
                messageLabel.setText("Invalid date format.");
                return;
            }

            if (from > to) {
                messageLabel.setText("Invalid time range.");
                return;
            }

            if (bookingDate.equals(today)) {
                for (int i = from; i <= to; i++) {
                    int slotHour = i + 13; // 1 PM = 13
                    if (slotHour <= currentHour) {
                        messageLabel.setText("One or more slots are in the past.");
                        return;
                    }
                }
            }

            for (int i = from; i <= to; i++) {
                if (slotManager.isSlotBooked(bookingDate, compIndex, i)) {
                    messageLabel.setText("Slot already booked.");
                    return;
                }
            }

            int hours = to - from + 1;
            int discount = (hours / 3) * 10;
            int baseCost = hours * 20;
            int totalCost = baseCost - discount;

            String timeSlotText = (from + 1) + " PM to " + (to + 1) + " PM";
            String compName = "PC-" + (compIndex + 1);

            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Confirm booking on " + bookingDate + "\n" +
                            compName + " | " + timeSlotText + "\nTotal: ₱" + totalCost,
                    "Confirm Booking", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            for (int i = from; i <= to; i++) {
                slotManager.bookSlot(bookingDate, compIndex, i, user.username);
                model.setValueAt("Booked by " + user.username, compIndex, i + 1);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Transaction t = new Transaction(user.username, compName, timeSlotText, totalCost, timestamp, bookingDate);
            transactionManager.addTransaction(t);

            ReceiptGenerator.generateReceipt(t);
            messageLabel.setText("Booked successfully: ₱" + totalCost);
        });

        // --- Clock Label and Timer ---
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clockLabel.setForeground(new Color(115, 70, 15));
        Timer clockTimer = new Timer(1000, e -> {
            LocalDate todayDate = LocalDate.parse(dateField.getText());
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText("Current Time: " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            slotManager.releaseExpiredBookings(todayDate, model);
            slotManager.populateTable(todayDate, model);
        });
        clockTimer.start();

        slotManager.populateTable(LocalDate.now(), model);

        // --- Footer Panel for empty space below table ---
        JPanel footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));
        footerPanel.setBackground(new Color(250, 244, 236)); // same warm beige
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel footerLabel = new JLabel("Thank you for choosing Capybara Internet Café!");
        footerLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
        footerLabel.setForeground(new Color(115, 70, 15));
        footerPanel.add(footerLabel);

        // Add components to frame
        frame.add(clockLabel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlsPanel, BorderLayout.WEST);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
