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

    private final AdminService adminService = new AdminService();

    @FXML
    private void LoginButtonHandler(ActionEvent event) throws IOException {
        String username = SignUpUsernameTF.getText();
        String password = SignUpPwTF.getText();
    
        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter a username and password.");
            return;
        }
    
        if (DatabaseHandler.validateLogin(username, password)) {
            DatabaseHandler.updateLastLogin(username);
    
            // ✅ Fetch account ID from database
            int accountId = UserDatabaseHandler.getUserId(username);
            String role = username.equalsIgnoreCase("admin") ? "ADMIN" : "USER";
    
            // ✅ Store logged-in user in UserService
            UserService.getInstance().setCurrentUser(accountId, role);
    
            // ✅ Load the correct homepage (Admin or User)
            FXMLLoader loader;
            if (role.equals("ADMIN")) {
                loader = new FXMLLoader(getClass().getResource("AdminHomepage.fxml")); // Admin Homepage
            } else {
                loader = new FXMLLoader(getClass().getResource("UserHomepage.fxml")); // User Homepage
            }
    
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
    
            // ✅ Set user data based on the loaded FXML
            if (role.equals("ADMIN")) {
                AdminHomepageController adminController = loader.getController();
                adminController.setUsername(username, adminService.loadProfilePicture(username));
                adminController.setCurrentUser(username);
            } else {
                UserHomepageController userController = loader.getController();
                userController.setUserData(accountId, username, adminService.loadProfilePicture(username));
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
