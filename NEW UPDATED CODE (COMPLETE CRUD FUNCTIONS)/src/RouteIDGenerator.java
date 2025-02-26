import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RouteIDGenerator {
    private static int routeCounter = getLastRouteCounter() + 1; // Get last used ID from DB
    private static int altRouteCounter = getLastAltRouteID() + 1; // Get last alt ID from DB

    public static String generateRouteID(String startLocation, String endLocation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());

        routeCounter = getLastRouteCounter() + 1; // Fetch latest route ID from DB
        return "WZE-RTS-" + currentDate + "-" + String.format("%03d", routeCounter++);
    }    

    public static String generateAltRouteID() {
        String altRouteID = "ALT-" + String.format("%03d", altRouteCounter);
        altRouteCounter++; 
        return altRouteID;
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
        String query = "SELECT MAX(alt_route_id) FROM WazeAltRoutes";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastAltID = rs.getString(1);
                if (lastAltID != null) {
                    lastID = Integer.parseInt(lastAltID.split("-")[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching last alternative route ID: " + e.getMessage());
        }
        return lastID;
    }

    private static int getLastRouteCounter() {
        int lastID = 0;
        String query = "SELECT route_id FROM WazeRoutes WHERE route_id LIKE ? ORDER BY route_id DESC LIMIT 1";
    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());
    
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            pstmt.setString(1, "WZE-RTS-" + currentDate + "-%");
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                String lastRouteID = rs.getString("route_id");
                if (lastRouteID != null) {
                    lastID = Integer.parseInt(lastRouteID.split("-")[3]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching last route counter: " + e.getMessage());
        }
    
        return lastID;
    }
}
