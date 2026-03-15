import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    public UserRole login(String username, String password) {

        FileHandler.initialize();

        String sql = "SELECT role FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String roleStr = rs.getString("role");
                return UserRole.valueOf(roleStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}