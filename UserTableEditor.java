import java.sql.Date;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.scene.control.DatePicker;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            TableColumn<UserRouteDetails, String> altRoutesCol,
            TableColumn<UserRouteDetails, String> stopOverRouteLoc,
            TableColumn<UserRouteDetails, Time> estTimeCol) {

        // Enable text editing for String-based columns
        makeStringColumnEditable(startCol, "startPoint", editedRoutes);
        makeStringColumnEditable(endCol, "endPoint", editedRoutes);
        makeStringColumnEditable(altRoutesCol, "alternativeRoutes", editedRoutes);
        makeStringColumnEditable(stopOverRouteLoc, "stopOverLocation", editedRoutes);

        // ✅ Enable editing for Time column with a custom converter
        estTimeCol.setCellFactory(TextFieldTableCell.forTableColumn(new TimeStringConverter()));
        estTimeCol.setOnEditCommit(event -> {
            UserRouteDetails route = event.getRowValue();
            Time newTime = event.getNewValue(); // Already converted using TimeStringConverter
            if (newTime != null) {
                route.setEstimatedTime(newTime);
                editedRoutes.add(route);
            }
        });
    }

    // planCalendar.setCellValueFactory(new PropertyValueFactory<>("plannedDate"));
    // planTime.setCellValueFactory(new PropertyValueFactory<>("plannedTime"));
    // startLocCol.setCellValueFactory(new PropertyValueFactory<>("startLoc"));
    // pinnedLocCol.setCellValueFactory(new PropertyValueFactory<>("pinnedLoc"));
    // planneddrivesTable.setItems(plannedList);
    // // UserTableEditor.makePlannedDrivesTableEditable(planCalendar, planTime,
    // // startLocCol, pinnedLocCol);
    // // planneddrivesTable.setEditable(true);

    public static void makePlannedDrivesTableEditable(
            TableColumn<UserPlannedDrives, String> startLocCol,
            TableColumn<UserPlannedDrives, String> pinnedLocCol,
            TableColumn<UserPlannedDrives, Time> planTime,
            TableColumn<UserPlannedDrives, Date> planCalendar) {

        // Enable text editing for String-based columns
        makeStringColumnEditable(startLocCol, "startLoc", editedPlannedDrives);
        makeStringColumnEditable(pinnedLocCol, "pinnedLoc", editedPlannedDrives);

        // Enable time editing
        // planTime.setCellFactory(TextFieldTableCell
        // .forTableColumn(new
        // LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm:ss"), null)));
        // planTime.setOnEditCommit(event -> {
        // UserPlannedDrives route = event.getRowValue();
        // Time newTime = event.getNewValue();
        // if (newTime != null) {
        // route.setPlannedTime(newTime);
        // editedPlannedDrives.add(route);
        // }
        // });

        // // Enable date editing using DatePicker
        // planCalendar.setCellFactory(column -> new
        // javafx.scene.control.TableCell<UserPlannedDrives, LocalDate>() {
        // private final DatePicker datePicker = new DatePicker();

        // {
        // datePicker.setOnAction(event -> {
        // commitEdit(datePicker.getValue());
        // });
        // setGraphic(datePicker);
        // setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        // }

        // @Override
        // protected void updateItem(LocalDate item, boolean empty) {
        // super.updateItem(item, empty);
        // if (empty) {
        // setGraphic(null);
        // } else {
        // datePicker.setValue(item);
        // setGraphic(datePicker);
        // }
        // }
        // });

        // planCalendar.setOnEditCommit(event -> {
        // UserPlannedDrives route = event.getRowValue();
        // LocalDate newDate = event.getNewValue();
        // if (newDate != null) {
        // route.setPlannedDate(Date.valueOf(newDate));
        // editedPlannedDrives.add(route);
        // }
        // });
    }

    // ===================== STRING COLUMN HELPER =====================
    private static <T> void makeStringColumnEditable(TableColumn<T, String> column, String fieldName,
            Set<T> editedSet) {
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            T item = event.getRowValue();
            try {
                item.getClass().getMethod("set" + capitalize(fieldName), String.class).invoke(item,
                        event.getNewValue());
                editedSet.add(item);
            } catch (Exception e) {
                System.err.println("❌ Error updating field '" + fieldName + "': " + e.getMessage());
            }
        });
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // ===================== GET & CLEAR EDITED DATA =====================
    public static Set<UserRouteDetails> getEditedRoutes() {
        return editedRoutes;
    }

    public static void clearEditedRoutes() {
        editedRoutes.clear();
    }
}
