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
import javax.swing.table.*;
import java.util.List;

public class AdminFrame extends JFrame {
    private User currentUser;
    private RepoManager repo;
    private JTabbedPane tabs;

    // Events tab
    private JTable tblEvents;
    private DefaultTableModel eventModel;

    // Pending tab
    private JTable tblPending;
    private DefaultTableModel pendingModel;

    // Attendance tab
    private JTable tblAttendance;
    private DefaultTableModel attModel;
    private JComboBox<Event> cbEventAtt;

    // Announcements tab
    private JTable tblAnn;
    private DefaultTableModel annModel;

    // Logs tab
    private JTable tblLogs;
    private DefaultTableModel logModel;

    public AdminFrame(User user) {
        this.currentUser = user;
        this.repo = RepoManager.getInstance();
        setTitle("Admin Dashboard — " + user.getFullName());
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
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel lblTitle = new JLabel("🎓 School Event Management — Admin");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerRight.setOpaque(false);
        JLabel lblUser = new JLabel("👤 " + currentUser.getFullName());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnLogout = smallBtn("Logout", new Color(220, 60, 60));
        btnLogout.addActionListener(e -> {
            repo.getLogRepo().log(currentUser.getId(), "LOGOUT", currentUser.getUsername() + " logged out.");
            new LoginFrame().setVisible(true);
            dispose();
        });
        headerRight.add(lblUser);
        headerRight.add(btnLogout);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setBackground(new Color(35, 35, 35));
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("📅 Events", buildEventsTab());
        tabs.addTab("⏳ Approvals", buildPendingTab());
        tabs.addTab("✅ Attendance", buildAttendanceTab());
        tabs.addTab("📢 Announcements", buildAnnouncementsTab());
        tabs.addTab("📋 Logs", buildLogsTab());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ---- EVENTS TAB ----
    private JPanel buildEventsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Title", "Category", "Date", "Location", "Status", "Registered"};
        eventModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblEvents = styledTable(eventModel);

        JScrollPane scroll = new JScrollPane(tblEvents);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setBackground(new Color(35, 35, 35));
        JButton btnAdd = smallBtn("+ Create Event", new Color(38, 100, 56));
        JButton btnEdit = smallBtn("✏ Edit", new Color(25, 118, 210));
        JButton btnDelete = smallBtn("🗑 Delete", new Color(200, 50, 50));
        JButton btnRefresh = smallBtn("🔄 Refresh", new Color(70, 70, 70));
        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDelete); btns.add(btnRefresh);

