import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Application started!");  // Debug print

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("introsplash.fxml"));
            Parent root = loader.load();

            System.out.println("introsplash.fxml loaded!");  // Debug print

            // Get SplashController and pass the stage
            SplashController controller = loader.getController();
            controller.setStage(primaryStage); // Pass the stage

            Scene scene = new Scene(root, 800, 500);
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Splash screen displayed!");  // Debug print
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading introsplash.fxml");  // Debug print
        }
    }

    public static void main(String[] args) {
        System.out.println("Launching application...");  // Debug print
        launch(args);
    }
}
