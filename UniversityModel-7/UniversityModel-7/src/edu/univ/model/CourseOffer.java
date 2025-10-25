
package edu.univ.model;

import java.util.*;

public class CourseOffer {
    private final String id;
    private final String courseId;
    private String facultyId;
    private final String semesterCode;
    private String schedule;
    private int capacity;
    private boolean enrollmentOpen = true;
    private String syllabus = "";
    private final List<String> enrolledStudentIds = new ArrayList<>();

    public CourseOffer(String id, String courseId, String facultyId, String semesterCode, String schedule, int capacity){
        this.id = id; this.courseId = courseId; this.facultyId = facultyId; this.semesterCode = semesterCode;
        this.schedule = schedule; this.capacity = capacity;
    }
    public String getId(){ return id; }
    public String getCourseId(){ return courseId; }
    public String getFacultyId(){ return facultyId; }
    public String getSemesterCode(){ return semesterCode; }
    public String getSchedule(){ return schedule; }
    public int getCapacity(){ return capacity; }
    public void setFacultyId(String id){ this.facultyId = id; }
    public void setSchedule(String s){ this.schedule = s; }
    public void setCapacity(int c){ this.capacity = c; }
    public boolean isEnrollmentOpen(){ return enrollmentOpen; }
    public void setEnrollmentOpen(boolean v){ this.enrollmentOpen = v; }
    public String getSyllabus(){ return syllabus; }
    public void setSyllabus(String s){ this.syllabus = s; }
    public List<String> getEnrolledStudentIds(){ return enrolledStudentIds; }
}
