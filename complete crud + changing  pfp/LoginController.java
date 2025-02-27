import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField SignUpUsernameTF;
    @FXML private PasswordField SignUpPwTF;
    @FXML private Button SignUpButton, SignInButton;

    private final AdminService adminService = new AdminService(); // ✅ AdminService instance

    @FXML
    private void LoginButtonHandler(ActionEvent event) throws IOException {
        String username = SignUpUsernameTF.getText();
        String password = SignUpPwTF.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter a username and password.");
            return;
        }

        // Validate login credentials
        if (DatabaseHandler.validateLogin(username, password)) {
            DatabaseHandler.updateLastLogin(username);

            // ✅ Fetch profile picture path (AFTER username is retrieved)
            String imagePath = adminService.loadProfilePicture(username);
            if (imagePath == null) {
                imagePath = ""; // Default to empty if no profile picture
            }

            FXMLLoader loader;
            if (username.equalsIgnoreCase("admin")) {
                loader = new FXMLLoader(getClass().getResource("AdminHomepage.fxml")); // Admin Homepage
            } else {
                loader = new FXMLLoader(getClass().getResource("UserHomepage.fxml")); // User Homepage
            }

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // ✅ Pass username & imagePath to controller
            if (username.equalsIgnoreCase("admin")) {
                AdminHomepageController adminController = loader.getController();
                adminController.setUsername(username, imagePath);
            } else {
                UserHomepageController userController = loader.getController();
                userController.setUsername(username);
            }

        } else {
            showLoginError("Invalid username or password.");
        }
    }

    @FXML
    private void createbuttonhandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAcc.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    private void showLoginError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText("Invalid Credentials");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
