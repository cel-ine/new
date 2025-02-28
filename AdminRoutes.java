
public class AdminRoutes {
    private String routeID;
    private int accountID;
    private String route_startpoint;
    private String route_endpoint;
    private String alternativeRoute;
    private String stopOver;
    private String estTime;

    // Constructor
    public AdminRoutes(String routeID, int accountID, String route_startpoint, String route_endpoint, 
                       String alternativeRoute, String stopOver, String estTime) {
        this.routeID = routeID;
        this.accountID = accountID;
        this.route_startpoint = route_startpoint;
        this.route_endpoint = route_endpoint;
        this.alternativeRoute = alternativeRoute;
        this.stopOver = stopOver;
        this.estTime = estTime;
    }
    public AdminRoutes(int accountID, String route_startpoint, String route_endpoint, String alternativeRoute, String stopOver) {
        this.accountID = accountID;
        this.route_startpoint = route_startpoint;
        this.route_endpoint = route_endpoint;
        this.alternativeRoute = alternativeRoute;
        this.stopOver = stopOver;
        this.routeID = RouteIDGenerator.generateRouteID(route_startpoint, route_endpoint);
}


    // Getters and setters
    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getRoute_startpoint() {
        return route_startpoint;
    }

    public void setRoute_startpoint(String route_startpoint) {
        this.route_startpoint = route_startpoint;
    }

    public String getRoute_endpoint() {
        return route_endpoint;
    }

    public void setRoute_endpoint(String route_endpoint) {
        this.route_endpoint = route_endpoint;
    }

    public String getAlternativeRoute() {
        return alternativeRoute;
    }

    public void setAlternativeRoute(String alternativeRoute) {
        this.alternativeRoute = alternativeRoute;
    }

    public String getStopOver() {
        return stopOver;
    }

    public void setStopOver(String stopOver) {
        this.stopOver = stopOver;
    }

    public String getEstTime() {
        return estTime;
    }

    public void setEstTime(String estTime) {
        this.estTime = estTime;
    }
}