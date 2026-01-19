import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Student Grade Management System - Login");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JLabel message = new JLabel("", SwingConstants.CENTER);

        loginBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (user.equals("admin") && pass.equals("admin123")) {
                message.setText("✅ Login successful!");
                dispose(); // close login window
                new DashboardFrame(); // open main window
            } else {
                message.setText("❌ Invalid login.");
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(loginBtn, BorderLayout.NORTH);
        bottom.add(message, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
}
