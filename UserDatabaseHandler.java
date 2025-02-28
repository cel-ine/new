import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDatabaseHandler {

    private static UserDatabaseHandler handler = null;
    public static Connection connection = null;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/WazeApp?useSSL=false&serverTimezone=Asia/Manila";
    private static final String USER = "root";
    private static final String PASSWORD = "ilovecompsci";

    private UserDatabaseHandler() {
        connectToDB();
    }

    public static UserDatabaseHandler getInstance() {
        if (handler == null) {
            handler = new UserDatabaseHandler();
        }
        if (connection == null) {
            handler.connectToDB(); // Ensure connection is established
        }
        return handler;
    }

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            connectToDB(); // Reconnect if closed
                    }
                    return connection;
                }
            
    private static void connectToDB() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Already connected to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Error checking connection status: " + e.getMessage());
            return true;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //💗💗💗 INSERT USER (NEW ACCOUNT)
    public boolean insertUser(String username, String password, String email, String birthdate, String firstName,
            String lastName) {
        getInstance(); // Ensure connection is initialized
        if (connection == null) {
            System.out.println("⚠ Failed to insert user: No database connection.");
            return false;
        }

        String checkQuery = "SELECT * FROM WazeAccounts WHERE LOWER(username) = ?";
        String insertQuery = "INSERT INTO WazeAccounts (username, passwords, email, birthdate, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            checkStmt.setString(1, username.toLowerCase());
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("⚠ User already exists: " + username);
                return false;
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, birthdate);
            insertStmt.setString(5, firstName);
            insertStmt.setString(6, lastName);
            insertStmt.executeUpdate();

            System.out.println("✅ User created successfully: " + username);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //💗💗💗 NEW ROUTE
    public boolean insertRoute(String username, String start, String end) {
        getInstance(); // Ensure connection is initialized
        if (connection == null) {
            System.out.println("⚠ Failed to insert route: No database connection.");
            return false;
        }

        String routeID = GenerateID.generateID("WazeRoutes", "ROUTE");
        String insertQuery = "INSERT INTO WazeRoutes (routeID, username, route_startpoint, route_endpoint) VALUES (?, ?, ?, ?)";

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, routeID);
            insertStmt.setString(2, username);
            insertStmt.setString(3, start);
            insertStmt.setString(4, end);
            insertStmt.executeUpdate();

            System.out.println("✅ Route created successfully: " + routeID);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error adding route: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 💗💗💗 NEW PLANNED DRIVE
    public boolean insertPlannedDrive(LocalDate date, Time inputTime, String startLoc, String endLoc) {
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance(); // Ensure connection is initialized
        Connection connection = handler.getConnection(); // Get the active connection

        if (connection == null || handler.isConnectionClosed()) {
            System.out.println("⚠ Failed to insert planned drive: No database connection.");
            return false;
        }

        int plannedDriveID = 1;
        int accountId = 1; // Assume account ID is 1 for now

        String insertQuery = "INSERT INTO WazePlannedDrives ( planned_date, planned_time, start_loc, pinned_loc) VALUES ( ?, ?, ?, ?)";

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            insertStmt.setDate(1, java.sql.Date.valueOf(date.plusDays(1))); // java.sql.Date
            insertStmt.setTime(2, inputTime);
            insertStmt.setString(3, startLoc);
            insertStmt.setString(4, endLoc);
            insertStmt.executeUpdate();

            getPlannedDrives(accountId); // Refresh the list of planned drives
            System.out.println("✅ Planned drive created successfully: " + plannedDriveID);
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error adding planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //💗💗💗 ROUTES TABLE DISPLAY
    public ObservableList<UserRouteDetails> getUserRouteDetails(int accountId) {
        ObservableList<UserRouteDetails> routes = FXCollections.observableArrayList();
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance();
        Connection connection = UserDatabaseHandler.getConnection();

        if (connection == null || UserDatabaseHandler.isConnectionClosed()) {
            System.out.println("⚠ Failed to fetch routes: No database connection.");
            return routes; // Return empty list if no connection
        }

        String query = """
            SELECT 

        wr.route_startpoint,
        wr.route_endpoint,
        wat.alt_routes,
        wat.stop_overloc,
        wtt.est_time
        FROM WazeRoutes wr
        LEFT JOIN WazeAltRoutes wat ON wr.route_id = wat.route_id
        LEFT JOIN WazeTravelTime wtt ON wr.route_id = wtt.route_id;
            """; 

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                routes.add(new UserRouteDetails(
                        // rs.getInt("accountId"),
                        // rs.getString("routeId"),
                        rs.getString("route_startpoint"),
                        rs.getString("route_endpoint"),
                        rs.getString("alt_routes"),
                        rs.getString("stop_overloc"),
                        rs.getTime("est_time")));
            }
            System.out.println("✅ Fetched " + routes.size() + " routes for account ID: " + accountId);
        } catch (SQLException e) {
            System.err.println("❌ Error fetching routes: " + e.getMessage());
            e.printStackTrace();
        }
        return routes;
    }

    //💗💗💗 PLANNED DRIVES TABLE DISPLAY
    public ObservableList<UserPlannedDrives> getPlannedDrives(int accountId) {
        ObservableList<UserPlannedDrives> drives = FXCollections.observableArrayList();
        String query = "SELECT * FROM WazePlannedDrives  ORDER BY planneddrive_id DESC"; // Fixed query
                                                                                         // placeholder

        UserDatabaseHandler.getInstance();
        try (Connection conn = UserDatabaseHandler.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // stmt.setInt(1, 1); // Properly passing accountId into the query
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                drives.add(new UserPlannedDrives(
                        rs.getInt("planneddrive_id"),
                        rs.getInt("account_id"),
                        rs.getDate("planned_date"),
                        rs.getTime("planned_time"),
                        rs.getString("start_loc"),
                        rs.getString("pinned_loc")));
            }

            System.out.println("✅ Fetched " + drives.size() + " planned drives for account ID: " + accountId);

        } catch (SQLException e) {
            System.err.println("❌ Error fetching planned drives: " + e.getMessage());
            e.printStackTrace();
        }

        return drives;
    }

    public static ObservableList<String> getAllLocations() {
        ObservableList<String> locations = FXCollections.observableArrayList();
        String query = "SELECT name FROM locations";
        UserDatabaseHandler.getInstance();
        try (Connection connection = UserDatabaseHandler.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching locations: " + e.getMessage());
        }
        return locations;
    }

    public static ObservableList<String> getStopoverLocations() {
        ObservableList<String> stopovers = FXCollections.observableArrayList();
        String query = "SELECT name FROM stopoverlocations";

        try (
                Connection connection = UserDatabaseHandler.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stopovers.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching stopover locations: " + e.getMessage());
        }

        return stopovers;
    }

    //💗💗💗 DELETE FUNCTIONS
    public static boolean deleteUserRoute(String routeID) {
        String query = "DELETE FROM WazeRoutes WHERE route_id = ?";

        try (Connection connection = getInstance().getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, routeID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Route deleted successfully: " + routeID);
                return true;
            } else {
                System.out.println("⚠ No route found with ID: " + routeID);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting user route: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUserPlannedDrive(int plannedDriveId) {
        String query = "DELETE FROM WazePlannedDrives WHERE planneddrive_id = ?";

        try (Connection connection = getInstance().getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, plannedDriveId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                UserDatabaseHandler.getInstance().getPlannedDrives(1);
                System.out.println("✅ Planned drive deleted successfully: " + plannedDriveId);
                return true;
            } else {
                System.out.println("⚠ No planned drive found with ID: " + plannedDriveId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<String> getLocations() {
        ObservableList<String> locations = FXCollections.observableArrayList();
        String query = "SELECT name FROM locations";
        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                locations.add(rs.getString("name"));
            }
            System.out.println("✅ Fetched " + locations.size() + " locations.");
        } catch (SQLException e) {
            System.err.println("❌ Error fetching locations: " + e.getMessage());
        }
        return locations;
    }
    ///💗💗💗 ADD FUNCTIONS
    public static boolean addUserPlannedDrive(UserPlannedDrives newPlannedDrive) {

        String query = "INSERT INTO WazePlannedDrives ( planned_date, planned_time,start_loc, pinned_loc) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // pstmt.setString(1, plannedDriveID);
            // pstmt.setInt(2, newPlannedDrive.getAccountId());
            pstmt.setString(2, newPlannedDrive.getStartLoc());
            pstmt.setString(3, newPlannedDrive.getPinnedLoc());
            pstmt.setDate(4, newPlannedDrive.getPlannedDate());
            pstmt.setTime(5, newPlannedDrive.getPlannedTime());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error adding planned drive: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    //💅💅💅 
    public static boolean deleteUserPlannedDrive(String driveID) {
        String query = "DELETE FROM WazePlannedDrives WHERE planneddrive_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, driveID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting planned drive: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 💅💅💅 UPDATE FUNCTIONS
    public static ObservableList<UserPlannedDrives> displayPlannedDrives() {
        ObservableList<UserPlannedDrives> plannedDriveList = FXCollections.observableArrayList();
        String query = "SELECT planneddrive_id, account_id, planned_date, planned_time, start_loc, pinned_loc FROM WazePlannedDrives";

        try (Connection conn = getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet result = pstmt.executeQuery()) {

            while (result.next()) {
                int plannedDriveId = result.getInt("planneddrive_id");
                int accountId = result.getInt("account_id");
                Date plannedDate = result.getDate("planned_date");
                Time plannedTime = result.getTime("planned_time");
                String startLoc = result.getString("start_loc");
                String pinnedLoc = result.getString("pinned_loc");

                plannedDriveList.add(new UserPlannedDrives(plannedDriveId, accountId, plannedDate, plannedTime,
                        startLoc, pinnedLoc));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching planned drives: " + e.getMessage());
            e.printStackTrace();
        }
        return plannedDriveList;
    }

    public static boolean updateUserRoute(UserRouteDetails route) {
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance();
        Connection connection = handler.getConnection(); // Ensure active connection

        if (connection == null || handler.isConnectionClosed()) {
            System.out.println("⚠ Failed to update route: No database connection.");
            return false;
        }

        String query = "UPDATE WazeRoutes SET route_startpoint = ?, route_endpoint = ?, alt_routes = ?, stop_overloc = ?, est_time = ? WHERE route_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, route.getStartPoint());
            pstmt.setString(2, route.getEndPoint());
            pstmt.setString(3, route.getAlternativeRoutes());
            pstmt.setString(4, route.getStopOverLocation());
            pstmt.setTime(5, route.getEstimatedTime());
            pstmt.setString(6, route.getRouteId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Route updated successfully.");
                return true;
            } else {
                System.out.println("⚠ No route found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating route: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateUserPlannedDrive(UserPlannedDrives plannedDrive) {
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance();
        Connection connection = handler.getConnection(); // Ensure active connection

        if (connection == null || handler.isConnectionClosed()) {
            System.out.println("⚠ Failed to update planned drive: No database connection.");
            return false;
        }

        String query = "UPDATE WazePlannedDrives SET planned_date = ?, planned_time = ?, start_loc = ?, pinned_loc = ? WHERE planneddrive_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, plannedDrive.getPlannedDate());
            pstmt.setString(2, plannedDrive.getStartLoc());
            pstmt.setString(3, plannedDrive.getPinnedLoc());
            pstmt.setTime(4, plannedDrive.getPlannedTime());
            pstmt.setInt(5, plannedDrive.getPlannedDriveID());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Planned drive updated successfully.");
                return true;
            } else {
                System.out.println("⚠ No planned drive found with the given ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating planned drive: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Add Routes
    // +
    public static boolean addRoutes(UserRouteDetails newRoute, List<String> locationList) {
        if (locationList == null || locationList.isEmpty()) {
            System.err.println("❌ Error: locationList is null or empty.");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmtRoutes = null;
        PreparedStatement pstmtAltRoutes = null;
        PreparedStatement pstmtTravelTime = null;
        PreparedStatement pstmtCheckAccount = null;

        String routeID = GenerateID.generateID("WazeRoutes", "ROUTE");
        String insertRouteQuery = "INSERT INTO WazeRoutes (route_id, account_id, route_startpoint, route_endpoint) VALUES (?, ?, ?, ?)";
        String insertAltRouteQuery = "INSERT INTO WazeAltRoutes (alt_route_id, route_id, alt_routes, stop_overloc) VALUES (?, ?, ?, ?)";
        String insertTravelTimeQuery = "INSERT INTO WazeTravelTime (traveltime_id, route_id, est_time) VALUES (?, ?, ?)";
        String checkAccountQuery = "SELECT COUNT(*) FROM WazeAccounts WHERE account_id = ?";

        try {
            conn = UserDatabaseHandler.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connection is not available or already closed.");
            }

            // Check if account_id exists
            pstmtCheckAccount = conn.prepareStatement(checkAccountQuery);
            pstmtCheckAccount.setInt(1, 1); // +
            ResultSet rs = pstmtCheckAccount.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.err.println("❌ Error: Account ID not found in WazeAccounts table.");
                return false;
            }

            conn.setAutoCommit(false); // Start transaction

            // Insert into WazeRoutes
            pstmtRoutes = conn.prepareStatement(insertRouteQuery);
            pstmtRoutes.setString(1, routeID);
            pstmtRoutes.setInt(2, 1);
            pstmtRoutes.setString(3, newRoute.getStartPoint());
            pstmtRoutes.setString(4, newRoute.getEndPoint());
            pstmtRoutes.executeUpdate();

            String altRouteID = GenerateID.generateAltRouteID();
            String alternativeRoute = GenerateID.generateRandomAlternativeRoute(
                    newRoute.getStartPoint(),
                    newRoute.getEndPoint(),
                    locationList);

            // Set stopover to "No Stopover" if it's null or empty
            String stopover = (newRoute.getStopOverLocation() == null || newRoute.getStopOverLocation().isEmpty())
                    ? "No Stopover"
                    : newRoute.getStopOverLocation();

            // Insert into WazeAltRoutes
            pstmtAltRoutes = conn.prepareStatement(insertAltRouteQuery);
            pstmtAltRoutes.setString(1, altRouteID);
            pstmtAltRoutes.setString(2, routeID);
            pstmtAltRoutes.setString(3, alternativeRoute);
            pstmtAltRoutes.setString(4, stopover);
            pstmtAltRoutes.executeUpdate();

            // Insert into WazeTravelTime
            pstmtTravelTime = conn.prepareStatement(insertTravelTimeQuery);
            pstmtTravelTime.setString(1, GenerateID.generateID("WazeTravelTime", "T_T-"));
            pstmtTravelTime.setString(2, routeID);
            pstmtTravelTime.setString(3, GenerateID.generateRandomEstTime());
            pstmtTravelTime.executeUpdate();

            conn.commit(); // Commit transaction
            UserDatabaseHandler.getInstance().getUserRouteDetails(1); // Refresh the list of user routes
            System.out.println("✅ Route added successfully.");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback changes on failure
                    System.err.println("⚠️ Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println("❌ Error adding route: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (pstmtCheckAccount != null)
                    pstmtCheckAccount.close();
                if (pstmtRoutes != null)
                    pstmtRoutes.close();
                if (pstmtAltRoutes != null)
                    pstmtAltRoutes.close();
                if (pstmtTravelTime != null)
                    pstmtTravelTime.close();
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
    }

    // Updates last login time for a user
    public static void updateLastLogin(String username) {
        // ❌ ERROR: Query string is split incorrectly, causing a syntax issue
        // FIX: Ensure the query is written as a single continuous string
        String query = "UPDATE WazeAccounts SET last_login = NOW() WHERE Username = ?";

        try (PreparedStatement stmt = UserDatabaseHandler.getInstance().getConnection().prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("Last login updated for: " + username);

        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String validateLogin(String username, String password) {
        String queryAdmin = "SELECT * FROM Admins WHERE username = ? AND password = ?";
        String queryUser = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = UserDatabaseHandler.getInstance().getConnection();
                PreparedStatement stmtAdmin = conn.prepareStatement(queryAdmin);
                PreparedStatement stmtUser = conn.prepareStatement(queryUser)) {

            stmtAdmin.setString(1, username);
            stmtAdmin.setString(2, password);
            ResultSet rsAdmin = stmtAdmin.executeQuery();
            if (rsAdmin.next()) {
                return "admin";
            }

            stmtUser.setString(1, username);
            stmtUser.setString(2, password);
            ResultSet rsUser = stmtUser.executeQuery();
            if (rsUser.next()) {
                return "user";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "invalid";
    }
    
    public static int getUserId(String username) {
        String query = "SELECT account_id FROM WazeAccounts WHERE username = ?";
        try (Connection conn = UserDatabaseHandler.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("account_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //FOR USER ACCOUNT'S DELETE AND UPDATE ❗❗❗
    public static boolean updateUserDetails(UserAccount user) {
        String updateQuery = "UPDATE wazeaccounts SET email = ?, username = ?, passwords = ?, first_name = ?, last_name = ?, birthdate = ? WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
    
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            
            // Convert LocalDate to SQL Date
            LocalDate birthdate = user.getBirthdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            stmt.setDate(6, java.sql.Date.valueOf(birthdate));
    
            stmt.setInt(7, user.getAccID());
    
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public static boolean deleteUser(int userId) {
        getInstance(); // Ensure connection is initialized
        if (connection == null) {
            System.out.println("⚠ Failed to delete user: No database connection.");
            return false;
        }
    
        String deleteQuery = "DELETE FROM WazeAccounts WHERE account_id = ?";
    
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, userId);
    
            int affectedRows = deleteStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ User deleted successfully: " + userId);
                return true;
            } else {
                System.out.println("⚠ No user found with ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
