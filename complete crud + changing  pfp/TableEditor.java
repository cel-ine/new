import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;

public class TableEditor {
    private static final Set<AdminUser> editedUsers = new HashSet<>();
    private static final Set<AdminRoutes> editedRoutes = new HashSet<>();
    private static final Set<AdminPlannedDrives> editedPlannedDrives = new HashSet<>();

    //ACCOUNTS
    public static void makeTableEditable(TableColumn<AdminUser, String> emailCol1,
                                         TableColumn<AdminUser, String> usernameCol1,
                                         TableColumn<AdminUser, String> passwordCol,
                                         TableColumn<AdminUser, String> firstNameCol,
                                         TableColumn<AdminUser, String> lastNameCol) {

        // Enable text editing for specified columns
        emailCol1.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameCol1.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // Track changes
        emailCol1.setOnEditCommit(event -> {
            AdminUser user = event.getRowValue();
            user.setEmail(event.getNewValue());
            editedUsers.add(user);
        });

        usernameCol1.setOnEditCommit(event -> {
            AdminUser user = event.getRowValue();
            user.setUsername(event.getNewValue());
            editedUsers.add(user);
        });

        passwordCol.setOnEditCommit(event -> {
            AdminUser user = event.getRowValue();
            user.setPassword(event.getNewValue());
            editedUsers.add(user);
        });

        firstNameCol.setOnEditCommit(event -> {
            AdminUser user = event.getRowValue();
            user.setFirstName(event.getNewValue());
            editedUsers.add(user);
        });

        lastNameCol.setOnEditCommit(event -> {
            AdminUser user = event.getRowValue();
            user.setLastName(event.getNewValue());
            editedUsers.add(user);
        });
    }

    public static Set<AdminUser> getEditedUsers() {
        return editedUsers;
    }
    // ROUTES
    public static void makeRoutesTableEditable(
            TableColumn<AdminRoutes, String> startPointCol,
            TableColumn<AdminRoutes, String> endPointCol,
            TableColumn<AdminRoutes, String> stopOverCol) {

        ObservableList<String> locationOptions = FXCollections.observableArrayList(AdminService.getAllLocations());
        ObservableList<String> stopOverOptions = FXCollections.observableArrayList(AdminService.getAllStopovers());

        
        startPointCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
        endPointCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
        stopOverCol.setCellFactory(ComboBoxTableCell.forTableColumn(stopOverOptions));

        
        startPointCol.setOnEditCommit(event -> {
            AdminRoutes route = event.getRowValue();
            route.setRoute_startpoint(event.getNewValue());
            editedRoutes.add(route); 
        });

        endPointCol.setOnEditCommit(event -> {
            AdminRoutes route = event.getRowValue();
            route.setRoute_endpoint(event.getNewValue());
            editedRoutes.add(route); 
        });

        stopOverCol.setOnEditCommit(event -> {
            AdminRoutes route = event.getRowValue();
            route.setStopOver(event.getNewValue());
            editedRoutes.add(route);
        });
    }

    public static Set<AdminRoutes> getEditedRoutes() {
        return editedRoutes;
    }

    public static void clearEditedRoutes() {
        editedRoutes.clear();
    }

    // 🟢 Planned Drives Table (Editable: DatePicker + Time TextField + ComboBoxes)
    public static void makePlannedDrivesTableEditable(
            TableColumn<AdminPlannedDrives, String> pdPinnedLocCol,
            TableColumn<AdminPlannedDrives, String> pdStartLocCol,
            TableColumn<AdminPlannedDrives, LocalDate> pdCalendarCol,
            TableColumn<AdminPlannedDrives, LocalTime> pdPlannedTimeCol) {

        ObservableList<String> locationOptions = FXCollections.observableArrayList(AdminService.getAllLocations());

        // ✅ Location ComboBox columns
        pdPinnedLocCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
        pdStartLocCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));

        // 📅 DatePicker column (Planned Date)
        pdCalendarCol.setCellFactory(column -> new TableCell<AdminPlannedDrives, LocalDate>() {
            private final DatePicker datePicker = new DatePicker();
        
            {
                datePicker.setOnAction(event -> {
                    LocalDate newDate = datePicker.getValue();
                    commitEdit(newDate); // 🔥 Key line to commit the edit
                    updateModel(newDate);
                });
            }
        
            private void updateModel(LocalDate newDate) {
                AdminPlannedDrives drive = getTableView().getItems().get(getIndex());
                drive.setPlannedDate(newDate);  // ✅ Update model immediately
                editedPlannedDrives.add(drive);
            }
        
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setGraphic(null);
                } else {
                    datePicker.setValue(date);
                    setGraphic(datePicker);
                }
            }
        
            @Override
            public void startEdit() {
                super.startEdit();
                if (!isEmpty()) {
                    datePicker.requestFocus();  // ✅ Focus picker on edit
                }
            }
        });
        

        // ⏰ Time TextField column (Planned Time) with validation
        pdPlannedTimeCol.setCellFactory(column -> new TableCell<AdminPlannedDrives, LocalTime>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> validateAndCommitTime());
                textField.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                    if (!newFocus) validateAndCommitTime();
                });
            }

            private void validateAndCommitTime() {
                try {
                    LocalTime newTime = LocalTime.parse(textField.getText());
                    commitEdit(newTime);
                    updateModel(newTime);
                } catch (DateTimeParseException e) {
                    textField.setText(getItem() != null ? getItem().toString() : "");
                }
            }

            private void updateModel(LocalTime newTime) {
                AdminPlannedDrives drive = getTableView().getItems().get(getIndex());
                drive.setPlannedTime(newTime);
                editedPlannedDrives.add(drive);
            }

            @Override
            protected void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setGraphic(null);
                } else {
                    textField.setText(time.toString());
                    setGraphic(textField);
                }
            }
        });

        // 🔄 On edit commit handlers
        pdPinnedLocCol.setOnEditCommit(event -> {
            AdminPlannedDrives drive = event.getRowValue();
            drive.setPinnedLoc(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        pdStartLocCol.setOnEditCommit(event -> {
            AdminPlannedDrives drive = event.getRowValue();
            drive.setStartLoc(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        pdPlannedTimeCol.setOnEditCommit(event -> {
            AdminPlannedDrives drive = event.getRowValue();
            drive.setPlannedTime(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        pdCalendarCol.setOnEditCommit(event -> {
            AdminPlannedDrives drive = event.getRowValue();
            drive.setPlannedDate(event.getNewValue());
            editedPlannedDrives.add(drive);
        });
    }


    public static Set<AdminPlannedDrives> getEditedPlannedDrives() {
    return editedPlannedDrives;
    }
}