import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHandler {
    private static DatabaseHandler handler = null;
    private static Connection connection = null;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/WazeApp?useSSL=false&serverTimezone=Asia/Manila";
    private static final String USER = "root";
    private static final String PASSWORD = "ilovecompsci"; // Change if needed

    private DatabaseHandler() {
        connectToDB();
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private static void connectToDB() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL Driver
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                System.out.println("Connected to database!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            connectToDB();
        }
        return connection;
    }

    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static void updateLastLogin(String username) {
        String query = "UPDATE WazeAccounts SET last_login = NOW() WHERE Username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("Last login updated for: " + username);
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //LOAD
    public static ObservableList<String> loadUsernames() {
        ObservableList<String> accountList = FXCollections.observableArrayList();
        String query = "SELECT account_id, username FROM WazeAccounts WHERE role != 'admin'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int accountID = rs.getInt("account_id");
                String username = rs.getString("username");
                accountList.add(accountID + " - " + username);
            }
        } catch (SQLException e) {
            System.err.println("Error loading account data: " + e.getMessage());
        }
        return accountList;
    }

    public static ObservableList<String> loadLocations() {
        ObservableList<String> locationList = FXCollections.observableArrayList();
        String query = "SELECT name FROM locations ORDER BY name ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                locationList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading locations: " + e.getMessage());
        }
        return locationList;
    }

    public static ObservableList<String> loadStopovers() {
        ObservableList<String> stopoverList = FXCollections.observableArrayList();
        String query = "SELECT name FROM StopoverLocations";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stopoverList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading stopovers: " + e.getMessage());
        }
        return stopoverList;
    }



    // 🌮🌮🌮 DASHBOARD DISPLAY
    public static ObservableList<AdminUser> displayUsers() {
        ObservableList<AdminUser> userList = FXCollections.observableArrayList();
        String query = "SELECT Username, Email, last_login FROM WazeAccounts WHERE role != 'admin'";
    
        try (Connection conn = getConnection();  // Properly manage the connection
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {
    
            while (result.next()) {
                String username = result.getString("Username");
                String email = result.getString("Email");
                Timestamp timestamp = result.getTimestamp("last_login");
                String lastLogin = (timestamp != null) ? timestamp.toLocalDateTime().toString() : "Logged in a long time ago";
    
                System.out.println("Fetched User: " + username + " | Last Login: " + lastLogin); // Debugging
                userList.add(new AdminUser(username, email, lastLogin));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }
    
    // 🌮🌮🌮 ACCOUNT MANAGER TABLE DISPLAY
    public static ObservableList<AdminUser> displayAccounts() {
        ObservableList<AdminUser> accountList = FXCollections.observableArrayList();
        String query = "SELECT account_id, email, username, passwords, birthdate, first_name, last_name FROM WazeAccounts WHERE role != 'admin'";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet result = pstmt.executeQuery()) {

            while (result.next()) {
                int id = result.getInt("account_id");
                String email = result.getString("email");  // FIXED CASE
                String username = result.getString("username");  // FIXED CASE
                String password = result.getString("passwords");  // FIXED CASE
                String birthdate = result.getString("birthdate");  // FIXED CASE
                String firstName = result.getString("first_name");  // FIXED CASE
                String lastName = result.getString("last_name");  // FIXED CASE

                accountList.add(new AdminUser(id, email, username, password, birthdate, firstName, lastName));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
            e.printStackTrace();
        }
        return accountList;
    }

    // 🌮🌮🌮 ROUTES MANAGER TABLE DISPLAY
    public static ObservableList<AdminRoutes> displayUserRoutes() {
        ObservableList<AdminRoutes> savedRoutesList = FXCollections.observableArrayList();
        String query = """
                SELECT 
            wr.route_id,
            wr.account_id,
            wr.route_startpoint,
            wr.route_endpoint,
            wat.alt_routes,
            wat.stop_overloc,
            wtt.est_time
            FROM WazeRoutes wr
            LEFT JOIN WazeAltRoutes wat ON wr.route_id = wat.route_id
            LEFT JOIN WazeTravelTime wtt ON wr.route_id = wtt.route_id;
                """; 
                
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {
    
            while (result.next()) {
                String routeId = result.getString("route_id");
                int accountID = result.getInt("account_id"); 
                String routeStart = result.getString("route_startpoint");
                String routeEnd = result.getString("route_endpoint");
                String altRoutes = result.getString("alt_routes");
                String stopOver = result.getString("stop_overloc");
                String estTime = result.getString("est_time"); // Retrieve as String

    
                savedRoutesList.add(new AdminRoutes(routeId, accountID, routeStart, routeEnd, 
                                                    altRoutes, stopOver, estTime));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching routes: " + e.getMessage());
            e.printStackTrace();
        }
        return savedRoutesList;
    }
    // 🌮🌮🌮 PLANNED DRIVES TABLE DISPLAY
    public static ObservableList<AdminPlannedDrives> displayPlannedDrives() {
        ObservableList<AdminPlannedDrives> plannedDrivesList = FXCollections.observableArrayList();
        String query = "SELECT planneddrive_id, account_id, planned_date, planned_time, start_loc, pinned_loc FROM WazePlannedDrives";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet result = pstmt.executeQuery()) {
    
            while (result.next()) {
                int plannedDriveID = result.getInt("planneddrive_id");
                int accountID = result.getInt("account_id");
                LocalDate plannedDate = result.getDate("planned_date").toLocalDate(); 
                LocalTime plannedTime = result.getTime("planned_time").toLocalTime(); 
                String startLoc = result.getString("start_loc");
                String pinnedLoc = result.getString("pinned_loc");
    
                // Make sure AdminPlannedDrives has this constructor!
                plannedDrivesList.add(new AdminPlannedDrives(plannedDriveID, accountID, plannedDate, plannedTime, startLoc, pinnedLoc));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching planned drives: " + e.getMessage());
            e.printStackTrace();
        }
        return plannedDrivesList;
    }
    

    // 🎀🎀🎀 ACCOUNT MANAGER - CRUD OPERATIONS
    public static boolean addUser(AdminUser newUser) {
        String query = "INSERT INTO WazeAccounts (email, username, passwords, birthdate, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newUser.getEmail());
            pstmt.setString(2, newUser.getUsername());
            pstmt.setString(3, newUser.getPassword());
            pstmt.setString(4, newUser.getBirthDate());
            pstmt.setString(5, newUser.getFirstName());
            pstmt.setString(6, newUser.getLastName());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(int accID) {
        String query = "DELETE FROM wazeaccounts WHERE account_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
                return true;  
            } else {
                System.out.println("No user found with ID: " + accID);
                return false; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    public static boolean updateUserDetails(AdminUser user) {
        String updateUser = "UPDATE wazeaccounts SET email = ?, username = ?, passwords = ?, first_name = ?, last_name = ? WHERE account_id = ?";
        Connection conn = null; 
    
        try {
            conn = DatabaseHandler.getConnection();
            conn.setAutoCommit(false); // Begin transaction
    
            try (PreparedStatement stmt = conn.prepareStatement(updateUser)) {
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getUsername());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getFirstName());
                stmt.setString(5, user.getLastName());
                stmt.setInt(6, user.getAccID());
                stmt.executeUpdate();
            }
    
            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) { // Ensure conn is not null before rollback
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close(); // Close connection properly
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    



    // 🎀🎀🎀 ROUTES MANAGER- CRUD OPERATIONS
    public static boolean addRoutes(AdminRoutes newRoute, List<String> locationList) {
        Connection conn = null;
        PreparedStatement pstmtRoutes = null;
        PreparedStatement pstmtAltRoutes = null;
        PreparedStatement pstmtTravelTime = null;
    
        String insertRouteQuery = "INSERT INTO WazeRoutes (route_id, account_id, route_startpoint, route_endpoint) VALUES (?, ?, ?, ?)";
        String insertAltRouteQuery = "INSERT INTO WazeAltRoutes (alt_route_id, route_id, alt_routes, stop_overloc) VALUES (?, ?, ?, ?)";
        String insertTravelTimeQuery = "INSERT INTO WazeTravelTime (traveltime_id, route_id, est_time) VALUES (?, ?, ?)";
    
        try {
            conn = DatabaseHandler.getConnection();
            conn.setAutoCommit(false); // Start transaction
    
            // Insert into WazeRoutes
            pstmtRoutes = conn.prepareStatement(insertRouteQuery);
            pstmtRoutes.setString(1, newRoute.getRouteID()); 
            pstmtRoutes.setInt(2, newRoute.getAccountID());
            pstmtRoutes.setString(3, newRoute.getRoute_startpoint());
            pstmtRoutes.setString(4, newRoute.getRoute_endpoint());
            pstmtRoutes.executeUpdate();
    
            String altRouteID = RouteIDGenerator.generateAltRouteID();
            String alternativeRoute = RouteIDGenerator.generateRandomAlternativeRoute(
                newRoute.getRoute_startpoint(),
                newRoute.getRoute_endpoint(),
                locationList
            );
    
            // Set stopover to "No Stopover" if it's null or empty
            String stopover = (newRoute.getStopOver() == null || newRoute.getStopOver().isEmpty()) 
                            ? "No Stopover" : newRoute.getStopOver();
    
            // Insert into WazeAltRoutes
            pstmtAltRoutes = conn.prepareStatement(insertAltRouteQuery);
            pstmtAltRoutes.setString(1, altRouteID);
            pstmtAltRoutes.setString(2, newRoute.getRouteID());
            pstmtAltRoutes.setString(3, alternativeRoute); // Can be null or randomly generated
            pstmtAltRoutes.setString(4, stopover); // Always "No Stopover" if empty
            pstmtAltRoutes.executeUpdate();
    
            // Insert into WazeTravelTime
            pstmtTravelTime = conn.prepareStatement(insertTravelTimeQuery);
            pstmtTravelTime.setString(1, "T_T-" + String.format("%03d", new Random().nextInt(999))); // Random travel time ID
            pstmtTravelTime.setString(2, newRoute.getRouteID());
            pstmtTravelTime.setString(3, RouteIDGenerator.generateRandomEstTime()); // Generate est time
            pstmtTravelTime.executeUpdate();
    
            conn.commit(); // Commit transaction
            return true;
    
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback changes on failure
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error adding route: " + e.getMessage());
            return false;
    
        } finally {
            try {
                if (pstmtRoutes != null) pstmtRoutes.close();
                if (pstmtAltRoutes != null) pstmtAltRoutes.close();
                if (pstmtTravelTime != null) pstmtTravelTime.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
    }
     
    public static boolean deleteSavedRoute (String routeID) {
        String query = "DELETE FROM WazeRoutes WHERE route_id = ?";
        try (Connection conn = DatabaseHandler.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, routeID);
            int rowsAffected = stmt.executeUpdate();
                
            if (rowsAffected > 0) {
                System.out.println("Saved Route deleted successfully.");
                return true;  
        } else {
                System.out.println("No saved Routes: " + routeID);
                return false; 
        }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    public static boolean updateRoute(AdminRoutes route) {
        if (route == null || route.getRouteID() == null) {
            System.out.println("❌ Error: Invalid route. Cannot update.");
            return false;
        }
    
        boolean success = false;
        
        String updateRoutesQuery = "UPDATE WazeRoutes SET route_startpoint = ?, route_endpoint = ? WHERE route_id = ?";
        String updateAltRoutesQuery = "UPDATE WazeAltRoutes SET stop_overloc = ? WHERE route_id = ?";
        
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmtRoutes = conn.prepareStatement(updateRoutesQuery);
             PreparedStatement pstmtAltRoutes = conn.prepareStatement(updateAltRoutesQuery)) {
            
            conn.setAutoCommit(false);
    
            pstmtRoutes.setString(1, route.getRoute_startpoint());
            pstmtRoutes.setString(2, route.getRoute_endpoint());
            pstmtRoutes.setString(3, route.getRouteID().trim());
    
            pstmtAltRoutes.setString(1, route.getStopOver());
            pstmtAltRoutes.setString(2, route.getRouteID().trim());
    
            int rowsAffectedRoutes = pstmtRoutes.executeUpdate();
            int rowsAffectedAltRoutes = pstmtAltRoutes.executeUpdate();
    
            if (rowsAffectedRoutes > 0 || rowsAffectedAltRoutes > 0) {
                conn.commit();
                success = true;
                System.out.println("Route update successful.");
            } else {
                System.out.println("No update made. Check route ID & values.");
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error updating route: " + e.getMessage());
        }
        
        return success;
    }
    
    
    //🎀🎀🎀 PLANNED DRIVES
    public static boolean addPlannedDrive(AdminPlannedDrives newPlannedDrive) {
        String query = "INSERT INTO WazePlannedDrives (account_id, planned_date, planned_time, start_loc, pinned_loc) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            pstmt.setInt(1, newPlannedDrive.getAccount_id()); 
            pstmt.setDate(2, java.sql.Date.valueOf(newPlannedDrive.getPlannedDate())); 
            pstmt.setTime(3, java.sql.Time.valueOf(newPlannedDrive.getPlannedTime())); 
            pstmt.setString(4, newPlannedDrive.getStartLoc()); 
            pstmt.setString(5, newPlannedDrive.getPinnedLoc()); 
    
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }    

    public static boolean deletePlannedDrive(int plannedDriveID) {
        String query = "DELETE FROM WazePlannedDrives WHERE planneddrive_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, plannedDriveID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePlannedDrive(AdminPlannedDrives plannedDrive) {
        String query = "UPDATE WazePlannedDrives SET pinned_loc = ?, start_loc = ?, planned_date = ?, planned_time  = ? WHERE planneddrive_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, plannedDrive.getPinnedLoc());
            pstmt.setString(2, plannedDrive.getStartLoc());
            pstmt.setObject(3, plannedDrive.getPlannedDate()); // No timezone shifts
            pstmt.setTime(4, java.sql.Time.valueOf(plannedDrive.getPlannedTime())); 
            pstmt.setInt(5, plannedDrive.getPlannedDriveID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //INSERTS NEW USER
    public boolean insertUser(String username, String password, String email, String birthdate, String firstName, String lastName) {
        if (isConnectionClosed()) {
            System.out.println("No database connection. Cannot create user.");
            return false;
        }
        String checkQuery = "SELECT * FROM WazeAccounts WHERE LOWER(Username) = ?";
        String insertQuery = "INSERT INTO WazeAccounts (Username, Passwords, Email, Birthdate, First_Name, Last_Name) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            checkStmt.setString(1, username.toLowerCase());
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                System.out.println("User already exists: " + username);
                return false;
            }
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, birthdate);
            insertStmt.setString(5, firstName);
            insertStmt.setString(6, lastName);
            insertStmt.executeUpdate();

            System.out.println("User created successfully: " + username);
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    //LOGIN CREDENTIALS VALIDATION
    public static boolean validateLogin(String username, String password) {
        Connection conn = getConnection(); // Ensure connection is valid
    
        if (conn == null) {
            System.err.println("No database connection. Cannot validate login.");
            return false;
        } 
        String query = "SELECT * FROM WazeAccounts WHERE Username = ? AND Passwords = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet result = pstmt.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
        }
        return false;
    }

}