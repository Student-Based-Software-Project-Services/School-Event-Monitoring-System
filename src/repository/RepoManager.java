package repository;

import database.DBConnection;
import java.sql.Connection;

public class RepoManager {
    private static RepoManager instance;
    private Connection conn;

    private UserRepository userRepo;
    private EventRepository eventRepo;
    private RegistrationRepository registrationRepo;
    private AttendanceRepository attendanceRepo;
    private AnnouncementRepository announcementRepo;
    private ActivityLogRepository logRepo;

    public RepoManager() {
        Connection conn = DBConnection.getConnection();
        this.conn = conn;
        userRepo = new UserRepository(conn);
        eventRepo = new EventRepository(conn);
        registrationRepo = new RegistrationRepository(conn);
        attendanceRepo = new AttendanceRepository(conn);
        announcementRepo = new AnnouncementRepository(conn);
        logRepo = new ActivityLogRepository(conn);
    }

    public static RepoManager getInstance() {
        if (instance == null) {
            instance = new RepoManager();
        }
        return instance;
    }

    public UserRepository getUserRepo() { return userRepo; }
    public EventRepository getEventRepo() { return eventRepo; }
    public RegistrationRepository getRegistrationRepo() { return registrationRepo; }
    public AttendanceRepository getAttendanceRepo() { return attendanceRepo; }
    public AnnouncementRepository getAnnouncementRepo() { return announcementRepo; }
    public ActivityLogRepository getLogRepo() { return logRepo; }
    public Connection getConnection() { return conn; }
}
