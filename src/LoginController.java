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

    // private Stage stage;
    // private Scene scene;
    private Parent root;

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

        FXMLLoader loader;
        if (username.equalsIgnoreCase("admin")) {
            loader = new FXMLLoader(getClass().getResource("AdminHomepage.fxml")); // Admin Homepage
        } else {
            loader = new FXMLLoader(getClass().getResource("UserHomepage.fxml")); // User Homepage
        }

        root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Pass username to controller
        if (username.equalsIgnoreCase("admin")) {
            AdminHomepageController adminController = loader.getController();
            // adminController.setUsername(username);
        } else {
            UserHomepageController userController = loader.getController();
            userController.setUsername(username);
        }

    } else {
        showLoginError("Invalid username or password.");
    }
}

    @FXML
    private void createbuttonhandler(ActionEvent event) throws IOException { //SIGN UP HANDLER PARA SA CREATE ACCOUNT
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAcc.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
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
