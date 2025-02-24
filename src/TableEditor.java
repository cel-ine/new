import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.Set;
import java.util.HashSet;

public class TableEditor {
    private static final Set<AdminUser> editedUsers = new HashSet<>();
    private static final Set<AdminRoutes> editedRoutes = new HashSet<>();
    private static final Set<AdminPlannedDrives> editedPlannedDrives = new HashSet<>();

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

    public static void makeTableEditable(TableColumn<AdminRoutes, String> routeStartCol,
                                         TableColumn<AdminRoutes, String> routeEndCol) {
        // Enable text editing for specified columns
        routeStartCol.setCellFactory(TextFieldTableCell.forTableColumn());
        routeEndCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // Track changes
        routeStartCol.setOnEditCommit(event -> {
            AdminRoutes routes = event.getRowValue();
            routes.setRoute_startpoint(event.getNewValue());
            editedRoutes.add(routes);
        });

        routeEndCol.setOnEditCommit(event -> {
            AdminRoutes routes = event.getRowValue();
            routes.setRoute_endpoint(event.getNewValue());
            editedRoutes.add(routes);
        });
    }

    public static Set<AdminRoutes> getEditedRoutes() {
        return editedRoutes;
    }

    public static void makeTableEditable(TableColumn<AdminPlannedDrives, String> pdPinnedLocCol) {
        // Enable text editing for specified columns
        pdPinnedLocCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // Track changes
        pdPinnedLocCol.setOnEditCommit(event -> {
            AdminPlannedDrives plannedDrive = event.getRowValue();
            plannedDrive.setPinnedLoc(event.getNewValue());
            editedPlannedDrives.add(plannedDrive);
        });
    }

    public static Set<AdminPlannedDrives> getEditedPlannedDrives() {
        return editedPlannedDrives;
    }
}