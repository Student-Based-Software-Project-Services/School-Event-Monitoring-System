package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import model.*;
import repository.RepoManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class StudentFrame extends JFrame {
    private User currentUser;
    private RepoManager repo;
    private JTabbedPane tabs;

    // Events tab
    private JTable tblEvents;
    private DefaultTableModel eventModel;

    // My Events tab
    private JTable tblMyEvents;
    private DefaultTableModel myEventModel;

    // Announcements tab
    private JTable tblAnn;
    private DefaultTableModel annModel;

    public StudentFrame(User user) {
        this.currentUser = user;
        this.repo = RepoManager.getInstance();
        setTitle("Student Dashboard — " + user.getFullName());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        loadAll();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(38, 100, 56));
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel lblTitle = new JLabel("🎓 School Event Management — Student Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hRight.setOpaque(false);
        JLabel lblUser = new JLabel("👤 " + currentUser.getFullName());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnLogout = smallBtn("Logout", new Color(200, 50, 50));
        btnLogout.addActionListener(e -> {
            repo.getLogRepo().log(currentUser.getId(), "LOGOUT", currentUser.getUsername() + " logged out.");
            new LoginFrame().setVisible(true);
            dispose();
        });
        hRight.add(lblUser); hRight.add(btnLogout);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(hRight, BorderLayout.EAST);

        tabs = new JTabbedPane();
        tabs.setBackground(new Color(35, 35, 35));
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("📅 Available Events", buildEventsTab());
        tabs.addTab("📌 My Registrations", buildMyEventsTab());
        tabs.addTab("📢 Announcements", buildAnnouncementsTab());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ---- AVAILABLE EVENTS ----
    private JPanel buildEventsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Title", "Category", "Date", "Location", "Slots Left", "Registered?"};
        eventModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblEvents = styledTable(eventModel);

        JScrollPane scroll = new JScrollPane(tblEvents);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setBackground(new Color(35, 35, 35));
        JButton btnRegister = smallBtn("✅ Register", new Color(38, 130, 56));
        JButton btnUnregister = smallBtn("❌ Unregister", new Color(180, 60, 60));
        JButton btnDetails = smallBtn("🔍 Details", new Color(25, 118, 210));
        JButton btnRefresh = smallBtn("🔄 Refresh", new Color(70, 70, 70));
        btns.add(btnRegister); btns.add(btnUnregister); btns.add(btnDetails); btns.add(btnRefresh);

        p.add(btns, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> doRegister());
        btnUnregister.addActionListener(e -> doUnregister());
        btnRefresh.addActionListener(e -> loadAll());
        btnDetails.addActionListener(e -> showDetails());

        return p;
    }

    private void doRegister() {
        int row = tblEvents.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        int id = (int) eventModel.getValueAt(row, 0);
        String title = (String) eventModel.getValueAt(row, 1);
        if (repo.getRegistrationRepo().isRegistered(id, currentUser.getId())) {
            JOptionPane.showMessageDialog(this, "You are already registered for this event.");
            return;
        }
        if (repo.getRegistrationRepo().register(id, currentUser.getId())) {
            repo.getLogRepo().log(currentUser.getId(), "REGISTER_EVENT", currentUser.getFullName() + " registered for: " + title);
            JOptionPane.showMessageDialog(this, "Successfully registered for: " + title);
            loadAll();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUnregister() {
        int row = tblEvents.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        int id = (int) eventModel.getValueAt(row, 0);
        String title = (String) eventModel.getValueAt(row, 1);
        if (!repo.getRegistrationRepo().isRegistered(id, currentUser.getId())) {
            JOptionPane.showMessageDialog(this, "You are not registered for this event.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Unregister from: " + title + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.getRegistrationRepo().unregister(id, currentUser.getId());
            repo.getLogRepo().log(currentUser.getId(), "UNREGISTER_EVENT", currentUser.getFullName() + " unregistered from: " + title);
            loadAll();
        }
    }

    private void showDetails() {
        int row = tblEvents.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        int id = (int) eventModel.getValueAt(row, 0);
        Event ev = repo.getEventRepo().getEventById(id);
        if (ev == null) return;
        boolean attended = repo.getAttendanceRepo().hasAttended(id, currentUser.getId());
        int attCount = repo.getAttendanceRepo().getAttendanceCount(id);
        String msg = "Title: " + ev.getTitle() + "\n"
            + "Category: " + ev.getCategory() + "\n"
            + "Date: " + ev.getEventDate() + "\n"
            + "Time: " + ev.getStartTime() + " - " + ev.getEndTime() + "\n"
            + "Location: " + ev.getLocation() + "\n"
            + "Max Participants: " + ev.getMaxParticipants() + "\n"
            + "Registered: " + ev.getRegisteredCount() + "\n"
            + "Attendance Count: " + attCount + "\n"
            + "Your Attendance: " + (attended ? "✅ Marked" : "Not yet") + "\n\n"
            + "Description:\n" + (ev.getDescription() != null ? ev.getDescription() : "");
        JTextArea ta = new JTextArea(msg);
        ta.setEditable(false);
        ta.setBackground(new Color(45, 45, 45));
        ta.setForeground(Color.WHITE);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane sc = new JScrollPane(ta);
        sc.setPreferredSize(new Dimension(400, 260));
        JOptionPane.showMessageDialog(this, sc, "Event Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---- MY EVENTS ----
    private JPanel buildMyEventsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Title", "Category", "Date", "Location", "Status", "Attended"};
        myEventModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblMyEvents = styledTable(myEventModel);
        JScrollPane scroll = new JScrollPane(tblMyEvents);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setBackground(new Color(35, 35, 35));
        JButton btnRefresh = smallBtn("🔄 Refresh", new Color(70, 70, 70));
        btns.add(btnRefresh);

        p.add(btns, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> loadMyEvents());
        return p;
    }

    // ---- ANNOUNCEMENTS ----
    private JPanel buildAnnouncementsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Title", "Event", "Posted By", "Date"};
        annModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblAnn = styledTable(annModel);

        // Message preview below table
        JTextArea preview = new JTextArea(4, 20);
        preview.setEditable(false);
        preview.setBackground(new Color(50, 50, 50));
        preview.setForeground(new Color(220, 220, 220));
        preview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        preview.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80,80,80)), "Message",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.PLAIN, 11), new Color(150,150,150)));

        JScrollPane topScroll = new JScrollPane(tblAnn);
        topScroll.getViewport().setBackground(new Color(45, 45, 45));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScroll, new JScrollPane(preview));
        split.setDividerLocation(260);
        split.setBackground(new Color(35, 35, 35));

        List<Announcement> announcements = repo.getAnnouncementRepo().getAll();
        tblAnn.getSelectionModel().addListSelectionListener(e -> {
            int row = tblAnn.getSelectedRow();
            if (row >= 0 && row < announcements.size()) {
                preview.setText(announcements.get(row).getMessage());
            }
        });

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    // ---- LOADERS ----
    public void loadAll() {
        loadEvents();
        loadMyEvents();
        loadAnnouncements();
    }

    private void loadEvents() {
        eventModel.setRowCount(0);
        List<Integer> myIds = repo.getRegistrationRepo().getRegisteredEventIds(currentUser.getId());
        for (Event e : repo.getEventRepo().getApprovedEvents()) {
            int slots = e.getMaxParticipants() - e.getRegisteredCount();
            boolean reg = myIds.contains(e.getId());
            eventModel.addRow(new Object[]{e.getId(), e.getTitle(), e.getCategory(),
                e.getEventDate(), e.getLocation(), slots > 0 ? slots : "Full", reg ? "Yes ✅" : "No"});
        }
    }

    private void loadMyEvents() {
        myEventModel.setRowCount(0);
        List<Integer> myIds = repo.getRegistrationRepo().getRegisteredEventIds(currentUser.getId());
        for (Event e : repo.getEventRepo().getAllEvents()) {
            if (myIds.contains(e.getId())) {
                boolean attended = repo.getAttendanceRepo().hasAttended(e.getId(), currentUser.getId());
                myEventModel.addRow(new Object[]{e.getId(), e.getTitle(), e.getCategory(),
                    e.getEventDate(), e.getLocation(), e.getStatus(), attended ? "Yes ✅" : "No"});
            }
        }
    }

    private void loadAnnouncements() {
        annModel.setRowCount(0);
        for (Announcement a : repo.getAnnouncementRepo().getAll()) {
            annModel.addRow(new Object[]{a.getTitle(),
                a.getEventTitle() != null ? a.getEventTitle() : "General",
                a.getPosterName(), a.getCreatedAt()});
        }
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(38, 130, 56));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 60));
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setBackground(new Color(35, 35, 35));
        t.getTableHeader().setForeground(new Color(180, 180, 180));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    private JButton smallBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
