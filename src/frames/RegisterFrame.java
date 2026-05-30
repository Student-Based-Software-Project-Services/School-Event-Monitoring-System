package frames;

import model.User;
import repository.RepoManager;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField txtFullName, txtUsername, txtEmail;
    private JPasswordField txtPassword, txtConfirm;

    public RegisterFrame() {
        setTitle("Create Account");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(30, 30, 30));

        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(new Color(38, 100, 56));
        left.setPreferredSize(new Dimension(280, 500));
        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0; gl.gridy = 0; gl.insets = new Insets(8,8,8,8);
        JLabel icon = new JLabel("📋", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        left.add(icon, gl);
        gl.gridy++;
        JLabel t = new JLabel("<html><center>Join the<br>Community</center></html>", SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        left.add(t, gl);
        gl.gridy++;
        JLabel s = new JLabel("<html><center>Register as a student<br>to join events</center></html>", SwingConstants.CENTER);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(new Color(200, 240, 210));
        left.add(s, gl);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(40, 40, 40));
        GridBagConstraints gr = new GridBagConstraints();
        gr.gridx = 0; gr.gridy = 0; gr.fill = GridBagConstraints.HORIZONTAL;
        gr.insets = new Insets(6, 30, 2, 30);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        right.add(title, gr);

        String[] labels = {"Full Name", "Username", "Email", "Password", "Confirm Password"};
        JTextField[] fields = new JTextField[5];
        fields[0] = txtFullName = new JTextField();
        fields[1] = txtUsername = new JTextField();
        fields[2] = txtEmail = new JTextField();
        fields[3] = txtPassword = new JPasswordField();
        fields[4] = txtConfirm = new JPasswordField();

        for (int i = 0; i < labels.length; i++) {
            gr.gridy++;
            gr.insets = new Insets(8, 30, 2, 30);
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(new Color(180,180,180));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            right.add(lbl, gr);
            gr.gridy++;
            gr.insets = new Insets(2, 30, 4, 30);
            styleField(fields[i]);
            right.add(fields[i], gr);
        }

        gr.gridy++;
        gr.insets = new Insets(12, 30, 4, 30);
        JButton btnReg = new JButton("Register");
        styleButton(btnReg, new Color(38, 100, 56));
        right.add(btnReg, gr);

        gr.gridy++;
        gr.insets = new Insets(4, 30, 8, 30);
        JButton btnBack = new JButton("Back to Login");
        styleButton(btnBack, new Color(60, 60, 60));
        right.add(btnBack, gr);

        root.add(left, BorderLayout.WEST);
        JScrollPane scroll = new JScrollPane(right);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);

        btnReg.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void doRegister() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name, Username, and Password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (RepoManager.getInstance().getUserRepo().usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User u = new User();
        u.setFullName(fullName);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole("STUDENT");
        if (RepoManager.getInstance().getUserRepo().register(u)) {
            RepoManager.getInstance().getLogRepo().log(0, "REGISTER", username + " registered as student.");
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(240, 34));
        f.setBackground(new Color(55, 55, 55));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80,80,80),1),
            BorderFactory.createEmptyBorder(4,10,4,10)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleButton(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(240, 38));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
