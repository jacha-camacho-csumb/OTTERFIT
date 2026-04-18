package model;

/**
 * User
 * <p>
 * Description:
 *  Simple data model representing an application user.
 *  Holds basic user information retrieved from or stored in the database.
 *
 *  This class is intentionally lightweight and is used to pass user data
 *  between the database layer and the UI without adding business logic.
 *
 *  NOTE: Intentionally left very simple i.e. no getters/setters
 * @author rcwav
 * @since 4/18/2026
 */
public class User {
    public final int userId;
    public final String username;
    public final String email;

    public User(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public static boolean isValid(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;
        if (email == null || !email.contains("@")) return false;
        return true;
    }
}
