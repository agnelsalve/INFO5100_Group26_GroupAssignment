
package edu.univ.ui;

import edu.univ.acl.AuthManager;
import edu.univ.acl.Role;
import edu.univ.model.*;
import edu.univ.repo.DataStore;
import edu.univ.util.GradeMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FacultyPanel extends JPanel {
    private final DataStore ds = DataStore.get();
    private final Faculty me;

    private final DefaultTableModel offerModel = new DefaultTableModel(new String[]{"OfferID","Course","Term","Schedule","Capacity","EnrollOpen"},0);
    private final JTable offerTable = new JTable(offerModel);

    private final DefaultTableModel enrollModel = new DefaultTableModel(new String[]{"StudentID","Name","Total%","Letter"},0);
    private final JTable enrollTable = new JTable(enrollModel);

    private final DefaultTableModel reportModel = new DefaultTableModel(new String[]{"Metric","Value"},0);
    private final JTable reportTable = new JTable(reportModel);

    public FacultyPanel(){
        setLayout(new BorderLayout(8,8));
        setBackground(Theme.BG);

        var ua = AuthManager.current();
        me = (Faculty) ds.getPersonById(ua.getPersonId());

        var header = new JLabel("Faculty — Courses & Grading");
        Theme.styleHeader(header);

        var tabs = new JTabbedPane();
        tabs.add("My Offers", offersPanel());
        tabs.add("Enrolled Students", enrolledPanel());
        tabs.add("Reports", reportPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshOffers();
    }

    private JPanel offersPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var edit = new JButton("Edit Schedule/Capacity");
        var toggle = new JButton("Toggle Enrollment (if before start)");
        var syllabus = new JButton("Set Syllabus");

        edit.addActionListener(e->editOffer());
        toggle.addActionListener(e->toggleOpen());
        syllabus.addActionListener(e->setSyllabus());

        top.add(edit); top.add(toggle); top.add(syllabus);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(offerTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel enrolledPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var loadBtn = new JButton("Load Enrolled for Selected Offer");
        var gradeBtn = new JButton("Enter Grade % and Compute Letter");
        loadBtn.addActionListener(e->loadEnrolled());
        gradeBtn.addActionListener(e->gradeSelected());

        top.add(loadBtn); top.add(gradeBtn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(enrollTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel reportPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var termBox = new JComboBox<>(ds.semesters.keySet().toArray(new String[0]));
        var run = new JButton("Run Course-Level Report");
        run.addActionListener(e->runReport((String) termBox.getSelectedItem()));
        top.add(new JLabel("Semester")); top.add(termBox); top.add(run);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        return p;
    }

    private void refreshOffers(){
        offerModel.setRowCount(0);
        for (var off: ds.offers.values()){
            if (me.getId().equals(off.getFacultyId())){
                var c = ds.courses.get(off.getCourseId());
                offerModel.addRow(new Object[]{off.getId(), c.getId()+" - "+c.getName(), off.getSemesterCode(), off.getSchedule(), off.getCapacity(), off.isEnrollmentOpen()});
            }
        }
    }

    private CourseOffer selectedOffer(){
        int r = offerTable.getSelectedRow();
        if (r<0) return null;
        String offId = (String) offerModel.getValueAt(r,0);
        return ds.offers.get(offId);
    }

    private void editOffer(){
        var off = selectedOffer();
        if (off==null) return;
        String sched = JOptionPane.showInputDialog(this,"Schedule", off.getSchedule());
        String capStr = JOptionPane.showInputDialog(this,"Capacity", off.getCapacity());
        try {
            int cap = Integer.parseInt(capStr);
            off.setSchedule(sched);
            off.setCapacity(cap);
            refreshOffers();
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid capacity","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleOpen(){
        var off = selectedOffer();
        if (off==null) return;
        var term = ds.semesters.get(off.getSemesterCode());
        if (!term.beforeStart()){
            JOptionPane.showMessageDialog(this,"Cannot toggle: semester already started","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        off.setEnrollmentOpen(!off.isEnrollmentOpen());
        refreshOffers();
    }

    private void setSyllabus(){
        var off = selectedOffer();
        if (off==null) return;
        String s = JOptionPane.showInputDialog(this,"Syllabus (text)", off.getSyllabus());
        if (s!=null) { off.setSyllabus(s); }
    }

    private void loadEnrolled(){
        enrollModel.setRowCount(0);
        var off = selectedOffer();
        if (off==null) return;
        for (var sid: off.getEnrolledStudentIds()){
            var s = ds.getStudent(sid);
            var recs = s.getTranscript().getOrDefault(off.getSemesterCode(), List.of());
            var rec = recs.stream().filter(r->r.getCourseOfferId().equals(off.getId())).findFirst().orElse(null);
            double pct = rec==null?0.0:rec.getTotalPercent();
            String letter = rec==null?"IP":rec.getLetter();
            enrollModel.addRow(new Object[]{s.getId(), s.fullName(), pct, letter});
        }
    }

    private void gradeSelected(){
        var off = selectedOffer();
        if (off==null) return;
        int r = enrollTable.getSelectedRow();
        if (r<0) return;
        String sid = (String) enrollModel.getValueAt(r,0);
        var s = ds.getStudent(sid);
        var rec = s.getTranscript().get(off.getSemesterCode()).stream().filter(x->x.getCourseOfferId().equals(off.getId())).findFirst().orElse(null);
        if (rec==null) return;
        String pctStr = JOptionPane.showInputDialog(this,"Total percent [0-100]", rec.getTotalPercent());
        try {
            double pct = Double.parseDouble(pctStr);
            if (pct<0||pct>100) throw new RuntimeException();
            rec.setTotalPercent(pct);
            rec.setLetter(edu.univ.util.GradeMap.fromPercent(pct));
            loadEnrolled();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid percent","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runReport(String term){
        reportModel.setRowCount(0);
        for (var off: ds.offers.values()){
            if (!off.getSemesterCode().equals(term) || !off.getFacultyId().equals(me.getId())) continue;
            var sids = off.getEnrolledStudentIds();
            int count = sids.size();
            java.util.List<String> letters = new java.util.ArrayList<>();
            for (var sid: sids){
                var s = ds.getStudent(sid);
                var rec = s.getTranscript().getOrDefault(term, java.util.List.of()).stream()
                        .filter(x->x.getCourseOfferId().equals(off.getId())).findFirst().orElse(null);
                if (rec!=null && !rec.getLetter().equals("IP")) letters.add(rec.getLetter());
            }
            double avg = letters.stream().mapToDouble(edu.univ.util.GradeMap::toPoints).average().orElse(0);
            java.util.Map<String, Long> dist = letters.stream().collect(java.util.stream.Collectors.groupingBy(x->x, java.util.stream.Collectors.counting()));
            reportModel.addRow(new Object[]{off.getId()+" — Enrollment", count});
            reportModel.addRow(new Object[]{off.getId()+" — Avg Grade (pts)", String.format("%.2f", avg)});
            reportModel.addRow(new Object[]{off.getId()+" — Distribution", dist.toString()});
            double tuition = 0;
            for (var sid: sids){
                var st = ds.getStudent(sid);
                for (var tx: st.getAccount().getHistory()){
                    if (tx.getNote().contains(off.getId())){
                        if (tx.getType()== Payment.Type.BILL) tuition += tx.getAmount();
                        if (tx.getType()== Payment.Type.REFUND) tuition -= tx.getAmount();
                    }
                }
            }
            reportModel.addRow(new Object[]{off.getId()+" — Tuition billed (net)", String.format("$%.2f", tuition)});
        }
    }
}
