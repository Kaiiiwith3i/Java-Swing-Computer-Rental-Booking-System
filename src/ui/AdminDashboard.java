package ui;

import manager.SlotManager;
import model.TransactionManager;
import model.Transaction;
import model.User;
import utils.ReceiptGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboard {
    private final User user;
    private final SlotManager slotManager;
    private final TransactionManager transactionManager;

    public AdminDashboard(User user, SlotManager slotManager, TransactionManager transactionManager) {
        this.user = user;
        this.slotManager = slotManager;
        this.transactionManager = transactionManager;
    }

    public void display() {
        JFrame frame = new JFrame("Admin Dashboard - " + user.username);
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel with warm beige color
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(new Color(250, 244, 236));
        frame.setContentPane(backgroundPanel);

        // --- Menu Bar ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exportReportItem = new JMenuItem("Export Bookings by Date");

        logoutItem.addActionListener(e -> {
            frame.dispose();
            new LoginScreen(slotManager, transactionManager).display();
        });

        exportReportItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter date to export (yyyy-MM-dd):");
            if (input == null || input.isBlank()) return;

            try {
                LocalDate selectedDate = LocalDate.parse(input);
                List<Transaction> transactions = transactionManager.getAllTransactions();

                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Export all bookings for " + selectedDate + "?",
                        "Confirm Export", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                try (FileWriter writer = new FileWriter("admin_day_report_" + selectedDate + ".txt")) {
                    for (Transaction t : transactions) {
                        if (t.bookingDate.equals(selectedDate)) {
                            writer.write("User: " + t.user + ", Computer: " + t.computerId +
                                    ", Slot: " + t.timeSlot + ", Paid: ₱" + t.amountPaid +
                                    ", Time: " + t.timestamp + "\n");
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Report exported successfully for " + selectedDate);
                }
            } catch (IOException | RuntimeException ex) {
                JOptionPane.showMessageDialog(frame, "Error exporting report: " + ex.getMessage());
            }
        });

        menu.add(logoutItem);
        menu.add(exportReportItem);
        menuBar.add(menu);
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
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Comic Sans MS", Font.BOLD, 16));

        // Enable dynamic column resizing
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Custom cell renderer for coloring booked/available cells
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
        scrollPane.setPreferredSize(new Dimension(1050, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 130, 90), 3, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // --- Action Panel with GridBagLayout on left ---
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 130, 90), 3, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        actionPanel.setBackground(new Color(250, 244, 236));
        actionPanel.setPreferredSize(new Dimension(350, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Manage Bookings");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        titleLabel.setForeground(new Color(115, 70, 15));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        actionPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        gbc.gridx = 0;
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 70, 20));
        actionPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        actionPanel.add(dateField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel computerLabel = new JLabel("Computer:");
        computerLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        computerLabel.setForeground(new Color(100, 70, 20));
        actionPanel.add(computerLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> computerBox = new JComboBox<>();
        for (int i = 0; i < SlotManager.NUM_COMPUTERS; i++) computerBox.addItem("PC-" + (i + 1));
        computerBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        actionPanel.add(computerBox, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel timeSlotLabel = new JLabel("Time Slot:");
        timeSlotLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        timeSlotLabel.setForeground(new Color(100, 70, 20));
        actionPanel.add(timeSlotLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> timeSlotBox = new JComboBox<>();
        for (int i = 0; i < SlotManager.NUM_TIME_SLOTS; i++) timeSlotBox.addItem((i + 1) + " PM");
        timeSlotBox.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        actionPanel.add(timeSlotBox, gbc);

        gbc.gridy++; gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton refreshButton = new JButton("Load Date");
        refreshButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        refreshButton.setBackground(new Color(194, 159, 94));
        refreshButton.setForeground(new Color(60, 30, 5));
        refreshButton.setFocusPainted(false);
        actionPanel.add(refreshButton, gbc);

        gbc.gridy++;
        JButton removeButton = new JButton("Remove Booking");
        removeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        removeButton.setBackground(new Color(194, 159, 94));
        removeButton.setForeground(new Color(60, 30, 5));
        removeButton.setFocusPainted(false);
        actionPanel.add(removeButton, gbc);

        gbc.gridy++;
        JButton reportButton = new JButton("Show Transactions");
        reportButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        reportButton.setBackground(new Color(194, 159, 94));
        reportButton.setForeground(new Color(60, 30, 5));
        reportButton.setFocusPainted(false);
        actionPanel.add(reportButton, gbc);

        gbc.gridy++;
        JButton receiptButton = new JButton("Generate Receipt for Last");
        receiptButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        receiptButton.setBackground(new Color(194, 159, 94));
        receiptButton.setForeground(new Color(60, 30, 5));
        receiptButton.setFocusPainted(false);
        actionPanel.add(receiptButton, gbc);

        gbc.gridy++;
        JLabel actionLabel = new JLabel(" ");
        actionLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 13));
        actionLabel.setForeground(new Color(115, 30, 30));
        actionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        actionPanel.add(actionLabel, gbc);
        
        gbc.gridy++;               // Move to next row
gbc.gridx = 0;
gbc.gridwidth = 2;         // Span across two columns to center nicely

// Load and scale the image (replace with your image path/resource)
ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/capybara_iconAdmin.jpg"));
Image originalImage = originalIcon.getImage();
Image scaledImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
ImageIcon scaledIcon = new ImageIcon(scaledImage);

JLabel imageLabel = new JLabel(scaledIcon);
imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

actionPanel.add(imageLabel, gbc);

        // --- Action Listeners ---
        refreshButton.addActionListener(e -> {
            try {
                LocalDate selectedDate = LocalDate.parse(dateField.getText());
                slotManager.populateTable(selectedDate, model);
                actionLabel.setText("Table updated.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Use yyyy-MM-dd");
            }
        });

        removeButton.addActionListener(e -> {
            try {
                LocalDate selectedDate = LocalDate.parse(dateField.getText());
                int compIndex = computerBox.getSelectedIndex();
                int timeIndex = timeSlotBox.getSelectedIndex();

                if (!slotManager.isSlotBooked(selectedDate, compIndex, timeIndex)) {
                    actionLabel.setText("Slot already available");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Remove booking for PC-" + (compIndex + 1) + " at " + (timeIndex + 1) + " PM on " + selectedDate + "?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                slotManager.clearSlot(selectedDate, compIndex, timeIndex);
                slotManager.populateTable(selectedDate, model);
                actionLabel.setText("Booking removed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format.");
            }
        });

        reportButton.addActionListener(e -> {
            try {
                LocalDate selectedDate = LocalDate.parse(dateField.getText());
                List<Transaction> transactions = transactionManager.getAllTransactions();
                JTextArea reportArea = new JTextArea(20, 60);
                for (Transaction t : transactions) {
                    if (t.bookingDate.equals(selectedDate)) {
                        reportArea.append("User: " + t.user + ", Computer: " + t.computerId +
                                ", Slot: " + t.timeSlot + ", Paid: ₱" + t.amountPaid +
                                ", Time: " + t.timestamp + "\n");
                    }
                }
                JOptionPane.showMessageDialog(frame, new JScrollPane(reportArea),
                        "Transactions on " + selectedDate, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format.");
            }
        });

        receiptButton.addActionListener(e -> {
            List<Transaction> transactions = transactionManager.getAllTransactions();
            if (!transactions.isEmpty()) {
                Transaction last = transactions.get(transactions.size() - 1);
                ReceiptGenerator.generateReceipt(last);
                actionLabel.setText("Receipt saved.");
            } else {
                actionLabel.setText("No transactions yet.");
            }
        });

        // --- Clock Label and Timer ---
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clockLabel.setForeground(new Color(115, 70, 15));
        Timer clockTimer = new Timer(1000, e -> {
            LocalDate current = LocalDate.parse(dateField.getText());
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText("Current Time: " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            slotManager.releaseExpiredBookings(current, model);
            slotManager.populateTable(current, model);
        });
        clockTimer.start();

        slotManager.populateTable(LocalDate.now(), model);

        // Add components to frame
        frame.add(clockLabel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.WEST);

        frame.setVisible(true);
    }
}
