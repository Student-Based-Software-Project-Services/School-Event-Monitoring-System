package repository;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {
    private Connection conn;

    public AttendanceRepository(Connection conn) { this.conn = conn; }

    public boolean markAttendance(int eventId, int studentId) {
        String sql = "INSERT IGNORE INTO attendance (event_id, student_id) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean hasAttended(int eventId, int studentId) {
        String sql = "SELECT id FROM attendance WHERE event_id=? AND student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getAttendees(int eventId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, a.time_in FROM users u JOIN attendance a ON u.id=a.student_id WHERE a.event_id=? ORDER BY a.time_in";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setCreatedAt(rs.getString("time_in"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getAttendanceCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE event_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
