package ui;

import model.User;
import manager.SlotManager;
import model.TransactionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class LoginScreen {
    private final SlotManager slotManager;
    private final TransactionManager transactionManager;
    private final User[] users = {
        new User("admin", "admin123", "admin"),
        new User("john", "john123", "customer"),
        new User("kai", "kai123", "customer")
    };

    public LoginScreen(SlotManager slotManager, TransactionManager transactionManager) {
        this.slotManager = slotManager;
        this.transactionManager = transactionManager;
    }

    public void display() {
        // Create frame
        JFrame loginFrame = new JFrame("Capybara Internet Café");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(true);
        loginFrame.setMinimumSize(new Dimension(350, 320));
        
        

        // Set custom font - fallback if font unavailable
        Font cozyFont = new Font("Comic Sans MS", Font.PLAIN, 14);

        // Background panel with image and overlay color
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/images/capybara_cafe_bg.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw background image scaled
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                // Semi-transparent overlay for readability
                g.setColor(new Color(255, 255, 240, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Main panel with padding and vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title with capybara icon
        JLabel titleLabel = new JLabel("Capybara Internet Café");
        titleLabel.setFont(cozyFont.deriveFont(Font.BOLD, 22f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(70, 50, 30));

        // Capybara icon
        ImageIcon capyIcon = new ImageIcon(getClass().getResource("/images/capybara_icon.jpg"));
        Image scaledIcon = capyIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Username field
        JLabel userLabel = new JLabel("Capybara Name:");
        userLabel.setFont(cozyFont);
        userLabel.setForeground(new Color(70, 50, 30));
        JTextField usernameField = new JTextField();
        usernameField.setFont(cozyFont);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 130, 90), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Password field
        JLabel passLabel = new JLabel("Secret Snack Code:");
        passLabel.setFont(cozyFont);
        passLabel.setForeground(new Color(70, 50, 30));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(cozyFont);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 130, 90), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setEchoChar('•');

        // Show password checkbox
        JCheckBox showPassword = new JCheckBox("Let me peek 🫣");
        showPassword.setOpaque(false);
        showPassword.setFont(cozyFont);
        showPassword.setForeground(new Color(70, 50, 30));
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPassword.setToolTipText("Click to see your secret snack code!");

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Status label for feedback
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(cozyFont.deriveFont(Font.ITALIC, 12f));
        statusLabel.setForeground(new Color(150, 50, 50));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        JButton loginButton = new JButton("Enter the Burrow 🐾");
        loginButton.setFont(cozyFont.deriveFont(Font.BOLD, 16f));
        loginButton.setBackground(new Color(170, 140, 100));
        loginButton.setForeground(new Color(40, 20, 0));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(140, 110, 70), 2, true));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setOpaque(true);

        // Button hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(200, 170, 110));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(170, 140, 100));
            }
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            Arrays.stream(users)
                .filter(user -> user.username.equals(username) && user.password.equals(password))
                .findFirst()
                .ifPresentOrElse(user -> {
                    loginFrame.dispose();
                    if (user.role.equals("admin")) {
                        new AdminDashboard(user, slotManager, transactionManager).display();
                    } else {
                        new CustomerBookingScreen(user, slotManager, transactionManager).display();
                    }
                }, () -> {
                    statusLabel.setText("Invalid login");
                });
        });

        // Add components to main panel with spacing
        mainPanel.add(titleLabel);
        mainPanel.add(iconLabel);
        mainPanel.add(userLabel);
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(passLabel);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(showPassword);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        backgroundPanel.add(mainPanel);
        loginFrame.setContentPane(backgroundPanel);
        loginFrame.setVisible(true);
    }
}
