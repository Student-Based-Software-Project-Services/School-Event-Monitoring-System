
import frames.LoginFrame;
import repository.RepoManager;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        
        // Try FlatLaf Dark; fallback to Nimbus if not present
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Flatlaf Dark".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        // Initialize DB singleton
        RepoManager.getInstance();

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
