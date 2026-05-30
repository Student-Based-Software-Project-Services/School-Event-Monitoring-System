package repository;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationRepository {
    private Connection conn;

    public RegistrationRepository(Connection conn) { this.conn = conn; }

    public boolean register(int eventId, int studentId) {
        String sql = "INSERT INTO registrations (event_id, student_id) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean unregister(int eventId, int studentId) {
        String sql = "DELETE FROM registrations WHERE event_id=? AND student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean isRegistered(int eventId, int studentId) {
        String sql = "SELECT id FROM registrations WHERE event_id=? AND student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getRegisteredStudents(int eventId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN registrations r ON u.id=r.student_id WHERE r.event_id=? ORDER BY u.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Integer> getRegisteredEventIds(int studentId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT event_id FROM registrations WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt("event_id"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }
}
