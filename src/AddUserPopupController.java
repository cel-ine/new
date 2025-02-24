import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AddUserPopupController {
    @FXML private TextField emailField, usernameField, firstNameField, lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker birthdatePicker;
    @FXML private Button addUserBTN;

    private AdminHomepageController adminHomepageController;

    // Method to set the reference of AdminHomepageController
    public void setAdminHomepageController(AdminHomepageController adminHomepageController) {
        this.adminHomepageController = adminHomepageController;
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String birthdate = (birthdatePicker.getValue() != null) ? birthdatePicker.getValue().toString() : "";
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty() && !birthdate.isEmpty() &&
            !firstName.isEmpty() && !lastName.isEmpty()) {

            if (!isValidEmail(email)) {
                showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.WARNING);
                return;
            }

            AdminUser newUser = new AdminUser(email, username, password, birthdate, firstName, lastName);
            boolean success = AdminService.addUser(newUser);

            if (success) {
                showSuccessPopup(); // Call success pop-up
                // Update the table in AdminHomepageController
                if (adminHomepageController != null) {
                    adminHomepageController.loadAccountManagerData();
                }
            } else {
                showAlert("Error", "User already exists or database error occurred.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "Please fill in all fields.", Alert.AlertType.WARNING);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private void showSuccessPopup() {
        showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}