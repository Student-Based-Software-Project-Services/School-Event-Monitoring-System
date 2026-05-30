package repository;

import model.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementRepository {
    private Connection conn;

    public AnnouncementRepository(Connection conn) { this.conn = conn; }

    public boolean post(Announcement a) {
        String sql = "INSERT INTO announcements (title, message, event_id, posted_by) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getMessage());
            if (a.getEventId() > 0) ps.setInt(3, a.getEventId()); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, a.getPostedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM announcements WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Announcement> getAll() {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as poster_name, e.title as event_title FROM announcements a LEFT JOIN users u ON a.posted_by=u.id LEFT JOIN events e ON a.event_id=e.id ORDER BY a.created_at DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Announcement map(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setMessage(rs.getString("message"));
        a.setEventId(rs.getInt("event_id"));
        a.setEventTitle(rs.getString("event_title"));
        a.setPostedBy(rs.getInt("posted_by"));
        a.setPosterName(rs.getString("poster_name"));
        a.setCreatedAt(rs.getString("created_at"));
        return a;
    }
}
