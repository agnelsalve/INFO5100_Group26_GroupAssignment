
package edu.univ.acl;

import edu.univ.repo.DataStore;

public class AuthManager {
    private static UserAccount current;

    public static boolean login(String email, String password) {
        var ds = DataStore.get();
        var ua = ds.getUserByEmail(email);
        if (ua != null && ua.getPassword().equals(password)) {
            current = ua;
            return true;
        }
        current = null;
        return false;
    }
    public static void logout() { current = null; }
    public static UserAccount current() { return current; }
    public static boolean require(Role role) {
        return current != null && current.getRole() == role;
    }
}
