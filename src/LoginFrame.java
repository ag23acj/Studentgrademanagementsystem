import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
                new DashboardFrame(true);
                dispose();
                return;
            }

            if (username.equalsIgnoreCase("user") && password.equals("user123")) {
                new DashboardFrame(false);
                dispose();
                return;
            }

            JOptionPane.showMessageDialog(this, "Invalid login!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
