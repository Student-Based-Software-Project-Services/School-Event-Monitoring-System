package frames;

import model.Announcement;
import model.Event;
import model.User;
import repository.RepoManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AnnouncementDialog extends JDialog {
    private User currentUser;
    private RepoManager repo = RepoManager.getInstance();
    private JTextField txtTitle;
    private JTextArea txtMessage;
    private JComboBox<Object> cbEvent;

    public AnnouncementDialog(JFrame parent, User user) {
        super(parent, "Post Announcement", true);
        this.currentUser = user;
        setSize(460, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(new Color(40, 40, 40));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("📢 Post Announcement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(new Color(40, 40, 40));

        form.add(flabel("Title:"));
        txtTitle = field();
        form.add(txtTitle);

        form.add(flabel("Link to Event (optional):"));
        cbEvent = new JComboBox<>();
        cbEvent.setBackground(new Color(55, 55, 55));
        cbEvent.setForeground(Color.WHITE);
        cbEvent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbEvent.addItem("-- No specific event --");
        List<Event> events = repo.getEventRepo().getAllEvents();
        for (Event e : events) cbEvent.addItem(e);
        form.add(cbEvent);

        form.add(flabel("Message:"));
        txtMessage = new JTextArea(5, 20);
        txtMessage.setBackground(new Color(55, 55, 55));
        txtMessage.setForeground(Color.WHITE);
        txtMessage.setCaretColor(Color.WHITE);
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        JScrollPane sc = new JScrollPane(txtMessage);
        sc.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        form.add(sc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setBackground(new Color(40, 40, 40));
        JButton btnPost = btn("Post", new Color(120, 70, 180));
        JButton btnCancel = btn("Cancel", new Color(70, 70, 70));
        btns.add(btnCancel); btns.add(btnPost);

        root.add(title, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(btns, BorderLayout.SOUTH);
        setContentPane(root);

        btnPost.addActionListener(e -> post());
        btnCancel.addActionListener(e -> dispose());
    }

    private void post() {
        String t = txtTitle.getText().trim();
        String msg = txtMessage.getText().trim();
        if (t.isEmpty() || msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and message are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Announcement a = new Announcement();
        a.setTitle(t);
        a.setMessage(msg);
        a.setPostedBy(currentUser.getId());
        Object sel = cbEvent.getSelectedItem();
        if (sel instanceof Event) a.setEventId(((Event) sel).getId());
        if (repo.getAnnouncementRepo().post(a)) {
            repo.getLogRepo().log(currentUser.getId(), "POST_ANN", "Posted: " + t);
            JOptionPane.showMessageDialog(this, "Announcement posted!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to post.", "Error", JOptionPane.ERROR_MESSAGE);
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
