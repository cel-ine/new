import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;

public class AdminService {
    public static ObservableList<AdminUser> getAllUsers() {
        return DatabaseHandler.displayUsers();
    }

    public static ObservableList<AdminUser> getAllAccounts() {
        return DatabaseHandler.displayAccounts();
    }

    public static boolean deleteUser(int accID) {
         return DatabaseHandler.deleteUser(accID);
    }

    public static boolean updateUser(AdminUser user) {
        return DatabaseHandler.updateUserDetails(user); // Calls DB update method
    }
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    public static int[] getDashboardCounts() {
        String query = "SELECT " +
            "(SELECT COUNT(*) FROM WazeAccounts WHERE role != 'admin') AS total_users, " +
            "(SELECT COUNT(*) FROM WazeRoutes) AS total_routes, " +
            "(SELECT COUNT(*) FROM WazePlannedDrives) AS total_planned_drives";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new int[]{
                    rs.getInt("total_users"),
                    rs.getInt("total_routes"),
                    rs.getInt("total_planned_drives")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0, 0};
    }
    public static boolean addUser(AdminUser newUser) {
        return DatabaseHandler.addUser(newUser);
    }






    //ROUTE MANAGER METHODS FOR TABLE & DATABASE HANDLING
    public static ObservableList<AdminRoutes> getAllRoutes() {
        return DatabaseHandler.displayUserRoutes();
    }
    public static boolean deleteSavedRoute(String routeID) {
        return DatabaseHandler.deleteSavedRoute(routeID);
   }
   public static boolean updateRoute(AdminRoutes route) {
    return DatabaseHandler.updateRoute(route);
    }
    public static boolean addRoutes(AdminRoutes newRoute, ObservableList<String> locationList) {
        return DatabaseHandler.addRoutes(newRoute, locationList);
    }



    //PLANNED DRIVES MANAGER METHODS FOR TABLE & DATABASE HANDLING
    public static ObservableList<AdminPlannedDrives> displayPlannedDrives() {
        return DatabaseHandler.displayPlannedDrives();
    }
    public static boolean addPlannedDrive(AdminPlannedDrives newPlannedDrive) {
        return DatabaseHandler.addPlannedDrive(newPlannedDrive);
    }

    public static boolean deletePlannedDrive(int plannedDriveID) {
        return DatabaseHandler.deletePlannedDrive(plannedDriveID);
    }

    public static boolean updatePlannedDrive(AdminPlannedDrives plannedDrive) {
        return DatabaseHandler.updatePlannedDrive(plannedDrive);
    }

    //
    public static ObservableList<String> getAllUsernames() {
        return DatabaseHandler.loadUsernames();
    }

    public static ObservableList<String> getAllLocations() {
        return DatabaseHandler.loadLocations();
    }

    public static ObservableList<String> getAllStopovers() {
        return DatabaseHandler.loadStopovers();
    }
}

