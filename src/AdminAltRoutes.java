public class AdminAltRoutes {
        private String altRouteID;
        private String routeID;
        private String altRoutes;
        private String stopOverLoc;
      
    
        public void AdminUser (String altRouteID, String routeID, String altRoutes, String stopOverLoc) {
            this.altRouteID = altRouteID;
            this.routeID = routeID;
            this.altRoutes = altRoutes;
            this.stopOverLoc = stopOverLoc;
        }
        public String getrouteID() {
            return routeID;
        }
        public void setrouteID(String routeID) {
            this.routeID = routeID;
        }
        ////////
        public String getaltRouteID() {
            return altRouteID;
        }
         public void setaltRouteID(String altRouteID) {
        this.altRouteID = altRouteID;
        }
        ////////
        public String getaltRoutes() {
            return altRoutes;
        }
        public void setaltRoutes(String altRoutes) {
            this.altRoutes = altRoutes;
        }
        ////////
        public String getstopOverLoc() {
            return stopOverLoc;
        }
        public void setstopOverLoc(String stopOverLoc) {
            this.stopOverLoc = stopOverLoc;
        }
    }
        