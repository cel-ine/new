public class AdminUser {
    private static AdminUser currentUser;
    private int acc_id;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String birthday;
    private String lastLogIn; 
    private String oldUsername;

    // Constructor for regular users
    public AdminUser(int acc_id, String email, String username, String password, String birthday,  String firstname, String lastname ) {
        this.acc_id = acc_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }
    public AdminUser(String email, String username, String password, String birthday,  String firstname, String lastname ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    public AdminUser(String username, String email, String lastLogIn) {
        this.username = username;
        this.email = email;
        this.lastLogIn = lastLogIn;
    }

    public static AdminUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(AdminUser user) {
        currentUser = user;
    }

    public int getAccID() { 
        return acc_id;
    }
    
    public void setAccID(int acc_id) {
        this.acc_id = acc_id;
    }
    
    public String getLastLogIn() { 
        return lastLogIn;
    }
    
    public void setLastLogIn(String lastLogIn) {
        this.lastLogIn = lastLogIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthDate() {
        return birthday;
    }

    public void setBirthDate(String birthday) {
        this.birthday = birthday;
    }
    public String getOldUsername() {
        return oldUsername;
    }
    
    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

}
