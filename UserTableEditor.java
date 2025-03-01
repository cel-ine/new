import java.sql.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.scene.control.DatePicker;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

//modify
public class UserTableEditor {
    private static final Set<UserRouteDetails> editedRoutes = new HashSet<>();
    private static final Set<UserPlannedDrives> editedPlannedDrives = new HashSet<>();

    // ===================== USER ROUTE DETAILS (Routes Table) =====================
    public static void makeRouteTableEditable(
            TableColumn<UserRouteDetails, String> startCol,
            TableColumn<UserRouteDetails, String> endCol,
            //TableColumn<UserRouteDetails, String> altRoutesCol, ALT ROUTES SHOULD NOT BE EDITED BC ITS GENERATED
            TableColumn<UserRouteDetails, String> stopOverRouteLoc) {

        ObservableList<String> locationOptions = FXCollections.observableArrayList(AdminService.getAllLocations());
        ObservableList<String> stopOverOptions = FXCollections.observableArrayList(AdminService.getAllStopovers());

        startCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
        endCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
        stopOverRouteLoc.setCellFactory(ComboBoxTableCell.forTableColumn(stopOverOptions));

        startCol.setOnEditCommit(event -> {
            UserRouteDetails route = event.getRowValue();
            route.setStartPoint(event.getNewValue());
            editedRoutes.add(route); 
        });

        endCol.setOnEditCommit(event -> {
            UserRouteDetails route = event.getRowValue();
            route.setEndPoint(event.getNewValue());
            editedRoutes.add(route); 
        });

        stopOverRouteLoc.setOnEditCommit(event -> {
            UserRouteDetails route = event.getRowValue();
            route.setAlternativeRoutes(event.getNewValue());
            editedRoutes.add(route);
        });
        }
     // ===================== USER PLANNED DRIVES DETAILS (Planned Drives Table) =====================
    public static void makePlannedDrivesTableEditable(
        TableColumn<UserPlannedDrives, String> startLocCol,
        TableColumn<UserPlannedDrives, String> pinnedLocCol,
        TableColumn<UserPlannedDrives, Time> planTime,
        TableColumn<UserPlannedDrives, Date> planCalendar) {

            ObservableList<String> locationOptions = FXCollections.observableArrayList(AdminService.getAllLocations());
   
            startLocCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));
            pinnedLocCol.setCellFactory(ComboBoxTableCell.forTableColumn(locationOptions));

      
        planCalendar.setCellFactory(column -> new TableCell<UserPlannedDrives, Date>() {
            private final DatePicker datePicker = new DatePicker();
        
            {
                datePicker.setOnAction(event -> {
                    LocalDate newDate = datePicker.getValue();
                    commitEdit(Date.valueOf(newDate)); 
                    updateModel(newDate);
                });
            }
        
            private void updateModel(LocalDate newDate) {
                UserPlannedDrives drive = getTableView().getItems().get(getIndex());
                drive.setPlannedDate(Date.valueOf(newDate));  
                editedPlannedDrives.add(drive);
            }
        
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setGraphic(null);
                } else {
                    datePicker.setValue(date.toLocalDate());
                    setGraphic(datePicker);
                }
            }
        
            @Override
            public void startEdit() {
                super.startEdit();
                if (!isEmpty()) {
                    datePicker.requestFocus();  
                }
            }
        });


        planTime.setCellFactory(column -> new TableCell<UserPlannedDrives, Time>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> validateAndCommitTime());
                textField.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
                    if (!newFocus) validateAndCommitTime();
                });
            }

            private void validateAndCommitTime() {
    try {
        String timeText = textField.getText().trim();
        
   
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(timeText, formatter);

        Time newTime = Time.valueOf(localTime);

        commitEdit(newTime);
        updateModel(newTime);
    } catch (DateTimeParseException e) {
        textField.setText(getItem() != null ? getItem().toString() : "");
    }
}

        private void updateModel(Time newTime) {
            UserPlannedDrives drive = getTableView().getItems().get(getIndex());
            drive.setPlannedTime(newTime);
            editedPlannedDrives.add(drive);
        }

         @Override
        protected void updateItem(Time time, boolean empty) {
            super.updateItem(time, empty);
            if (empty || time == null) {
                    setGraphic(null);
            } else {
                textField.setText(time.toString());
                setGraphic(textField);
            }
        }
    });
  
        startLocCol.setOnEditCommit(event -> {
            UserPlannedDrives drive = event.getRowValue();
            drive.setStartLoc(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        pinnedLocCol.setOnEditCommit(event -> {
            UserPlannedDrives drive = event.getRowValue();
            drive.setPinnedLoc(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        planTime.setOnEditCommit(event -> {
            UserPlannedDrives drive = event.getRowValue();
            drive.setPlannedTime(event.getNewValue());
            editedPlannedDrives.add(drive);
        });

        planCalendar.setOnEditCommit(event -> {
            UserPlannedDrives drive = event.getRowValue();
            drive.setPlannedDate(event.getNewValue());
            editedPlannedDrives.add(drive);
        });
    }
    // // ===================== STRING COLUMN HELPER =====================
    // private static <T> void makeStringColumnEditable(TableColumn<T, String> column, String fieldName,
    //         Set<T> editedSet) {
    //     column.setCellFactory(TextFieldTableCell.forTableColumn());
    //     column.setOnEditCommit(event -> {
    //         T item = event.getRowValue();
    //         try {
    //             item.getClass().getMethod("set" + capitalize(fieldName), String.class).invoke(item,
    //                     event.getNewValue());
    //             editedSet.add(item);
    //         } catch (Exception e) {
    //             System.err.println("❌ Error updating field '" + fieldName + "': " + e.getMessage());
    //         }
    //     });
    // }

    // private static String capitalize(String str) {
    //     return str.substring(0, 1).toUpperCase() + str.substring(1);
    // }

    // ===================== GET & CLEAR EDITED DATA =====================
    public static Set<UserRouteDetails> getEditedRoutes() {
        return editedRoutes;
    }
    public static void clearEditedRoutes() {
        editedRoutes.clear();
    }
}
