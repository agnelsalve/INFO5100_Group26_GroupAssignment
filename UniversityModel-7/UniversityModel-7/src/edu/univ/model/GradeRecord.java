package edu.univ.model;

/**
 * Represents one graded course record for a student in a given term.
 * Used by Student, DataStore, GpaUtil, and StudentPanel.
 */
public class GradeRecord {

    private final String term;
    private final String courseOfferId;
    private final String courseId;
    private final int credits;
    private double totalPercent = 0.0;
    private String letter = "IP"; // default: In Progress

    public GradeRecord(String term, String courseOfferId, String courseId, int credits) {
        this.term = term;
        this.courseOfferId = courseOfferId;
        this.courseId = courseId;
        this.credits = credits;
    }

    // ----- Getters -----
    public String getTerm() { return term; }
    public String getCourseOfferId() { return courseOfferId; }
    public String getCourseId() { return courseId; }
    public int getCredits() { return credits; }
    public double getTotalPercent() { return totalPercent; }
    public String getLetter() { return letter; }

    // ----- Setters -----
    public void setTotalPercent(double totalPercent) {
        this.totalPercent = totalPercent;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s - %s (%.1f%%)",
                term, courseId, courseOfferId, letter, totalPercent);
    }
}
