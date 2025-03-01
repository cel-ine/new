import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.ObservableList;

public class UserService {
    private static UserService instance;
    private int currentUserId = 0;  // Default to 0 (no user logged in)
    private String currentUserRole = null;
    
    // Private constructor for Singleton
    private UserService() {}

    // Thread-safe Singleton
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void setCurrentUser(int userId, String role) {
        this.currentUserId = userId;
        this.currentUserRole = role;
        System.out.println("✅ [UserService] User set: ID=" + userId + ", Role=" + role);
    }
    
    public int getCurrentUserId() {
        System.out.println("🔍 [UserService] Fetching Account ID: " + currentUserId);
        return currentUserId;
    }
    

    // ✅ Get current user role
    public String getCurrentUserRole() {
        return currentUserRole;
    }

    // ✅ Check if user is logged in
    public boolean isLoggedIn() {
        return currentUserId > 0;
    }

    // ✅ Logout user
    public void logout() {
        currentUserId = 0;  // Reset to 0 instead of null
        currentUserRole = null;
        System.out.println("🚪 [UserService] User logged out.");
    }


    // Route Management
    public static ObservableList<UserRouteDetails> getUserRoutes(int accountId) {
        return UserDatabaseHandler.getInstance().getUserRouteDetails(accountId);
    }
    public static boolean addUserRoute(int accountId, UserRouteDetails newRoute, List<String> locationList) {
        UserDatabaseHandler.getInstance();
        return UserDatabaseHandler.addRoutes(accountId,newRoute, locationList);
    }
    public static boolean deleteUserRoute(String routeID) {
        return UserDatabaseHandler.getInstance().deleteUserRoute(routeID);
    }
    public static boolean updateUserRoute(UserRouteDetails route) {
        return UserDatabaseHandler.getInstance().updateUserRoute(route);
    }


    // Planned Drives Management
    public static ObservableList<UserPlannedDrives> getUserPlannedDrives(int accountId) {
        return UserDatabaseHandler.getInstance().getPlannedDrives(accountId);
    }
    public static boolean addUserPlannedDrive(int accountId, UserPlannedDrives newPlannedDrive, String plannedDriveID) {
        UserDatabaseHandler.getInstance();
        return UserDatabaseHandler.addUserPlannedDrive(accountId ,newPlannedDrive, plannedDriveID);
    }
    public static boolean deleteUserPlannedDrive(int driveID) {
        UserDatabaseHandler.getInstance();
        return UserDatabaseHandler.deleteUserPlannedDrive(driveID);
    }

    public static boolean updateUserPlannedDrive(UserPlannedDrives plannedDrive) {
        UserDatabaseHandler.getInstance();
        return UserDatabaseHandler.updateUserPlannedDrive(plannedDrive);
    }
    
    
    // ID Generation
    public static String generateRouteID() {
        return GenerateID.generateID("WazeRoutes", "ROUTE");
    }
    
    public static String generatePlannedDriveID() {
        return GenerateID.generatePlannedDriveID();
    }
    
    public static String generateAltRouteID() {
        return GenerateID.generateAltRouteID();
    }
    
    public static String generateTravelTimeID() {
        return GenerateID.generateID("WazeTravelTime", "T_T");
    }
    
    // Location Management
    public static ObservableList<String> getAllLocations() {
        return UserDatabaseHandler.getInstance().getAllLocations();
    }
    
    public static ObservableList<String> getStopoverLocations() {
        return UserDatabaseHandler.getInstance().getStopoverLocations();
    }
    
    // User Account Management
    public static boolean deleteUser(int accID) {
        return UserDatabaseHandler.deleteUser(accID);
    }
    
    public static boolean updateUser(UserAccount user) {
        return UserDatabaseHandler.updateUserDetails(user);
    }

    // Profile Picture Management
    public static boolean saveProfilePicture(String username, String imagePath) {
        String query = "UPDATE WazeAccounts SET profile_picture = ? WHERE username = ? OR role = 'admin'";
        try (Connection conn = UserDatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, imagePath);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Profile picture updated successfully.");
                return true;
            } else {
                System.out.println("❌ No matching user found for username: " + username);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ SQL Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //THIS IS TO FETCH USERS DETAILS
    public static AdminUser getUserByUsername(String username) {
        String query = "SELECT * FROM WazeAccounts WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new AdminUser(
                    rs.getInt("account_id"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("passwords"),
                    rs.getString("birthdate"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static String loadProfilePicture(String username) {
        String query = "SELECT profile_picture FROM WazeAccounts WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_picture");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}