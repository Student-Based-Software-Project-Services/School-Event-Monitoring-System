package repository;

import model.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository {
    private Connection conn;

    public ActivityLogRepository(Connection conn) { this.conn = conn; }

    public void log(int userId, String action, String details) {
        String sql = "INSERT INTO activity_logs (user_id, action, details) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId > 0) ps.setInt(1, userId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ActivityLog> getAll() {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al LEFT JOIN users u ON al.user_id=u.id ORDER BY al.logged_at DESC LIMIT 200";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                log.setLoggedAt(rs.getString("logged_at"));
                list.add(log);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
