package repository;

import model.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private Connection conn;

    public EventRepository(Connection conn) { this.conn = conn; }

    public boolean createEvent(Event e) {
        String sql = "INSERT INTO events (title,description,category,location,event_date,start_time,end_time,max_participants,status,created_by) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTitle());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getCategory());
            ps.setString(4, e.getLocation());
            ps.setString(5, e.getEventDate());
            ps.setString(6, e.getStartTime());
            ps.setString(7, e.getEndTime());
            ps.setInt(8, e.getMaxParticipants());
            ps.setString(9, e.getStatus());
            ps.setInt(10, e.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean updateEvent(Event e) {
        String sql = "UPDATE events SET title=?,description=?,category=?,location=?,event_date=?,start_time=?,end_time=?,max_participants=?,status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getTitle());
            ps.setString(2, e.getDescription());
            ps.setString(3, e.getCategory());
            ps.setString(4, e.getLocation());
            ps.setString(5, e.getEventDate());
            ps.setString(6, e.getStartTime());
            ps.setString(7, e.getEndTime());
            ps.setInt(8, e.getMaxParticipants());
            ps.setString(9, e.getStatus());
            ps.setInt(10, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE events SET status=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Event> getAllEvents() {
        return queryEvents("SELECT e.*, u.full_name as creator_name, (SELECT COUNT(*) FROM registrations r WHERE r.event_id=e.id) as reg_count FROM events e LEFT JOIN users u ON e.created_by=u.id ORDER BY e.event_date DESC");
    }

    public List<Event> getApprovedEvents() {
        return queryEvents("SELECT e.*, u.full_name as creator_name, (SELECT COUNT(*) FROM registrations r WHERE r.event_id=e.id) as reg_count FROM events e LEFT JOIN users u ON e.created_by=u.id WHERE e.status='APPROVED' ORDER BY e.event_date ASC");
    }

    public List<Event> getPendingEvents() {
        return queryEvents("SELECT e.*, u.full_name as creator_name, (SELECT COUNT(*) FROM registrations r WHERE r.event_id=e.id) as reg_count FROM events e LEFT JOIN users u ON e.created_by=u.id WHERE e.status='PENDING' ORDER BY e.event_date ASC");
    }

    public Event getEventById(int id) {
        String sql = "SELECT e.*, u.full_name as creator_name, (SELECT COUNT(*) FROM registrations r WHERE r.event_id=e.id) as reg_count FROM events e LEFT JOIN users u ON e.created_by=u.id WHERE e.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEvent(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private List<Event> queryEvents(String sql) {
        List<Event> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapEvent(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setCategory(rs.getString("category"));
        e.setLocation(rs.getString("location"));
        e.setEventDate(rs.getString("event_date"));
        e.setStartTime(rs.getString("start_time"));
        e.setEndTime(rs.getString("end_time"));
        e.setMaxParticipants(rs.getInt("max_participants"));
        e.setStatus(rs.getString("status"));
        e.setCreatedBy(rs.getInt("created_by"));
        e.setCreatedAt(rs.getString("created_at"));
        e.setCreatorName(rs.getString("creator_name"));
        e.setRegisteredCount(rs.getInt("reg_count"));
        return e;
    }
}
