import java.sql.Date;



public class UserAccount {
   

        private static UserAccount currentUser; // Stores the logged-in user
    
        private int accountId;
        private String email;
        private String username;
        private String password;
        private Date birthdate;
        private String firstName;
        private String lastName;
        private String lastLogin; // Stored as a string or datetime
    
        // Constructor
        public void UserAccount(int accountId, String email, String username, String password, Date birthdate, String firstName, String lastName, String lastLogin) {
            this.accountId = accountId;
            this.email = email;
            this.username = username;
            this.password = password;
            this.birthdate = birthdate;
            this.firstName = firstName;
            this.lastName = lastName;
            this.lastLogin = lastLogin;
        }
        public UserAccount(String email, String username, String password, Date birthdate,  String firstName, String lastName ) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthdate = birthdate;
        }
        public UserAccount (int accountId, String email, String username, String password, Date birthdate,  String firstName, String lastName ){
            this.accountId = accountId;
            this.email = email;
            this.username = username;
            this.password = password;
            this.birthdate = birthdate;
            this.firstName = firstName;
            this.lastName = lastName;

        }
        public int getAccID() {
            return accountId;
        }
    
        public String getEmail() {
            return email;
        }
    
        public String getUsername() {
            return username;
        }
    
        public String getPassword() {
            return password;
        }
    
        public Date getBirthdate() {
            return birthdate;
        }
    
        public String getFirstName() {
            return firstName;
        }
    
        public String getLastName() {
            return lastName;
        }
    
        public String getLastLogin() {
            return lastLogin;
        }
    
        // Setters
        public void setEmail(String email) {
            this.email = email;
        }
    
        public void setUsername(String username) {
            this.username = username;
        }
    
        public void setPassword(String password) {
            this.password = password;
        }
    
        public void setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
        }
    
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    
        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }
    
        // Current User Session Management
        public static void setCurrentUser(UserAccount user) {
            currentUser = user;
        }
    
        public static UserAccount getCurrentUser() {
            return currentUser;
        }
    
        public static void clearCurrentUser() {
            currentUser = null; // Logs out the user
        }
    }
    