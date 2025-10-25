
package edu.univ.repo;

import edu.univ.acl.Role;
import edu.univ.acl.UserAccount;
import edu.univ.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DataStore {
    private static final DataStore INSTANCE = new DataStore();
    public static DataStore get(){ return INSTANCE; }

    public final Map<String, Department> departments = new LinkedHashMap<>();
    public final Map<String, Person> persons = new LinkedHashMap<>();
    public final Map<String, UserAccount> users = new LinkedHashMap<>();
    public final Map<String, Course> courses = new LinkedHashMap<>();
    public final Map<String, Semester> semesters = new LinkedHashMap<>();
    public final Map<String, CourseOffer> offers = new LinkedHashMap<>();

    private DataStore(){ seed(); }

    public UserAccount getUserByEmail(String email){
        return users.get(email.toLowerCase());
    }
    public Person getPersonById(String id){ return persons.get(id); }
    public Student getStudent(String id){ return (Student)persons.get(id); }
    public Faculty getFaculty(String id){ return (Faculty)persons.get(id); }

    public List<CourseOffer> searchOffersByCourseId(String term, String courseId){
        String norm = courseId.replace(" ", "");
        return offers.values().stream().filter(o -> o.getSemesterCode().equals(term)
                && o.getCourseId().replace(" ", "").contains(norm)).collect(Collectors.toList());
    }
    public List<CourseOffer> searchOffersByFaculty(String term, String facultyName){
        String fn = facultyName.toLowerCase();
        return offers.values().stream().filter(o -> o.getSemesterCode().equals(term)).filter(o -> {
            var f = (Faculty) persons.get(o.getFacultyId());
            return f != null && f.fullName().toLowerCase().contains(fn);
        }).collect(Collectors.toList());
    }
    public List<CourseOffer> searchOffersByTitle(String term, String title){
        String t = title.toLowerCase();
        return offers.values().stream().filter(o -> o.getSemesterCode().equals(term)).filter(o -> {
            var c = courses.get(o.getCourseId());
            return c != null && c.getName().toLowerCase().contains(t);
        }).collect(Collectors.toList());
    }

    public double tuitionPerCredit(){ return 1200.0; }

    private void seed(){
        departments.put("IS", new Department("IS", "Information Systems"));
        semesters.put("Fall 2025", new Semester("Fall 2025",
                LocalDate.of(2025,9,2), LocalDate.of(2025,12,20)));

        courses.put("INFO5100", new Course("INFO5100","App Eng & Dev",4));
        courses.put("INFO6150", new Course("INFO6150","Web Design & UX",4));
        courses.put("INFO6205", new Course("INFO6205","Prog Struct & Algos",4));
        courses.put("INFO6210", new Course("INFO6210","DBMS",4));
        courses.put("INFO7245", new Course("INFO7245","Big Data Systems",4));

        var admin = new Admin("U1000001","Alice","Admin","admin@univ.edu","IS");
        persons.put(admin.getId(), admin);
        users.put("admin@univ.edu", new UserAccount("admin@univ.edu","admin123", Role.ADMIN, admin.getId()));

        var reg = new Registrar("U1000002","Rita","Registrar","registrar@univ.edu","IS");
        persons.put(reg.getId(), reg);
        users.put("registrar@univ.edu", new UserAccount("registrar@univ.edu","reg123", Role.REGISTRAR, reg.getId()));

        for (int i=0;i<10;i++){
            var f = new Faculty("U10F%03d".formatted(i), "Prof"+i, "Smith", "prof"+i+"@univ.edu","IS");
            persons.put(f.getId(), f);
            users.put(f.getEmail(), new UserAccount(f.getEmail(),"fac"+i, Role.FACULTY, f.getId()));
        }
        for (int i=0;i<10;i++){
            var s = new Student("U10S%03d".formatted(i), "Stu"+i, "Lee", "stu"+i+"@univ.edu","IS");
            persons.put(s.getId(), s);
            users.put(s.getEmail(), new UserAccount(s.getEmail(),"stu"+i, Role.STUDENT, s.getId()));
        }
        for (int i=0;i<10;i++){
            var p = new Student("U99S%03d".formatted(i), "Extra"+i, "User", "extra"+i+"@univ.edu","IS");
            persons.put(p.getId(), p);
        }

        String term = "Fall 2025";
        String[] courseIds = {"INFO5100","INFO6150","INFO6205","INFO6210","INFO7245"};
        int fi = 0;
        for (int i=0;i<courseIds.length;i++){
            String cid = courseIds[i];
            String fid = "U10F%03d".formatted(fi++);
            var off = new CourseOffer(cid.replace(" ","")+ "-F25-0"+(i+1), cid, fid, term, "Tue 6-9pm", 30);
            offers.put(off.getId(), off);
        }

        for (int s=0;s<5;s++){
            var stu = (Student) persons.get("U10S%03d".formatted(s));
            for (int k=0;k<3;k++){
                var off = offers.values().stream().toList().get(k);
                off.getEnrolledStudentIds().add(stu.getId());
                var c = courses.get(off.getCourseId());
                stu.getAccount().bill(c.getCredits() * tuitionPerCredit(), "Enrolled "+off.getId());
                var rec = new GradeRecord(term, off.getId(), off.getCourseId(), c.getCredits());
                stu.getTranscript().computeIfAbsent(term, t-> new ArrayList<>()).add(rec);
                stu.setCurrentTermCredits(stu.getCurrentTermCredits()+c.getCredits());
            }
        }
    }
}
