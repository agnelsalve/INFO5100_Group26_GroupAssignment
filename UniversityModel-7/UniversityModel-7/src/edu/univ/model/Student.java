
package edu.univ.model;

import java.util.*;

public class Student extends Person {
    private final String program = "MSIS";
    private final Account account = new Account();
    private final Map<String, List<GradeRecord>> transcript = new HashMap<>();
    private int currentTermCredits = 0;

    public Student(String id, String fn, String ln, String email, String dept) {
        super(id, fn, ln, email, dept);
    }
    public Account getAccount(){ return account; }
    public Map<String,List<GradeRecord>> getTranscript(){ return transcript; }
    public int getCurrentTermCredits(){ return currentTermCredits; }
    public void setCurrentTermCredits(int c){ currentTermCredits = c; }
    public String getProgram(){ return program; }
}
