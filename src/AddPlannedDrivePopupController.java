import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddPlannedDrivePopupController {
    @FXML private ComboBox<String> accountIDComboBox1, prefTimeCombobox;
    @FXML private DatePicker calendarPicker;
    @FXML private TextField pinnedLocTF, startLocTF;
    @FXML private Button savePlannedDriveBTN;

    private AdminHomepageController adminHomepageController;
    
    private ObservableList<String> timeList = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private ObservableList<String> accountIDs = FXCollections.observableArrayList();

    // Set reference of AdminHomepageController
    public void setAdminHomepageController(AdminHomepageController adminHomepageController) {
        this.adminHomepageController = adminHomepageController;
    }

    @FXML
    public void initialize() {
        populateTimeList(); // Load all times into ComboBox
        prefTimeCombobox.setItems(timeList);
        prefTimeCombobox.setEditable(true); // Enable text input

        prefTimeCombobox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            filterComboBox(prefTimeCombobox, newValue);
        });

        accountIDs = loadAccountIDs();
    }

    // Load account IDs from WazeAccounts to populate ComboBox
    public ObservableList<String> loadAccountIDs() {
        String query = "SELECT account_id, username FROM WazeAccounts";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int accountID = rs.getInt("account_id");
                String username = rs.getString("username");
                String displayText = accountID + " - " + username;
                accountIDs.add(displayText);
            }

            accountIDComboBox1.setItems(accountIDs);
        } catch (SQLException e) {
            System.err.println("Error loading account data: " + e.getMessage());
        }
                return accountIDs;
    }

    // Populate time list with every minute from 12:00 AM to 11:59 PM
    private void populateTimeList() {
        LocalTime time = LocalTime.MIDNIGHT; // Start at 12:00 AM
        while (!time.equals(LocalTime.of(23, 59).plusMinutes(1))) { // Until 11:59 PM
            timeList.add(time.format(timeFormatter)); // Add formatted time
            time = time.plusMinutes(1); // Increment by 1 minute
        }
    }

    // Filters the ComboBox based on user input
    private void filterComboBox(ComboBox<String> comboBox, String userInput) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();
        for (String time : timeList) {
            if (time.toLowerCase().contains(userInput.toLowerCase())) {
                filteredList.add(time);
            }
        }
        comboBox.setItems(filteredList);
        comboBox.show(); // Keep dropdown open
    }

    // Handle adding a new planned drive
    @FXML
    private void handleAddPlannedDrive(ActionEvent event) {
        String accountID = accountIDComboBox1.getValue();
        LocalDate calendar = calendarPicker.getValue();
        String prefTimeText = prefTimeCombobox.getEditor().getText().trim();
        String startLoc = startLocTF.getText();
        String pinnedLoc = pinnedLocTF.getText();

        // Check for empty fields
        if (accountID == null || calendar == null || prefTimeText.isEmpty() || pinnedLoc.isEmpty() || startLoc.isEmpty()) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        // Validate and parse time
        LocalTime plannedTime;
        try {
            plannedTime = LocalTime.parse(prefTimeText, timeFormatter);
        } catch (DateTimeParseException e) {
            showAlert("Invalid Time", "Please enter a valid time in HH:MM AM/PM format.", Alert.AlertType.WARNING);
            return;
        }

        // Create AdminPlannedDrives object with parsed LocalTime
        AdminPlannedDrives newPlannedDrive = new AdminPlannedDrives(
            Integer.parseInt(accountID.split(" - ")[0]), // Extract numeric account ID
            calendar,
            plannedTime,
            startLoc,
            pinnedLoc
        );

        boolean success = AdminService.addPlannedDrive(newPlannedDrive);

        if (success) {
            showSuccessPopup();
            if (adminHomepageController != null) {
                adminHomepageController.loadPlannedDrivesData(); // Refresh table
            }
            closeWindow();
        } else {
            showAlert("Error", "Failed to add planned drive. Please try again.", Alert.AlertType.ERROR);
        }
    }

    // Close the pop-up window
    private void closeWindow() {
        Stage stage = (Stage) savePlannedDriveBTN.getScene().getWindow();
        stage.close();
    }

    // Display success pop-up
    private void showSuccessPopup() {
        showAlert("Success", "Planned drive added successfully!", Alert.AlertType.INFORMATION);
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