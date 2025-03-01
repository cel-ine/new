import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddPlannedDrivePopupController {
    @FXML private ComboBox<String> accountIDComboBox1, startComboBox, endComboBox;
    @FXML private DatePicker calendarPicker;
    @FXML private TextField prefTimeTextField;
    @FXML private Button savePlannedDriveBTN;

    private AdminHomepageController adminHomepageController;
    
    private ObservableList<String> timeList = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private ObservableList<String> locationList = FXCollections.observableArrayList();

    public void setAdminHomepageController(AdminHomepageController adminHomepageController) {
        this.adminHomepageController = adminHomepageController;
    }

    @FXML
    public void initialize() {
        accountIDComboBox1.setItems(AdminService.getAllUsernames());
        
        locationList = AdminService.getAllLocations(); 
        startComboBox.setItems(locationList);
        endComboBox.setItems(locationList);
        startComboBox.setEditable(true);
        endComboBox.setEditable(true);
        accountIDComboBox1.setEditable(true);
        calendarPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now())); // Disable past dates
            }
        });
        
        populateMilitaryTimeList(); // Load all times
    }

    private void populateMilitaryTimeList() {
        LocalTime time = LocalTime.MIDNIGHT;
        while (!time.equals(LocalTime.of(23, 59).plusMinutes(1))) { // Until 23:59
            timeList.add(time.format(timeFormatter)); // 24-hour format
            time = time.plusMinutes(1); // Increment by 1 minute
        }
    }

    @FXML
    private void handleAddPlannedDrive(ActionEvent event) {
        String accountID = accountIDComboBox1.getValue();
        LocalDate calendar = calendarPicker.getValue(); // Get selected date
        String prefTimeText = prefTimeTextField.getText().trim();
        String startLoc = startComboBox.getValue();
        String endLoc = endComboBox.getValue();

        if (accountID == null || calendar == null || prefTimeText.isEmpty() || startLoc == null || endLoc == null) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        // 🔍 Debugging: Print selected and current date
        System.out.println("Selected Date: " + calendar);
        System.out.println("Today's Date: " + LocalDate.now());

        // 🔥 Validation: Ensure the selected date is today or in the future
        if (calendar.isBefore(LocalDate.now())) {
            showAlert("Invalid Date", "You cannot select a past date.", Alert.AlertType.WARNING);
            return;
        }

        try {
            LocalTime enteredTime = LocalTime.parse(prefTimeText, timeFormatter);
        } catch (DateTimeParseException e) {
            showAlert("Invalid Time", "Time format should be HH:MM.", Alert.AlertType.WARNING);
            return;
        }

        LocalTime plannedTime = LocalTime.parse(prefTimeText, timeFormatter);

        AdminPlannedDrives newPlannedDrive = new AdminPlannedDrives(
            Integer.parseInt(accountID.split(" - ")[0]),
            calendar,
            plannedTime,
            startLoc,
            endLoc
        );

        boolean success = AdminService.addPlannedDrive(newPlannedDrive);

        if (success) {
            showSuccessPopup();
            if (adminHomepageController != null) {
                adminHomepageController.loadPlannedDrivesData();
            }
            closeWindow();
        } else {
            showAlert("Error", "Failed to add planned drive. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) savePlannedDriveBTN.getScene().getWindow();
        stage.close();
    }

    private void showSuccessPopup() {
        showAlert("Success", "Planned drive added successfully!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
