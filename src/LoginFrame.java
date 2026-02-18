import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("Login - Student Grade Management System");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        panel.add(passField);

        JButton loginButton = new JButton("Login");

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        // Default test accounts
        // admin / admin123
        // user  / user123
        loginButton.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            UserRole role;

            if (username.equals("admin") && password.equals("admin123")) {
                role = UserRole.ADMIN;
            } else if (username.equals("user") && password.equals("user123")) {
                role = UserRole.TUTOR;
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid login.");
                return;
            }

            JOptionPane.showMessageDialog(this, "✅ Login successful: " + role);
            dispose();
            new DashboardFrame(role);
        });

        setVisible(true);
    }
}
