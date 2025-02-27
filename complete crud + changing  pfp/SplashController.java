import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("Stage received in SplashController: " + stage);
    }

    @FXML
    public void initialize() {
        System.out.println("SplashController initialized");

        // Ensure the stage is not null before switching screens
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            if (stage == null) {
                System.out.println("Error: Stage is null! Cannot switch to login screen.");
                return;
            }
            System.out.println("Switching to login screen...");
            loadLoginScreen();
        });
        delay.play();
    }

    private void loadLoginScreen() {
        try {
            System.out.println("Loading login.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Ensure the controller gets the stage
            // LoginController loginController = loader.getController();
            // loginController.setStage(stage);

            // Ensure UI updates are done on the JavaFX thread
            Platform.runLater(() -> {
                Scene scene = new Scene(root, 900, 600);
                stage.setScene(scene);
                stage.show();
                System.out.println("Login screen loaded!");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
