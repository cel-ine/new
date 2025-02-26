import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddRoutesPopupController {

    @FXML private ComboBox<String> accountIDCombobox, startPointComboBox, endPointCombobox, stopOverCombobox;
    @FXML private Button saveRouteBTN;

    private AdminHomepageController adminHomepageController;
    private ObservableList<String> locationList = FXCollections.observableArrayList();

    public void setAdminHomepageController(AdminHomepageController adminHomepageController) {
        this.adminHomepageController = adminHomepageController;
    }

    // Initialize method to load data when the scene loads
    public void initialize() {
        accountIDCombobox.setItems(AdminService.getAllUsernames());
        locationList = AdminService.getAllLocations(); // Load locations from DB
        startPointComboBox.setItems(locationList);
        endPointCombobox.setItems(locationList);
        stopOverCombobox.setItems(AdminService.getAllStopovers()); // Load stopovers from DB
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
            ObservableList<String> filteredEndPoints = FXCollections.observableArrayList(locationList);
            filteredEndPoints.remove(selectedStart);
            endPointCombobox.setItems(filteredEndPoints);

            if (!filteredEndPoints.contains(endPointCombobox.getValue())) {
                endPointCombobox.setValue(null);
            }
        }
    }

    @FXML
    private void onEndPointSelected(ActionEvent event) {
        String selectedEnd = endPointCombobox.getValue();
        String selectedStart = startPointComboBox.getValue();

        if (selectedEnd != null) {
            ObservableList<String> filteredStartPoints = FXCollections.observableArrayList(locationList);
            filteredStartPoints.remove(selectedEnd);

            startPointComboBox.setItems(filteredStartPoints);

            if (selectedStart != null && filteredStartPoints.contains(selectedStart)) {
                startPointComboBox.setValue(selectedStart);
            } else {
                startPointComboBox.setValue(null);
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

    private void closeWindow() {
        Stage stage = (Stage) saveRouteBTN.getScene().getWindow();
        stage.close();
    }

    private void showSuccessPopup() {
        showAlert("Success", "Route added successfully!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
