
package edu.univ.acl;

import java.util.Objects;

public class UserAccount {
    private final String email;
    private String password; // demo only
    private final Role role;
    private final String personId;

    public UserAccount(String email, String password, Role role, String personId) {
        this.email = email.toLowerCase();
        this.password = password;
        this.role = role;
        this.personId = personId;
    }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public Role getRole() { return role; }
    public String getPersonId() { return personId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        return email.equals(that.email);
    }
    @Override public int hashCode() { return Objects.hash(email); }
}
