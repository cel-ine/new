import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserHomepageController {
    @FXML
    private Label welcomeLabel;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username + "!");
    }
}