        p.add(btns, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            EventFormDialog d = new EventFormDialog(this, currentUser, null);
            d.setVisible(true);
            loadAll();
        });
        btnEdit.addActionListener(e -> {
            int row = tblEvents.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
            int id = (int) eventModel.getValueAt(row, 0);
            Event ev = repo.getEventRepo().getEventById(id);
            EventFormDialog d = new EventFormDialog(this, currentUser, ev);
            d.setVisible(true);
            loadAll();
        });
        btnDelete.addActionListener(e -> {
            int row = tblEvents.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
            int id = (int) eventModel.getValueAt(row, 0);
            String title = (String) eventModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete event: " + title + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repo.getEventRepo().deleteEvent(id);
                repo.getLogRepo().log(currentUser.getId(), "DELETE_EVENT", "Deleted: " + title);
                loadAll();
            }
        });
        btnRefresh.addActionListener(e -> loadAll());

        return p;
    }

    // ---- PENDING TAB ----
    private JPanel buildPendingTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Title", "Category", "Date", "Created By"};
        pendingModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblPending = styledTable(pendingModel);

        JScrollPane scroll = new JScrollPane(tblPending);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setBackground(new Color(35, 35, 35));
        JButton btnApprove = smallBtn("✅ Approve", new Color(38, 130, 56));
        JButton btnReject = smallBtn("❌ Reject", new Color(200, 80, 50));
        btns.add(btnApprove); btns.add(btnReject);

        p.add(btns, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        btnApprove.addActionListener(e -> {
            int row = tblPending.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event."); return; }
            int id = (int) pendingModel.getValueAt(row, 0);
            String title = (String) pendingModel.getValueAt(row, 1);
            repo.getEventRepo().updateStatus(id, "APPROVED");
            repo.getLogRepo().log(currentUser.getId(), "APPROVE_EVENT", "Approved: " + title);
            loadAll();
        });
        btnReject.addActionListener(e -> {
            int row = tblPending.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event."); return; }
            int id = (int) pendingModel.getValueAt(row, 0);
            String title = (String) pendingModel.getValueAt(row, 1);
            repo.getEventRepo().updateStatus(id, "CANCELLED");
            repo.getLogRepo().log(currentUser.getId(), "REJECT_EVENT", "Rejected: " + title);
            loadAll();
        });

        return p;
    }

    // ---- ATTENDANCE TAB ----
    private JPanel buildAttendanceTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(35, 35, 35));
        top.add(label("Event:"));
        cbEventAtt = new JComboBox<>();
        cbEventAtt.setPreferredSize(new Dimension(260, 30));
        cbEventAtt.setBackground(new Color(55, 55, 55));
        cbEventAtt.setForeground(Color.WHITE);
        top.add(cbEventAtt);
        JButton btnMark = smallBtn("✅ Mark Attendance", new Color(25, 118, 210));
        JButton btnLoad = smallBtn("Load", new Color(70, 70, 70));
        top.add(btnLoad);
        top.add(btnMark);

        String[] cols = {"Student ID", "Name", "Username", "Time In"};
        attModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblAttendance = styledTable(attModel);
        JScrollPane scroll = new JScrollPane(tblAttendance);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadAttendance());
        btnMark.addActionListener(e -> {
            Event ev = (Event) cbEventAtt.getSelectedItem();
            if (ev == null) return;
            List<model.User> students = repo.getRegistrationRepo().getRegisteredStudents(ev.getId());
            if (students.isEmpty()) { JOptionPane.showMessageDialog(this, "No registered students."); return; }
            String[] names = students.stream().map(u -> u.getFullName() + " (" + u.getUsername() + ")").toArray(String[]::new);
            String chosen = (String) JOptionPane.showInputDialog(this, "Select student:", "Mark Attendance",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
            if (chosen != null) {
                int idx = java.util.Arrays.asList(names).indexOf(chosen);
                model.User st = students.get(idx);
                if (repo.getAttendanceRepo().markAttendance(ev.getId(), st.getId())) {
                    repo.getLogRepo().log(currentUser.getId(), "MARK_ATTENDANCE",
                        st.getFullName() + " attended " + ev.getTitle());
                    JOptionPane.showMessageDialog(this, "Attendance marked!");
                    loadAttendance();
                } else {
                    JOptionPane.showMessageDialog(this, "Already marked or error.");
                }
            }
        });

        return p;
    }

    // ---- ANNOUNCEMENTS TAB ----
    private JPanel buildAnnouncementsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Title", "Event", "Posted By", "Date"};
        annModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblAnn = styledTable(annModel);
        JScrollPane scroll = new JScrollPane(tblAnn);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btns.setBackground(new Color(35, 35, 35));
        JButton btnPost = smallBtn("📢 Post Announcement", new Color(120, 70, 180));
        JButton btnDelete = smallBtn("🗑 Delete", new Color(200, 50, 50));
        btns.add(btnPost); btns.add(btnDelete);

        p.add(btns, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        btnPost.addActionListener(e -> {
            AnnouncementDialog d = new AnnouncementDialog(this, currentUser);
            d.setVisible(true);
            loadAll();
        });
        btnDelete.addActionListener(e -> {
            int row = tblAnn.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an announcement."); return; }
            int id = (int) annModel.getValueAt(row, 0);
            repo.getAnnouncementRepo().delete(id);
            repo.getLogRepo().log(currentUser.getId(), "DELETE_ANN", "Deleted announcement #" + id);
            loadAll();
        });

        return p;
    }

    // ---- LOGS TAB ----
    private JPanel buildLogsTab() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Username", "Action", "Details", "Timestamp"};
        logModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblLogs = styledTable(logModel);
        JScrollPane scroll = new JScrollPane(tblLogs);
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.setBackground(new Color(35, 35, 35));
        JButton btnRefresh = smallBtn("🔄 Refresh Logs", new Color(70, 70, 70));
        top.add(btnRefresh);

        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> loadLogs());
        return p;
    }

    // ---- DATA LOADERS ----
    public void loadAll() {
        loadEvents();
        loadPending();
        loadAttendanceCombo();
        loadAttendance();
        loadAnnouncements();
        loadLogs();
    }

    private void loadEvents() {
        eventModel.setRowCount(0);
        for (Event e : repo.getEventRepo().getAllEvents()) {
            eventModel.addRow(new Object[]{e.getId(), e.getTitle(), e.getCategory(),
                e.getEventDate(), e.getLocation(), e.getStatus(), e.getRegisteredCount()});
        }
    }

    private void loadPending() {
        pendingModel.setRowCount(0);
        for (Event e : repo.getEventRepo().getPendingEvents()) {
            pendingModel.addRow(new Object[]{e.getId(), e.getTitle(), e.getCategory(),
                e.getEventDate(), e.getCreatorName()});
        }
    }

    private void loadAttendanceCombo() {
        cbEventAtt.removeAllItems();
        for (Event e : repo.getEventRepo().getAllEvents()) cbEventAtt.addItem(e);
    }

    private void loadAttendance() {
        attModel.setRowCount(0);
        Event ev = (Event) cbEventAtt.getSelectedItem();
        if (ev == null) return;
        for (model.User u : repo.getAttendanceRepo().getAttendees(ev.getId())) {
            attModel.addRow(new Object[]{u.getId(), u.getFullName(), u.getUsername(), u.getCreatedAt()});
        }
    }

    private void loadAnnouncements() {
        annModel.setRowCount(0);
        for (Announcement a : repo.getAnnouncementRepo().getAll()) {
            annModel.addRow(new Object[]{a.getId(), a.getTitle(),
                a.getEventTitle() != null ? a.getEventTitle() : "General",
                a.getPosterName(), a.getCreatedAt()});
        }
    }

    private void loadLogs() {
        logModel.setRowCount(0);
        for (ActivityLog log : repo.getLogRepo().getAll()) {
            logModel.addRow(new Object[]{log.getId(), log.getUsername(), log.getAction(),
                log.getDetails(), log.getLoggedAt()});
        }
    }

    // ---- HELPERS ----
    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(25, 118, 210));
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

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 180));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }
}
