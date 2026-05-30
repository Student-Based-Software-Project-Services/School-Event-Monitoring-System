package frames;

import model.User;
import repository.RepoManager;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public LoginFrame() {
        setTitle("School Event Management System");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(30, 30, 30));

        // Left panel - branding
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(new Color(25, 118, 210));
        left.setPreferredSize(new Dimension(320, 500));
        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0; gl.gridy = 0; gl.insets = new Insets(8,8,8,8);

        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        left.add(icon, gl);

        gl.gridy++;
        JLabel appTitle = new JLabel("<html><center>School Event<br>Management<br>System</center></html>", SwingConstants.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setForeground(Color.WHITE);
        left.add(appTitle, gl);

        gl.gridy++;
        JLabel sub = new JLabel("<html><center>Organize. Manage. Monitor.</center></html>", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(200, 220, 255));
        left.add(sub, gl);

        // Right panel - login form
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(40, 40, 40));
        GridBagConstraints gr = new GridBagConstraints();
        gr.gridx = 0; gr.gridy = 0; gr.fill = GridBagConstraints.HORIZONTAL;
        gr.insets = new Insets(8, 30, 8, 30);

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        loginTitle.setForeground(Color.WHITE);
        gr.anchor = GridBagConstraints.WEST;
        right.add(loginTitle, gr);

        gr.gridy++;
        JLabel sub2 = new JLabel("Sign in to your account");
        sub2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub2.setForeground(new Color(160, 160, 160));
        right.add(sub2, gr);

        gr.gridy++;
        gr.insets = new Insets(18, 30, 4, 30);
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(new Color(180, 180, 180));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        right.add(lblUser, gr);

        gr.gridy++;
        gr.insets = new Insets(2, 30, 8, 30);
        txtUsername = new JTextField();
        styleField(txtUsername);
        right.add(txtUsername, gr);

        gr.gridy++;
        gr.insets = new Insets(8, 30, 4, 30);
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(new Color(180, 180, 180));
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        right.add(lblPass, gr);

        gr.gridy++;
        gr.insets = new Insets(2, 30, 20, 30);
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        right.add(txtPassword, gr);

        gr.gridy++;
        gr.insets = new Insets(4, 30, 8, 30);
        btnLogin = new JButton("Login");
        styleButton(btnLogin, new Color(25, 118, 210));
        right.add(btnLogin, gr);

        gr.gridy++;
        gr.insets = new Insets(4, 30, 8, 30);
        btnRegister = new JButton("Create Account");
        styleButton(btnRegister, new Color(60, 60, 60));
        right.add(btnRegister, gr);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = RepoManager.getInstance().getUserRepo().login(username, password);
        if (user != null) {
            RepoManager.getInstance().getLogRepo().log(user.getId(), "LOGIN", user.getFullName() + " logged in.");
            if (user.isAdmin()) {
                new AdminFrame(user).setVisible(true);
            } else {
                new StudentFrame(user).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(260, 38));
        f.setBackground(new Color(55, 55, 55));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleButton(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(260, 40));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
