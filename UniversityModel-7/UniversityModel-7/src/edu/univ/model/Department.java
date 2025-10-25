
package edu.univ.model;

public class Department {
    private final String id;
    private String name;

    public Department(String id, String name) {
        this.id = id; this.name = name;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    @Override public String toString() { return name; }
}
