package frames;

import model.Event;
import model.User;
import repository.RepoManager;
import javax.swing.*;
import java.awt.*;

public class EventFormDialog extends JDialog {
    private User currentUser;
    private Event existingEvent;
    private RepoManager repo = RepoManager.getInstance();

    private JTextField txtTitle, txtLocation, txtDate, txtStart, txtEnd, txtMax;
    private JComboBox<String> cbCategory, cbStatus;
    private JTextArea txtDesc;

    public EventFormDialog(JFrame parent, User user, Event event) {
        super(parent, event == null ? "Create Event" : "Edit Event", true);
        this.currentUser = user;
        this.existingEvent = event;
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        if (event != null) populate(event);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(new Color(40, 40, 40));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel(existingEvent == null ? "Create New Event" : "Edit Event");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(new Color(40, 40, 40));

        txtTitle = field(); txtLocation = field();
        txtDate = field(); txtDate.setToolTipText("Format: YYYY-MM-DD");
        txtStart = field(); txtStart.setToolTipText("Format: HH:MM");
        txtEnd = field(); txtEnd.setToolTipText("Format: HH:MM");
        txtMax = field(); txtMax.setText("100");
        cbCategory = new JComboBox<>(new String[]{"SEMINAR","SPORTS","ORGANIZATION","WORKSHOP","COMPETITION","PROGRAM","OTHER"});
        styleCombo(cbCategory);
        cbStatus = new JComboBox<>(new String[]{"PENDING","APPROVED","CANCELLED","COMPLETED"});
        styleCombo(cbStatus);
        if (!currentUser.isAdmin()) cbStatus.setEnabled(false);

        form.add(flabel("Title:")); form.add(txtTitle);
        form.add(flabel("Category:")); form.add(cbCategory);
        form.add(flabel("Location:")); form.add(txtLocation);
        form.add(flabel("Event Date (YYYY-MM-DD):")); form.add(txtDate);
        form.add(flabel("Start Time (HH:MM):")); form.add(txtStart);
        form.add(flabel("End Time (HH:MM):")); form.add(txtEnd);
        form.add(flabel("Max Participants:")); form.add(txtMax);
        form.add(flabel("Status:")); form.add(cbStatus);

        txtDesc = new JTextArea(3, 20);
        txtDesc.setBackground(new Color(55, 55, 55));
        txtDesc.setForeground(Color.WHITE);
        txtDesc.setCaretColor(Color.WHITE);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        JScrollPane descScroll = new JScrollPane(txtDesc);
        descScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80,80,80)), "Description",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.PLAIN, 12), new Color(160,160,160)));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setBackground(new Color(40, 40, 40));
        JButton btnSave = btn("Save", new Color(25, 118, 210));
        JButton btnCancel = btn("Cancel", new Color(70, 70, 70));
        btns.add(btnCancel); btns.add(btnSave);

        root.add(title, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(new Color(40, 40, 40));
        center.add(form, BorderLayout.NORTH);
        center.add(descScroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void populate(Event e) {
        txtTitle.setText(e.getTitle());
        txtLocation.setText(e.getLocation());
        txtDate.setText(e.getEventDate());
        txtStart.setText(e.getStartTime());
        txtEnd.setText(e.getEndTime());
        txtMax.setText(String.valueOf(e.getMaxParticipants()));
        txtDesc.setText(e.getDescription());
        cbCategory.setSelectedItem(e.getCategory());
        cbStatus.setSelectedItem(e.getStatus());
    }

    private void save() {
        String title = txtTitle.getText().trim();
        String date = txtDate.getText().trim();
        if (title.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Date are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int max;
        try { max = Integer.parseInt(txtMax.getText().trim()); } catch (NumberFormatException ex) { max = 100; }

        Event e = existingEvent != null ? existingEvent : new Event();
        e.setTitle(title);
        e.setDescription(txtDesc.getText().trim());
        e.setCategory((String) cbCategory.getSelectedItem());
        e.setLocation(txtLocation.getText().trim());
        e.setEventDate(date);
        e.setStartTime(txtStart.getText().trim());
        e.setEndTime(txtEnd.getText().trim());
        e.setMaxParticipants(max);
        e.setStatus((String) cbStatus.getSelectedItem());
        e.setCreatedBy(currentUser.getId());

        boolean ok;
        if (existingEvent == null) {
            if (!currentUser.isAdmin()) e.setStatus("PENDING");
            ok = repo.getEventRepo().createEvent(e);
            if (ok) repo.getLogRepo().log(currentUser.getId(), "CREATE_EVENT", "Created: " + title);
        } else {
            ok = repo.getEventRepo().updateEvent(e);
            if (ok) repo.getLogRepo().log(currentUser.getId(), "EDIT_EVENT", "Edited: " + title);
        }
        if (ok) {
            JOptionPane.showMessageDialog(this, "Event saved successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save event.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(new Color(55, 55, 55));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }

    private JLabel flabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(180, 180, 180));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private void styleCombo(JComboBox<?> c) {
        c.setBackground(new Color(55, 55, 55));
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
