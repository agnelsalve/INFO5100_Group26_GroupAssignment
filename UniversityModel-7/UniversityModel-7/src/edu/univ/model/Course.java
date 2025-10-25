
package edu.univ.model;

public class Course {
    private final String id;
    private String name;
    private int credits;

    public Course(String id, String name, int credits){
        this.id = id; this.name = name; this.credits = credits;
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public int getCredits(){ return credits; }
    public void setName(String n){ name = n; }
    public void setCredits(int c){ credits = c; }
}
