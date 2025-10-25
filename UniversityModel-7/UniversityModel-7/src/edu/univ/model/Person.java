
package edu.univ.model;

public abstract class Person {
    protected final String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String departmentId;

    public Person(String id, String firstName, String lastName, String email, String departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();
        this.departmentId = departmentId;
    }
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getDepartmentId() { return departmentId; }
    public void setFirstName(String v) { firstName = v; }
    public void setLastName(String v) { lastName = v; }
    public void setEmail(String v) { email = v.toLowerCase(); }
    public void setDepartmentId(String v) { departmentId = v; }
    public String fullName(){ return firstName + " " + lastName; }
}
