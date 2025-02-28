import java.io.File;
import java.io.IOException;
import java.sql.Date;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
// import java.sql.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.print.DocFlavor.URL;

public class UserHomepageController {
    //MAIN PANE 
    @FXML private Pane accountPaneLeft, currentDatePane, currentTimePane, parentAnchorPane, foundationPane2;
    @FXML private Pane homeanchrPane, mainPane, nagivateAnchorPane, planneddrivesanchPane, routeanchPane;
    @FXML private AnchorPane homeAnchPicPane;
    @FXML private StackPane homePane, planneddrivesstckPane, routestckPane, stackpaneImages;
    @FXML private HBox hboxImage;
    @FXML private MenuButton menuBTN;
    @FXML private MenuItem signOut;
    @FXML private WebView mapWebView; 

    @FXML private Label currentDate, currentTime, drivesLabel, endLabel, routeLabel, selectDateLabel;
    @FXML private Label startLabel, timeLabel, txtDate, txtTime, usernameLabel;
    @FXML private Button addBTN, addRoute, deleteBTN, homeButton, planneddrivesButton, routeButton, updateBTN, updatePlanBTN;
    @FXML private Circle drivePic, profilePic, routePic;
    @FXML private ImageView endPic, userHomepageImage1, userHomepageImage2, imageTwo, startPic, wazeLogo;
    @FXML private DatePicker dateInput;
    @FXML private ComboBox<String> endLoc, inputTime, planEndLoc, planStartLoc, startLoc, stopOverLoc;
    @FXML private TableView<UserPlannedDrives> planneddrivesTable; 
    // ROUTE TABLE
    @FXML private TableView<UserRouteDetails> routeTable;
    @FXML private TableColumn<UserRouteDetails, String> altRoutesCol, endCol, startCol, stopOverRouteLoc;
    @FXML private TableColumn<UserRouteDetails, Time> estTimeCol;
    //PLANNED DRIVES TABLE
    @FXML private TableColumn<UserPlannedDrives, String> pinnedLocCol, startLocCol;
    @FXML private TableColumn<UserPlannedDrives, Date> planCalendar;
    @FXML private TableColumn<UserPlannedDrives, Time> planTime;
 
    private int accountId;
    private UserAccount currentUser;

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    public void setCurrentUser(UserAccount user) {
        this.currentUser = user;
    }
    private ObservableList<UserRouteDetails> routeList = FXCollections.observableArrayList(); // ROUTES TABLE 
    private ObservableList<UserPlannedDrives> plannedList = FXCollections.observableArrayList(); //PLANNED DRIVES TABLE

    @FXML
    public void initialize() {
        showPane(homePane);
        startDateTimeUpdater();
        loadMap();

        loadLocations();

        loadTimes();
        
        Platform.runLater(() -> { //to display the username logged in to menu item 
            Scene scene = parentAnchorPane.getScene();
            if (scene != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            }
        });
    
        // Initialize Route Table
        List<UserRouteDetails> editedRoutes = new ArrayList<>();
        startCol.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        altRoutesCol.setCellValueFactory(new PropertyValueFactory<>("alternativeRoutes"));
        stopOverRouteLoc.setCellValueFactory(new PropertyValueFactory<>("stopOverLocation"));
        estTimeCol.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        routeTable.setItems(routeList);
        UserTableEditor.makeRouteTableEditable(startCol, endCol, altRoutesCol, stopOverRouteLoc, estTimeCol);
        routeTable.setEditable(true);
        loadRoutes();

        // Initialize Planned Drive Table
        planCalendar.setCellValueFactory(new PropertyValueFactory<>("plannedDate"));
        planTime.setCellValueFactory(new PropertyValueFactory<>("plannedTime"));
        startLocCol.setCellValueFactory(new PropertyValueFactory<>("startLoc"));
        pinnedLocCol.setCellValueFactory(new PropertyValueFactory<>("pinnedLoc"));
        planneddrivesTable.setItems(plannedList);
        UserTableEditor.makePlannedDrivesTableEditable(startLocCol, pinnedLocCol, planTime, planCalendar);
        planneddrivesTable.setEditable(true);
        loadPlannedDrives();

    }
    private String loggedInUsername;
    private String imagePath; 

