import java.sql.Time;

public class UserRouteDetails {
    private int accountId;
    private String routeId;
    private String startPoint;
    private String endPoint;
    private String alternativeRoutes;
    private String stopOverLocation;
    private String estimatedTime;

    public UserRouteDetails(int accountId, String routeId, String startPoint, String endPoint, String alternativeRoutes,
            String stopOverLocation, String estimatedTime) {
        this.accountId = accountId;
        this.routeId = routeId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.alternativeRoutes = alternativeRoutes;
        this.stopOverLocation = stopOverLocation;
        this.estimatedTime = estimatedTime;
    }
    public UserRouteDetails(String startPoint, String endPoint, String alternativeRoutes,
    String stopOverLocation, String estimatedTime) {
    // this.accountId = accountId;
    // this.routeId = routeId;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.alternativeRoutes = alternativeRoutes;
    this.stopOverLocation = stopOverLocation;
    this.estimatedTime = estimatedTime;
    }
    //🌷
    public UserRouteDetails(int accountId, String startPoint, String endPoint, String alternativeRoutes, String stopOverLocation) {
        this.accountId = accountId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.alternativeRoutes = alternativeRoutes;
        this.stopOverLocation = stopOverLocation;
        this.routeId = RouteIDGenerator.generateRouteID(startPoint, endPoint);
}
   

    // Getters and setters for each field
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAlternativeRoutes() {
        return alternativeRoutes;
    }

    public void setAlternativeRoutes(String alternativeRoutes) {
        this.alternativeRoutes = alternativeRoutes;
    }

    public String getStopOverLocation() {
        return stopOverLocation;
    }

    public void setStopOverLocation(String stopOverLocation) {
        this.stopOverLocation = stopOverLocation;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}