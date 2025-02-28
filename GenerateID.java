import java.sql.*;
import java.util.List;
import java.util.Random;

public class GenerateID {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/FakeWazeAccount?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Example7#24";

    public static String generateID(String table, String prefix) {
        String id = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Count existing records in the table
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
            rs.next();
            int count = rs.getInt(1) + 1; // Increment count

            // Generate a random 3-digit number for uniqueness
            Random random = new Random();
            int randomNum = random.nextInt(900) + 100; // Random number between 100-999

            id = prefix + count + randomNum; // Format: PREFIX + COUNT + RANDOM_NUM

            // Ensure the ID is unique
            while (isIDExists(conn, table, id)) {
                randomNum = random.nextInt(900) + 100;
                id = prefix + count + randomNum;
            }

            System.out.println("✅ Generated Unique ID: " + id);
        } catch (SQLException e) {
            System.err.println("❌ Error generating ID: " + e.getMessage());
        }
        return id;
    }

    private static boolean isIDExists(Connection conn, String table, String id) throws SQLException {
        String query = "";
        if (table.equals("WazeRoutes")) {
            query = "SELECT COUNT(*) FROM " + table + " WHERE route_id = ?";
        } else if (table.equals("WazePlannedDrives")) {
            query = "SELECT COUNT(*) FROM " + table + " WHERE planneddrive_id = ?";
        } else if (table.equals("WazeAltRoutes")) {
            query = "SELECT COUNT(*) FROM " + table + " WHERE alt_route_id = ?";

        } else if (table.equals("WazeTravelTime")) {
            query = "SELECT COUNT(*) FROM " + table + " WHERE traveltime_id = ?"; // +
        } else {
            throw new SQLException("Unknown table: " + table);
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public static String generatePlannedDriveID() {
        return generateID("WazePlannedDrives", "PLND");
    }

    public static String generateAltRouteID() {
        return generateID("WazeAltRoutes", "ALT");
    }

    public static String generateRandomAlternativeRoute(String startPoint, String endPoint, List<String> locationList) {
        Random random = new Random();

        if (locationList.isEmpty()) {
            return null;
        }
        String randomLocation = locationList.get(random.nextInt(locationList.size()));
        return startPoint + " -> " + randomLocation + " -> " + endPoint;
    }

    public static String generateRandomEstTime() {
        Random random = new Random();
        int hours = random.nextInt(5); // Random hours between 0-4
        int minutes = random.nextInt(60); // Random minutes between 0-59
        return String.format("%02d:%02d:00", hours, minutes);
    }
}
