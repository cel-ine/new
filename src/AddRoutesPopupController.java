import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class AddRoutesPopupController {

    @FXML private ComboBox<String> accountIDCombobox, startPointComboBox, endPointCombobox, stopOverCombobox;
    @FXML private Button saveRouteBTN;

    private AdminHomepageController adminHomepageController;

    public void setAdminHomepageController(AdminHomepageController adminHomepageController) {
        this.adminHomepageController = adminHomepageController;
    }

    private ObservableList<String> locationList = FXCollections.observableArrayList();

    // Initialize method to load data when the scene loads
    public void initialize() {
        loadUsernames(); 
        locationList = loadLocations();
        startPointComboBox.setItems(locationList); 
        endPointCombobox.setItems(locationList); 
        loadStopovers(); 
    }
    // Load account IDs and usernames from WazeAccounts table
    public void loadUsernames() {
        ObservableList<String> accountList = FXCollections.observableArrayList();
        String query = "SELECT account_id, username FROM WazeAccounts";
    
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                int accountID = rs.getInt("account_id");
                String username = rs.getString("username");
                String displayText = accountID + " - " + username;
                accountList.add(displayText);
            }
    
            accountIDCombobox.setItems(accountList);
        } catch (SQLException e) {
            System.err.println("Error loading account data: " + e.getMessage());
        }
    }

    public static ObservableList<String> loadLocations() {
        ObservableList<String> locationList = FXCollections.observableArrayList();
        String query = "SELECT name FROM locations ORDER BY name ASC"; 
    
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                locationList.add(rs.getString("name"));
            }
    
            System.out.println("Loaded locations: " + locationList); // Debugging output
    
        } catch (SQLException e) {
            System.err.println("Error loading locations: " + e.getMessage());
        }
        return locationList;
    }
    

    public void loadStopovers() {
        ObservableList<String> stopoverList = FXCollections.observableArrayList();
        String query = "SELECT name FROM StopoverLocations";
    
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                stopoverList.add(rs.getString("name"));
            }
    
            stopOverCombobox.setItems(stopoverList);
        } catch (SQLException e) {
            System.err.println("Error loading stopovers: " + e.getMessage());
        }
    }
    

    @FXML
    private void handleAddRoute(ActionEvent event) {
        String selectedEntry = accountIDCombobox.getValue();
        String selectedStart = startPointComboBox.getValue();
        String selectedEnd = endPointCombobox.getValue();
        String selectedStopOver = stopOverCombobox.getValue();

        // Validation: Ensure required fields are selected
        if (selectedEntry == null || selectedEntry.trim().isEmpty() ||
            selectedStart == null || selectedStart.trim().isEmpty() ||
            selectedEnd == null || selectedEnd.trim().isEmpty()) {
            
            showAlert("Error", "Please choose user, start location, and end location.", Alert.AlertType.ERROR);
            return;
        }

        String[] parts = selectedEntry.split(" - ");
        int accountID;
        try {
            accountID = Integer.parseInt(parts[0]); // Get only the ID
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid account selection.", Alert.AlertType.ERROR);
            return;
        }
        String alternativeRoute = RouteIDGenerator.generateRandomAlternativeRoute(selectedStart, selectedEnd, locationList); 
        // Create and save the new route
        AdminRoutes newRoute = new AdminRoutes(accountID, selectedStart, selectedEnd, alternativeRoute, selectedStopOver);
        boolean success = AdminService.addRoutes(newRoute, locationList);

        if (success) {
            showSuccessPopup();
            if (adminHomepageController != null) {
                adminHomepageController.loadRouteManagerData(); // Refresh table
            }
            closeWindow();
        } else {
            showAlert("Error", "Failed to add route. Please try again.", Alert.AlertType.ERROR);
        }
    }
   
    @FXML
    private void onStartPointSelected(ActionEvent event) {
        String selectedStart = startPointComboBox.getValue();
    
        if (selectedStart != null) {
            // Create a new filtered list
            ObservableList<String> filteredEndPoints = FXCollections.observableArrayList(locationList);
            filteredEndPoints.remove(selectedStart);
    
            // Update the endpoint ComboBox
            endPointCombobox.setItems(filteredEndPoints);
    
            // Reset selection if it's now invalid
            if (!filteredEndPoints.contains(endPointCombobox.getValue())) {
                endPointCombobox.setValue(null);
            }
        }
    }
    

    @FXML
    private void onEndPointSelected(ActionEvent event) {
        String selectedEnd = endPointCombobox.getValue();
        String selectedStart = startPointComboBox.getValue(); // Store current start selection


        if (selectedEnd != null) {
            // Use a fresh copy of locationList for filtering
            ObservableList<String> filteredStartPoints = FXCollections.observableArrayList(locationList);
            filteredStartPoints.remove(selectedEnd);

            startPointComboBox.setItems(filteredStartPoints);

            // Restore the previous selection if still valid
            if (selectedStart != null && filteredStartPoints.contains(selectedStart)) {
                startPointComboBox.setValue(selectedStart);
            } else {
                startPointComboBox.setValue(null); // Reset if no longer valid
            }
        }
    }


    @FXML
    private void onStopOverSelected(ActionEvent event) {
        String selectedStopover = stopOverCombobox.getValue();

        if (selectedStopover != null) {
            if (selectedStopover.equals(startPointComboBox.getValue()) ||
                selectedStopover.equals(endPointCombobox.getValue())) {
                showAlert("Error", "Stopover cannot be the same as the Start or End point.", Alert.AlertType.ERROR);
                stopOverCombobox.setValue(null);
            }
        }
    }

    // Close the pop-up window
    private void closeWindow() {
        Stage stage = (Stage) saveRouteBTN.getScene().getWindow();
        stage.close();
    }

    // Display success pop-up
    private void showSuccessPopup() {
        showAlert("Success", "Route added successfully!", Alert.AlertType.INFORMATION);
    }

    // Display alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
