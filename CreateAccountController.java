import java.io.IOException;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateAccountController {
    @FXML private TextField createUNTF, createEmailTF, createPassTF, firstnameTF, lastnameTF;
    @FXML private Button CreateSignUpButton, backtoSignInButton;
    @FXML private DatePicker birthdatePicker;

    private final DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML
    public void createbuttonHandler(ActionEvent event) throws IOException {
        String username = createUNTF.getText().trim().toLowerCase();
        String password = createPassTF.getText().trim();
        String email = createEmailTF.getText().trim();
        String firstname = firstnameTF.getText().trim();
        String lastname = lastnameTF.getText().trim();
        String birthdate = (birthdatePicker.getValue() != null) ? birthdatePicker.getValue().toString() : "";

        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || birthdate.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address (must be @gmail.com or @yahoo.com).");
            return;
        }

        // Insert user into database
        if (dbHandler.insertUser(username, password, email, birthdate, firstname, lastname)) {
            showSuccessPopup(); // Call success pop-up
        } else {
            showError("User already exists or database error occurred.");
        }
    }


    @FXML 
    public void backtoSignInButton(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) createUNTF.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
            stage.show();

    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@(yahoo|gmail)\\.com$";
        return Pattern.matches(regex, email);
    }

    private void showSuccessPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your account has been created successfully! Click 'Back to Login' to sign in.");

        ButtonType backToLoginButton = new ButtonType("Back to Login");
        alert.getButtonTypes().setAll(backToLoginButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == backToLoginButton) {
                goToLoginPage();
            }
        });
    }

    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) createUNTF.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