    public void setUsername(String username, String imagePath) {
        this.loggedInUsername = username;
        menuBTN.setText(username); 

        if (imagePath != null && !imagePath.isEmpty()) {
            Image profileImage = new Image(imagePath);
            userHomepageImage1.setImage(profileImage);
            userHomepageImage2.setImage(profileImage);
        }
    }
    private void loadMap() {
        WebEngine webEngine = mapWebView.getEngine();
        webEngine.load("https://www.waze.com/live-map/");
    }
    

    public void updateProfilePicture(String imagePath) {
        Image newImage = new Image(imagePath);
        userHomepageImage1.setImage(newImage);
        userHomepageImage2.setImage(newImage);
    }    

    public void setUserData(int accountId, String username, String imagePath) {
        this.accountId = accountId; //SAVES THE USER'S ACC ID 
        this.loggedInUsername = username;
        menuBTN.setText(username); 
    
        if (imagePath != null && !imagePath.isEmpty()) {
            Image newImage = new Image(imagePath);
            userHomepageImage1.setImage(newImage);
            userHomepageImage2.setImage(newImage);
        }
    }
    //GETTER FOR ACC ID PARA FETCH NI USERHOMEPAGE
    public int getAccountId() {
        return accountId;
    }
    
    private void startDateTimeUpdater() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    updateDate();
                    updateTime();
                });
            }
        }, 0, 1000);
    }

    private void updateDate() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Manila"));
        txtDate.setText("📆 " + currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
    }

    private void updateTime() {
        LocalTime currentTimePH = LocalTime.now(ZoneId.of("Asia/Manila"));
        txtTime.setText("⏰ " + currentTimePH.format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
    }

    @FXML
    private void handleHomeButtonClick(ActionEvent event) {
        setActiveButton(homeButton);
        showPane(homePane);
    }

    @FXML
    private void handleRouteButtonClick(ActionEvent event) {
        setActiveButton(routeButton);
        showPane(routestckPane);
        loadRoutes();
    }

    @FXML
    private void handlePlannedDrivesButtonClick(ActionEvent event) {
        setActiveButton(planneddrivesButton);
        showPane(planneddrivesstckPane);
        loadPlannedDrives();
    }

    @FXML
    private void handleAccountSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserAccountSettings.fxml"));
            Parent root = loader.load();
            UserAccountSettingsController settingsController = loader.getController();

            String latestImagePath = UserService.loadProfilePicture(loggedInUsername);

            settingsController.setUserData(loggedInUsername, latestImagePath);
            settingsController.setUserHomepageController(this); 

            Stage stage = (Stage) menuBTN.getScene().getWindow(); 
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

















    //CRUD BUTTONS

    @FXML
    private void handleAddRouteButtonClick(ActionEvent event) {
        String start = startLoc.getValue();
        String end = endLoc.getValue();
        String selectedStopOverLoc = stopOverLoc.getValue();

        //accountId

        if (start == null || end == null) {
            showAlert("Input Error", "Start Location and End Location must be selected.", AlertType.ERROR);
            return;
        }

        // Ensure accountId is set
        if (accountId == 0) {
            showAlert("Error", "User account ID is not set. Please log in again.", AlertType.ERROR);
            return;
        }

        System.out.println("Account ID: " + accountId);

        UserRouteDetails newRoute = new UserRouteDetails(accountId, UserService.generateRouteID(), start,
                end, null, selectedStopOverLoc, null);

        ObservableList<String> observableLocationList = UserService.getAllLocations(); // Get ObservableList
        List<String> locationList = FXCollections.observableArrayList(observableLocationList); // Convert to List
        UserDatabaseHandler.addRoutes(newRoute, locationList);
        loadRoutes();
    }
//GET 
    // @FXML
    // private void addRouteMethod(ActionEvent event) {
    // if (startLoc.getValue() == null || endLoc.getValue() == null) {
    // showAlert("Input Error", "Start Location and End Location must be
    // selected.");
    // return;
    // }
    // UserRouteDetails newRoute = new UserRouteDetails(accountId,
    // UserService.generateRouteID(), startLoc.getValue(),
    // endLoc.getValue(), null, null, null);

    @FXML
    private void handleUpdateRouteButtonClick(ActionEvent event) {
        Set<UserRouteDetails> editedRoutes = UserTableEditor.getEditedRoutes();
        boolean success = true;
        for (UserRouteDetails route : editedRoutes) {
            if (!UserService.updateUserRoute(route)) {
                success = false;
            }
        }
        showAlert(success ? "Success" : "Error",
                success ? "Route details updated successfully!" : "Failed to update route details.",
                success ? AlertType.INFORMATION : AlertType.ERROR);
        if (success) {
            loadRoutes(); // Reload data from the database
            TableEditor.clearEditedRoutes();
        }
    }

    @FXML
    private void handleAddPlannedDriveButtonClick(ActionEvent event) {
        String startLoc = planStartLoc.getValue();
        String endLoc = planEndLoc.getValue();
        LocalDate planDate = dateInput.getValue();

        if (startLoc == null || endLoc == null) {
            showAlert("Input Error", "All fields must be filled.", AlertType.ERROR);
            return;
        }

        // Properly format time (assuming inputTime is in "HH:mm" format)
        Time inputTimeValue = Time.valueOf(inputTime.getValue() + ":00");

        UserDatabaseHandler.getInstance().insertPlannedDrive(planDate, inputTimeValue, startLoc, endLoc);
        loadPlannedDrives();
    }

    @FXML
    private void handleDeletePlannedDriveButtonClick(ActionEvent event) {
        UserPlannedDrives selectedDrive = planneddrivesTable.getSelectionModel().getSelectedItem();

        if (selectedDrive != null) {
            // Show confirmation alert
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected planned drive?");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            alert.showAndWait().ifPresent(type -> {
                if (type == buttonTypeYes) {
                    // User confirmed deletion
                    UserService.deleteUserPlannedDrive(selectedDrive.getPlannedDriveID());
                    loadPlannedDrives();
                }
            });
        } else {
            showAlert("No Selection", "No planned drive selected. Please select a planned drive to delete.",
                    AlertType.WARNING);
        }
    }

    @FXML
    private void handleDeleteRouteButtonClick(ActionEvent event) {
        UserRouteDetails selectedRoute = routeTable.getSelectionModel().getSelectedItem();

        if (selectedRoute != null) {
            // Show confirmation alert
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected route?");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            alert.showAndWait().ifPresent(type -> {
                if (type == buttonTypeYes) {
                    // User confirmed deletion
                    UserService.deleteUserRoute(selectedRoute.getRouteId());
                    loadRoutes();
                }
            });
        } else {
            showAlert("No Selection", "No route selected. Please select a route to delete.", AlertType.WARNING);
        }
    }

    // @FXML
    // private void handleUpdatePlannedDriveButtonClick(ActionEvent event) {
    //     Set<UserPlannedDrives> editedPlannedDrives = UserTableEditor.getEditedPlannedDrives();
    //     boolean success = true;
    //     for (UserPlannedDrives drive : editedPlannedDrives) {
    //         if (!UserService.updateUserPlannedDrive(drive)) {
    //             success = false;
    //         }
    //     }
    //     showAlert(success ? "Success" : "Error",
    //             success ? "Planned drive details updated successfully!" : "Failed to update planned drive details.",
    //             success ? AlertType.INFORMATION : AlertType.ERROR);
    //     if (success) {
    //         loadPlannedDrives(); // Reload data from the database
    //         UserTableEditor.clearEditedPlannedDrives();
    //     }
    // }

    private void loadRoutes() {
        routeList.setAll(UserService.getUserRoutes(accountId));
        routeTable.refresh();
    }

    private void loadPlannedDrives() {
        plannedList.setAll(UserService.getUserPlannedDrives(accountId));
        planneddrivesTable.refresh();
    }

    private void loadLocations() {
        ObservableList<String> locations = UserService.getAllLocations();
        ObservableList<String> stopoverLocations = UserService.getStopoverLocations();

        startLoc.setItems(locations);
        endLoc.setItems(locations);
        planStartLoc.setItems(locations);
        planEndLoc.setItems(locations);
        stopOverLoc.setItems(stopoverLocations);
    }

    private void loadTimes() {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(String.format("%02d:%02d", hour, minute));
            }
        }
        inputTime.setItems(times);
    }

    private void showPane(StackPane paneToShow) {
        homePane.setVisible(false);
        routestckPane.setVisible(false);
        planneddrivesstckPane.setVisible(false);
        paneToShow.setVisible(true);
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button activeButton = null; // Track the currently active button

    public void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active"); // Remove "active" from the previous button
        }

        button.getStyleClass().add("active"); // Add "active" to the clicked button
        activeButton = button; // Update the active button
    }

    public void handleSignOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        Stage signInStage = new Stage();
        signInStage.setScene(new Scene(root));
        signInStage.show();

        stage.hide();
    }

   
}


