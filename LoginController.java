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
        String username = SignUpUsernameTF.getText(); //papasa kay homepage
        String password = SignUpPwTF.getText();

        int accountId = UserDatabaseHandler.getUserId(username); //used to fetch the acc id nung nag log in

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter a username and password.");
            return;
        }

        if (DatabaseHandler.validateLogin(username, password)) {
            DatabaseHandler.updateLastLogin(username);

            UserService.getInstance().setCurrentUser(
            UserDatabaseHandler.getUserId(username),
            username.equalsIgnoreCase("admin") ? "ADMIN" : "USER"
        );

            String imagePath = adminService.loadProfilePicture(username);
        if (imagePath == null) {
            imagePath = ""; 
        }

        FXMLLoader loader;
        if (username.equalsIgnoreCase("admin")) {
            loader = new FXMLLoader(getClass().getResource("AdminHomepage.fxml")); // Admin Homepagev

        } else {
            loader = new FXMLLoader(getClass().getResource("UserHomepage.fxml")); // User Homepage
        }

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            if (username.equalsIgnoreCase("admin")) {
                AdminHomepageController adminController = loader.getController();
                adminController.setUsername(username, imagePath);
                adminController.setCurrentUser(username);
            } else {
                UserHomepageController userController = loader.getController();
                userController.setUserData(accountId, username, imagePath); // PASS THE ACC ID KAY USERHOMEPAGE
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
