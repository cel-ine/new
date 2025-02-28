import java.sql.Date;
import java.sql.Time;

public class UserPlannedDrives {
    private int plannedDriveID;
    private int accountId;

    private Date plannedDate;
    private Time plannedTime;

    private String startLoc;
    private String pinnedLoc;

    public UserPlannedDrives(int plannedDriveID, int accountId, Date plannedDate, Time plannedTime,
            String startLoc, String pinnedLoc) {
        this.plannedDriveID = plannedDriveID;
        this.accountId = accountId;

        this.plannedDate = plannedDate;
        this.plannedTime = plannedTime;
        this.startLoc = startLoc;
        this.pinnedLoc = pinnedLoc;
    }

    // Getters and setters for each field
    public int getPlannedDriveID() {
        return plannedDriveID;
    }

    public void setPlannedDriveID(int plannedDriveID) {
        this.plannedDriveID = plannedDriveID;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Time getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(Time plannedTime) {
        this.plannedTime = plannedTime;
    }

    public String getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(String startLoc) {
        this.startLoc = startLoc;
    }

    public String getPinnedLoc() {
        return pinnedLoc;
    }

    public void setPinnedLoc(String pinnedLoc) {
        this.pinnedLoc = pinnedLoc;
    }
}