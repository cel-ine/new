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
    public boolean insertPlannedDrive(int accountId, LocalDate date, Time inputTime, String startLoc, String endLoc) {
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance(); // Ensure connection is initialized
        Connection connection = handler.getConnection(); // Get the active connection
    
        if (connection == null || handler.isConnectionClosed()) {
            System.out.println("⚠ Failed to insert planned drive: No database connection.");
            return false;
        }
    
        String insertQuery = "INSERT INTO WazePlannedDrives (account_id, planned_date, planned_time, start_loc, pinned_loc) VALUES (?, ?, ?, ?, ?)";
    
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, accountId); // ✅ Assign the correct account ID
            insertStmt.setDate(2, java.sql.Date.valueOf(date.plusDays(1))); // ✅ planned_date
            insertStmt.setTime(3, inputTime); // ✅ planned_time
            insertStmt.setString(4, startLoc); // ✅ start_loc
            insertStmt.setString(5, endLoc); // ✅ pinned_loc
    
            insertStmt.executeUpdate();
    
            getPlannedDrives(accountId); // ✅ Refresh the list of planned drives for this user
            System.out.println("✅ Planned drive created successfully for Account ID: " + accountId);
            return true;
    
        } catch (SQLException e) {
            System.err.println("❌ Error adding planned drive: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    //💗💗💗 ROUTES TABLE DISPLAY
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
            wr.route_id,
            wr.account_id,
            wr.route_startpoint,
            wr.route_endpoint,
            wat.alt_routes,
            wat.stop_overloc,
            wtt.est_time
        FROM WazeRoutes wr
        LEFT JOIN WazeAltRoutes wat ON wr.route_id = wat.route_id
        LEFT JOIN WazeTravelTime wtt ON wr.route_id = wtt.route_id
        WHERE wr.account_id = ?;
    """; 

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, accountId); //passing the account id sa query 

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            routes.add(new UserRouteDetails(
                rs.getInt("account_id"),
                rs.getString("route_id"),
                rs.getString("route_startpoint"),
                rs.getString("route_endpoint"),
                rs.getString("alt_routes"),
                rs.getString("stop_overloc"),
                rs.getString("est_time")
            ));
        }
        System.out.println("Fetched " + routes.size() + " routes for account ID: " + accountId);
    } catch (SQLException e) {
        System.err.println("Error displaying user's saved routes: " + e.getMessage());
        e.printStackTrace();
    }
    return routes;
}

    //💗💗💗 PLANNED DRIVES TABLE DISPLAY
    public ObservableList<UserPlannedDrives> getPlannedDrives(int accountId) {
        ObservableList<UserPlannedDrives> drives = FXCollections.observableArrayList();
        String query = "SELECT * FROM WazePlannedDrives WHERE account_id = ? ORDER BY planneddrive_id DESC"; // Fixed query
                                                                                         // placeholder

        UserDatabaseHandler.getInstance();
        try (Connection conn = UserDatabaseHandler.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, accountId); //passing the account id sa query 

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

            System.out.println("Fetched " + drives.size() + " planned drives for account ID: " + accountId);

        } catch (SQLException e) {
            System.err.println("Error fetching planned drives: " + e.getMessage());
            e.printStackTrace();
        }

        return drives;
    }

    public static ObservableList<String> getAllLocations() { //FOR COMBOBOX CHOICES 
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

    public static ObservableList<String> getStopoverLocations() { //FOR COMBOBOX CHOICES
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
        if (routeID == null || routeID.trim().isEmpty()) {
            System.err.println("❌ Cannot delete: Route ID is null or empty.");
            return false;
        }
    
        routeID = routeID.trim(); // Remove leading/trailing spaces
        System.out.println("🛠 Attempting to delete route with ID: " + routeID);
    
        String deleteAltRoutesQuery = "DELETE FROM WazeAltRoutes WHERE route_id = ?";
        String deleteTravelTimeQuery = "DELETE FROM WazeTravelTime WHERE route_id = ?";
        String deleteRouteQuery = "DELETE FROM WazeRoutes WHERE route_id = ?";
    
        try (Connection connection = getInstance().getConnection()) {
            if (connection == null || connection.isClosed()) {
                System.err.println("❌ Database connection is unavailable.");
                return false;
            }
    
            connection.setAutoCommit(false); // Start transaction
    
            // Delete from dependent tables first
            try (PreparedStatement pstmtAltRoutes = connection.prepareStatement(deleteAltRoutesQuery);
                 PreparedStatement pstmtTravelTime = connection.prepareStatement(deleteTravelTimeQuery);
                 PreparedStatement pstmtRoutes = connection.prepareStatement(deleteRouteQuery)) {
    
                pstmtAltRoutes.setString(1, routeID);
                pstmtAltRoutes.executeUpdate();
    
                pstmtTravelTime.setString(1, routeID);
                pstmtTravelTime.executeUpdate();
    
                pstmtRoutes.setString(1, routeID);
                int affectedRows = pstmtRoutes.executeUpdate();
    
                if (affectedRows > 0) {
                    connection.commit(); // Commit transaction
                    System.out.println("✅ Route deleted successfully: " + routeID);
                    return true;
                } else {
                    System.out.println("⚠ No route found with ID: " + routeID);
                    connection.rollback(); // Rollback if nothing was deleted
                    return false;
                }
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


    ///💗💗💗 PLANNED DRIVES
    public static boolean addUserPlannedDrive(int accountId, UserPlannedDrives newPlannedDrive, String plannedDriveID) {
    
            String query = "INSERT INTO WazePlannedDrives (account_id, planned_date, planned_time,start_loc, pinned_loc) VALUES (?, ?, ?, ?, ?)";
    
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, plannedDriveID);
            pstmt.setInt(2, newPlannedDrive.getAccountId());
            pstmt.setString(3, newPlannedDrive.getStartLoc());
            pstmt.setString(4, newPlannedDrive.getPinnedLoc());
            pstmt.setDate(5, newPlannedDrive.getPlannedDate());
            pstmt.setTime(6, newPlannedDrive.getPlannedTime());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error adding planned drive: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }






    //💗💗💗 ADD ROUTE 
    public static boolean addRoutes(int accountId, UserRouteDetails newRoute, List<String> locationList) {
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

        try {
            conn = UserDatabaseHandler.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connection is not available or already closed.");
            }

            conn.setAutoCommit(false); // Start transaction

            // Insert into WazeRoutes
            pstmtRoutes = conn.prepareStatement(insertRouteQuery);
            pstmtRoutes.setString(1, newRoute.getRouteId()); 
            pstmtRoutes.setInt(2, accountId);  // ✅ Assign the route to the logged-in user
            pstmtRoutes.setString(3, newRoute.getStartPoint());
            pstmtRoutes.setString(4, newRoute.getEndPoint());
            pstmtRoutes.executeUpdate();

            String altRouteID = RouteIDGenerator.generateAltRouteID();
            String alternativeRoute = RouteIDGenerator.generateRandomAlternativeRoute(
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
            pstmtAltRoutes.setString(2, newRoute.getRouteId());
            pstmtAltRoutes.setString(3, alternativeRoute);
            pstmtAltRoutes.setString(4, stopover);
            pstmtAltRoutes.executeUpdate();

            // Insert into WazeTravelTime
            pstmtTravelTime = conn.prepareStatement(insertTravelTimeQuery);
            pstmtTravelTime.setString(1, "T_T-" + String.format("%03d", new Random().nextInt(999))); // Random travel time ID
            pstmtTravelTime.setString(2, newRoute.getRouteId());
            pstmtTravelTime.setString(3, RouteIDGenerator.generateRandomEstTime());
            pstmtTravelTime.executeUpdate();

            conn.commit(); // Commit transaction
            System.out.println("✅ Route added successfully.");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback changes on failure
                    System.err.println("⚠️ SDF rolled back.");
                } catch (SQLException rollbackEx) {
                    System.err.println("RRF: " + rollbackEx.getMessage());
                }
            }
            System.err.println("❌ Error adding route: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (pstmtRoutes != null) pstmtRoutes.close();
                if (pstmtAltRoutes != null) pstmtAltRoutes.close();
                if (pstmtTravelTime != null) pstmtTravelTime.close();
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources: " + closeEx.getMessage());
            }
        }
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
        if (route == null || route.getRouteId() == null) {
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
    
            pstmtRoutes.setString(1, route.getStartPoint());
            pstmtRoutes.setString(2, route.getEndPoint());
            pstmtRoutes.setString(3, route.getRouteId().trim());
    
            pstmtAltRoutes.setString(1, route.getStopOverLocation());
            pstmtAltRoutes.setString(2, route.getRouteId().trim());
    
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

    // UPDATE - PLANNED DRIVES
    public static boolean updateUserPlannedDrive(UserPlannedDrives plannedDrive) {
        UserDatabaseHandler handler = UserDatabaseHandler.getInstance();
        Connection connection = handler.getConnection(); // Ensure active connection
    
        if (connection == null || handler.isConnectionClosed()) {
            System.out.println("⚠ Failed to update planned drive: No database connection.");
            return false;
        }
    
        String query = "UPDATE WazePlannedDrives SET start_loc = ?, pinned_loc = ?, planned_date = ?, planned_time = ? WHERE planneddrive_id = ?";
    
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, plannedDrive.getStartLoc());
            pstmt.setString(2, plannedDrive.getPinnedLoc());
            pstmt.setObject(3, plannedDrive.getPlannedDate()); // No timezone shifts
            pstmt.setTime(4, plannedDrive.getPlannedTime()); 
            pstmt.setInt(5, plannedDrive.getPlannedDriveID());
            
            // **Debug print before executing update**
            System.out.println("🔄 Executing UPDATE Query:");
            System.out.println("SQL: " + query);
            System.out.println("Start Location: " + plannedDrive.getStartLoc());
            System.out.println("End Location: " + plannedDrive.getPinnedLoc());
            System.out.println("Date: " + plannedDrive.getPlannedDate());
            System.out.println("Time: " + plannedDrive.getPlannedTime());
            System.out.println("Drive ID: " + plannedDrive.getPlannedDriveID());
    
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Planned drive details updated successfully.");
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

    public static boolean validateLogin(String username, String password, int AccountUserID) {
            Connection conn = DatabaseHandler.getInstance().getConnection(); // Ensure connection is valid
         
            if (conn == null) {
                System.err.println("No database connection. Cannot validate login.");
                return false;
            }
            String query = "SELECT * FROM WazeAccounts WHERE Username = ? AND Passwords = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet result = pstmt.executeQuery()) {
                    if (result.next()) {
                        AccountUserID = result.getInt("account_id");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
        }
        return false;
    }
    
    public static int getUserId(String username) {
        int accountId = 0;
        String query = "SELECT account_id FROM WazeAccounts WHERE username = ?";
    
        try (Connection conn = UserDatabaseHandler.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                accountId = rs.getInt("account_id");
            }
    
            System.out.println("🔍 [DEBUG] Fetched account ID from DB: " + accountId); // Debugging log
    
        } catch (SQLException e) {
            System.err.println("❌ Error fetching account ID: " + e.getMessage());
        }
    
        return accountId;
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
