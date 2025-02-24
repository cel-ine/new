public class AdminTravelTime {
    private String travelTimeID;
    private String routeID;
    private String estimatedTime;
    
    public void AdminUser (String travelTimeID, String routeID, String estimatedTime) {
        this.travelTimeID = travelTimeID;
        this.routeID = routeID;
        this.estimatedTime = estimatedTime;
    }
    public String gettravelTimeID() {
        return travelTimeID;
    }
    public void settravelTimeID(String travelTimeID) {
        this.travelTimeID = travelTimeID;
    }
    ////////
    public String getrouteID() {
        return routeID;
    }
     public void setaltRouteID(String routeID) {
    this.routeID = routeID;
    }
    ////////
    public String getestimatedTime() {
        return estimatedTime;
    }
    public void setestimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}