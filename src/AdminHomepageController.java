import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminHomepageController {
    // MAIN PANE
    @FXML private StackPane MainPane;
    @FXML private MenuItem SignOutBTN;
    @FXML private MenuButton menuBTN;
    @FXML private TableView<AdminUser> dashboardTable;
    @FXML private TableColumn<AdminUser, String> usernameCol, emailCol, lastLogInCol;
    @FXML private Button manageAccBTN, managePlannedDBTN, manageRouteBTN;
    @FXML private Label totalUsersLabel, totalPlannedDrivesLabel, totalRoutesLabel;
    
    // MANAGE ACC PANE
    @FXML private StackPane accountManagerPane;
    @FXML private Button accmngraddUserBTN, back2dashboardBTN, AccMngrUpdateBTN, AccMngrDeleteBTN;
    @FXML private TableView<AdminUser> AccMngrTable;
    @FXML private TableColumn<AdminUser, String> emailCol1, usernameCol1, passwordCol, birthdateCol, firstNameCol, lastNameCol;
    @FXML private TableColumn<AdminUser, Integer> accIDCol;
    @FXML private TextField searchTF;

    // MANAGE ROUTES PANE
    @FXML private StackPane routesManagerPane;
    @FXML private Button routesUpdateBTN, routesDeleteBTN, routesAddBTN;
    @FXML private TableView<AdminRoutes> routesManagerTable;
    @FXML private TableColumn<AdminRoutes, Integer> routeAccIDCol;
    @FXML private TableColumn<AdminRoutes, String> routeIDCol, startPointCol, endPointCol, altRouteCol, stopOverCol, estTimeCol;   ;
    @FXML private TextField routeSearchTF;
 
    // MANAGE PLANNED DRIVES PANE
    @FXML private StackPane plannedDrivesPane;
    @FXML private Button addPlannedDBTN, updatePlannedDBTN, deletePlannedDBTN;
    @FXML private TableView<AdminPlannedDrives> plannedDrivesManagerTable;
    @FXML private TableColumn<AdminPlannedDrives, Integer> pdIDCol,pdAccIDCol ;
    @FXML private TableColumn<AdminPlannedDrives, String> pdStartLocCol, pdPinnedLocCol;
    @FXML private TableColumn<AdminPlannedDrives, LocalDate> pdCalendarCol;
    @FXML private TableColumn<AdminPlannedDrives, LocalTime> pdPlannedTimeCol;
    @FXML private TextField searchPlannedDrivesTF;
 
    private ObservableList<AdminUser> userList = FXCollections.observableArrayList(); //DASHBOARD
    private ObservableList<AdminUser> accountsList = FXCollections.observableArrayList(); //ACCOUNTS
    private ObservableList<AdminRoutes> savedRoutesList = FXCollections.observableArrayList(); //ROUTES
    private ObservableList<AdminPlannedDrives> plannedDrivesList = FXCollections.observableArrayList(); //PLANNED DRIVES

    @FXML
public void initialize() {
    showPane(MainPane); 
    
    // Initialize Dashboard Table
    usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    lastLogInCol.setCellValueFactory(new PropertyValueFactory<>("lastLogIn"));
    dashboardTable.setItems(userList);
    loadDashboardData();

    // 🟡🟡🟡 Initialize Account Manager Table
    accIDCol.setCellValueFactory(new PropertyValueFactory<>("accID"));
    emailCol1.setCellValueFactory(new PropertyValueFactory<>("email"));
    usernameCol1.setCellValueFactory(new PropertyValueFactory<>("username"));
    passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    birthdateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
    firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    AccMngrTable.setItems(accountsList);
    TableEditor.makeTableEditable(emailCol1, usernameCol1, passwordCol, firstNameCol, lastNameCol);
    AccMngrTable.setEditable(true);
    searchTF.textProperty().addListener((observable, oldValue, newValue) -> filterUsers(newValue));
    loadAccountManagerData();
    updateCounters();

    // 🟡🟡🟡 Initialize Routes Table
    routeIDCol.setCellValueFactory(new PropertyValueFactory<>("routeID"));
    routeAccIDCol.setCellValueFactory(new PropertyValueFactory<>("accountID"));       
    startPointCol.setCellValueFactory(new PropertyValueFactory<>("route_startpoint"));
    endPointCol.setCellValueFactory(new PropertyValueFactory<>("route_endpoint"));
    altRouteCol.setCellValueFactory(new PropertyValueFactory<>("alternativeRoute"));
    stopOverCol.setCellValueFactory(new PropertyValueFactory<>("stopOver"));
    estTimeCol.setCellValueFactory(new PropertyValueFactory<>("estTime"));

    // Set ComboBoxTableCell for startPointCol, endPointCol, and stopOverCol
    ObservableList<String> locationOptions = FXCollections.observableArrayList(AddRoutesPopupController.loadLocations());
    startPointCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
    endPointCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
    stopOverCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));

    routesManagerTable.setItems(savedRoutesList);
    routesManagerTable.setEditable(true);
    routeSearchTF.textProperty().addListener((observable, oldValue, newValue) -> filterUsersRoutes(newValue));
    loadRouteManagerData();

    // 🟡🟡🟡 Initialize Planned Drives Table
    pdIDCol.setCellValueFactory(new PropertyValueFactory<>("plannedDriveID"));
    pdAccIDCol.setCellValueFactory(new PropertyValueFactory<>("account_id"));
    pdCalendarCol.setCellValueFactory(new PropertyValueFactory<>("plannedDate"));
    pdPlannedTimeCol.setCellValueFactory(new PropertyValueFactory<>("plannedTime"));
    pdStartLocCol.setCellValueFactory(new PropertyValueFactory<>("startLoc"));
    pdPinnedLocCol.setCellValueFactory(new PropertyValueFactory<>("pinnedLoc"));
    plannedDrivesManagerTable.setItems(plannedDrivesList);
    TableEditor.makeTableEditable(pdPinnedLocCol); // PLANNED TIME AND DATE SHUD BE EDITABLE ASWELL 
    plannedDrivesManagerTable.setEditable(true);
    searchPlannedDrivesTF.textProperty().addListener((observable, oldValue, newValue) -> filterUsersPlannedDrive(newValue));

    loadPlannedDrivesData();
}

    @FXML
    private void refreshDashboardTable() { //DASHBOARD TABLE
        userList.setAll(AdminService.getAllUsers()); 
        dashboardTable.setItems(userList);
    }
    @FXML
    private void loadDashboardData() { //DASHBOARD TABLE
        userList.setAll(AdminService.getAllUsers());
        dashboardTable.setItems(userList);
    }
    @FXML
    private void back2dashboardButtonHandler(ActionEvent event) { 
        showPane(MainPane);
        refreshDashboardTable();
    }
    @FXML
    private void updateCounters() { //DASHBOARD COUNTER
        int[] counts = AdminService.getDashboardCounts();
        totalUsersLabel.setText("Total Users: " + counts[0]);
        totalRoutesLabel.setText("Total Routes: " + counts[1]);
        totalPlannedDrivesLabel.setText("Total Planned Drives: " + counts[2]);
    }
    @FXML
    public void handleSignOut(ActionEvent event) throws IOException { //SIGN OUT BUTTON 
        Stage stage = (Stage) menuBTN.getScene().getWindow();  
        stage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        Stage signInStage = new Stage();
        signInStage.setScene(new Scene(root));
        signInStage.show();
    }


    
    // 🎀🎀🎀 ACCOUNT MANAGER
    @FXML
    private void refreshTableView() {  //REFRESHES ACC TABLE
        AccMngrTable.getItems().clear();
        AccMngrTable.getItems().addAll(AdminService.getAllUsers()); 
    } 
    @FXML
    private void manageAccButtonHandler(ActionEvent event) { // LOADS ACC MANAGER PANE
        showPane(accountManagerPane);
        loadAccountManagerData();
    }
    @FXML
    public void loadAccountManagerData() { //ACCOUNT MANAGER TABLE 
        accountsList.setAll(AdminService.getAllAccounts());
        AccMngrTable.setItems(accountsList);
    }
    @FXML
    private void deleteButtonHandler(ActionEvent event) { //ACC MANAGER - DELETE
        AdminUser selectedUser = AccMngrTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            AdminService.deleteUser(selectedUser.getAccID());
            showAlert("Success", "User deleted successfully!", Alert.AlertType.INFORMATION);
            loadAccountManagerData();
            updateCounters();
            refreshDashboardTable();
        }
    }
    @FXML
    private void handleUpdateButton(ActionEvent event) { //ACC MANAGER - UPDATE
        AdminUser selectedUser = AccMngrTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String newEmail = selectedUser.getEmail();
            if (!AdminService.isValidEmail(newEmail)) {
                showAlert("Invalid Email", "Please enter a valid email address.", Alert.AlertType.WARNING);
                return; 
            }
            boolean success = AdminService.updateUser(selectedUser);
            showAlert(success ? "Success" : "Error",
                    success ? "User details updated successfully!" : "Failed to update user details.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (success) {
                loadAccountManagerData(); // Reload data from the database
            }
        } else {
            showAlert("Warning", "No user selected for update.", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void openAddUserPopup(ActionEvent event) { //ACC MANAGER - ADD
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddUserPopup.fxml"));
            Parent root = loader.load();

            AddUserPopupController addUserPopupController = loader.getController();
            addUserPopupController.setAdminHomepageController(this); // Set the reference

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🎀🎀🎀 ROUTES MANAGER
    @FXML
    private void manageRoutesButtonHandler(ActionEvent event) { // LOADS ROUTES MANAGER PANE
        showPane(routesManagerPane);
        loadRouteManagerData();
    }
    @FXML
    public void loadRouteManagerData() { //ROUTE MANAGER TABLE 
        savedRoutesList.setAll(AdminService.getAllRoutes());
        routesManagerTable.setItems(savedRoutesList);
    } 
    @FXML
    private void openAddRoutesPopup (ActionEvent event) { //ROUTE MANAGER - ADD
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRoutePopup.fxml"));
            Parent root = loader.load();
    
            AddRoutesPopupController addRoutesPopupController = loader.getController();
            addRoutesPopupController.setAdminHomepageController(this);
            addRoutesPopupController.loadUsernames(); // Load user list
    
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void deleteRouteButtonHandler(ActionEvent event) { //ROUTE MANAGER - DELETE
        AdminRoutes selectedRoute = routesManagerTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            AdminService.deleteSavedRoute(selectedRoute.getRouteID());
            showAlert("Success", "Saved Route deleted successfully!", Alert.AlertType.INFORMATION);
            loadRouteManagerData();
        }
    }
    @FXML
    private void handleRouteUpdate(ActionEvent event) { // ROUTE MANAGER - UPDATE
        AdminRoutes selectedRoute = routesManagerTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            
            String newStartPoint = selectedRoute.getRoute_startpoint();
            String newEndPoint = selectedRoute.getRoute_endpoint();
            String newStopOver = selectedRoute.getStopOver();

            if (newStartPoint.isEmpty() || newEndPoint.isEmpty() || newStopOver.isEmpty()) {
                showAlert("Invalid Input", "Start point, end point, and stopover cannot be empty.", Alert.AlertType.WARNING);
                return;
            }

            boolean success = AdminService.updateRoute(selectedRoute);
            showAlert(success ? "Success" : "Error",
                    success ? "Route details updated successfully!" : "Failed to update route details.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (success) {
                loadRouteManagerData();
                routesManagerTable.refresh();
            }
        } else {
            showAlert("Warning", "No route selected for update.", Alert.AlertType.WARNING);
        }
    }

    // 🎀🎀🎀 PLANNED DRIVES
    @FXML
    private void managePlannedDrivesButtonHandler(ActionEvent event) { 
        showPane(plannedDrivesPane);
    }
    @FXML
    void loadPlannedDrivesData() {
        plannedDrivesList.setAll(AdminService.displayPlannedDrives());
        plannedDrivesManagerTable.setItems(plannedDrivesList);
    }
    
    @FXML
    private void openAddPlannedDrivePopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddPlannedDrivePopup.fxml"));
            Parent root = loader.load();
    
            AddPlannedDrivePopupController addPlannedDrivePopupController = loader.getController();
            addPlannedDrivePopupController.setAdminHomepageController(this); // Set the reference
            addPlannedDrivePopupController.loadAccountIDs(); // Load account IDs
    
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void deletePlannedDriveButtonHandler(ActionEvent event) {
        AdminPlannedDrives selectedDrive = plannedDrivesManagerTable.getSelectionModel().getSelectedItem();
        if (selectedDrive != null) {
            boolean success = AdminService.deletePlannedDrive(selectedDrive.getPlannedDriveID());
            if (success) {
                showAlert("Success", "Planned drive deleted successfully!", Alert.AlertType.INFORMATION);
                loadPlannedDrivesData();
            } else {
                showAlert("Error", "Failed to delete planned drive. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "No planned drive selected for deletion.", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void handlePlannedDriveUpdate(ActionEvent event) {
        AdminPlannedDrives selectedDrive = plannedDrivesManagerTable.getSelectionModel().getSelectedItem();
        if (selectedDrive != null) {
            // Assuming you want to update the pinned location of the planned drive
            String newPinnedLoc = selectedDrive.getPinnedLoc();

            // Validate the new pinned location if necessary
            if (newPinnedLoc.isEmpty()) {
                showAlert("Invalid Input", "Pinned location cannot be empty.", Alert.AlertType.WARNING);
                return;
            }

            // Update the planned drive in the database
            boolean success = AdminService.updatePlannedDrive(selectedDrive);
            showAlert(success ? "Success" : "Error",
                    success ? "Planned drive details updated successfully!" : "Failed to update planned drive details.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (success) {
                loadPlannedDrivesData(); // Reload data from the database
            }
        } else {
            showAlert("Warning", "No planned drive selected for update.", Alert.AlertType.WARNING);
        }
    }

    private void filterUsers(String searchText) { // 🔍 SEARCH FUNCTION IN ACC MANAGER
        ObservableList<AdminUser> filteredList = accountsList.filtered(user ->
            user.getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
            user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
            user.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
            user.getLastName().toLowerCase().contains(searchText.toLowerCase())
        );
        AccMngrTable.setItems(filteredList);
    }

    private void filterUsersRoutes(String searchUser) { // 🔍 SEARCH FUNCTION IN ROUTES MANAGER
        ObservableList<AdminRoutes> filteredList = savedRoutesList.filtered(user ->
            String.valueOf(user.getAccountID()).contains(searchUser)
        );
        routesManagerTable.setItems(filteredList);
    }
    
    private void filterUsersPlannedDrive (String searchID) { // 🔍 SEARCH FUNCTION IN ROUTES MANAGER
        ObservableList<AdminPlannedDrives> filteredList = plannedDrivesList.filtered(user ->
            String.valueOf(user.getPlannedDriveID()).contains(searchID)
        );
        plannedDrivesManagerTable.setItems(filteredList);
    }

    private void showPane(StackPane paneToShow) { 
        MainPane.setVisible(false);
        accountManagerPane.setVisible(false);
        plannedDrivesPane.setVisible(false);
        routesManagerPane.setVisible(false);
        paneToShow.setVisible(true);    
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}