import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RouteIDGenerator {
    private static int routeCounter = getLastRouteCounter() + 1;
    private static int altRouteCounter = getLastAltRouteID() + 1;

    public static String generateRouteID(String startLocation, String endLocation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());

        routeCounter = getLastRouteCounter() + 1; // Ensure fresh ID fetching
        return "WZE-RTS-" + currentDate + "-" + String.format("%03d", routeCounter++);
    }

    public static String generateAltRouteID() {
        altRouteCounter = getLastAltRouteID() + 1; // Always get the latest ID
        return String.format("ALT-%03d", altRouteCounter++);
    }

    public static String generateRandomEstTime() {
        Random random = new Random();
        int hours = random.nextInt(5);
        int minutes = random.nextInt(46) + 15;
        return String.format("%02d:%02d:00", hours, minutes);
    }

    public static String generateRandomAlternativeRoute(String start, String end, List<String> locationList) {
        List<String> filteredLocations = new ArrayList<>(locationList);
        filteredLocations.remove(start);
        filteredLocations.remove(end);

        if (filteredLocations.isEmpty()) {
            return "No Alternative Route";
        }

        String randomLocation = filteredLocations.get(new Random().nextInt(filteredLocations.size()));
        return "Via " + randomLocation;
    }

    private static int getLastAltRouteID() {
        int lastID = 0;
        String query = "SELECT MAX(CAST(SUBSTRING_INDEX(alt_route_id, '-', -1) AS UNSIGNED)) FROM WazeAltRoutes";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                lastID = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error fetching last alternative route ID: " + e.getMessage());
        }
        return lastID;
    }

    private static int getLastRouteCounter() {
        int lastID = 0;
        String query = "SELECT MAX(CAST(SUBSTRING_INDEX(route_id, '-', -1) AS UNSIGNED)) FROM WazeRoutes WHERE route_id LIKE ?";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "WZE-RTS-" + currentDate + "-%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                lastID = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error fetching last route counter: " + e.getMessage());
        }

        return lastID;
    }
}
