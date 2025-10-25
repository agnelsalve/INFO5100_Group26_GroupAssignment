
package edu.univ.ui;

import edu.univ.acl.AuthManager;
import edu.univ.model.*;
import edu.univ.repo.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;

public class StudentPanel extends JPanel {
    private final DataStore ds = DataStore.get();
    private final Student me;

    private final JComboBox<String> termBox;
    private final DefaultTableModel offerModel = new DefaultTableModel(new String[]{"OfferID","Course","Faculty","Sched","Cap","Enrolled","Open"},0);
    private final JTable offerTable = new JTable(offerModel);

    private final DefaultTableModel transcriptModel = new DefaultTableModel(new String[]{"Term","Standing","CourseID","Course Name","Grade","Term GPA","Overall GPA"},0);
    private final JTable transcriptTable = new JTable(transcriptModel);

    private final DefaultTableModel payModel = new DefaultTableModel(new String[]{"At","Type","Amount","Note"},0);
    private final JTable payTable = new JTable(payModel);

    public StudentPanel(){
        setLayout(new BorderLayout(8,8));
        setBackground(Theme.BG);

        var ua = AuthManager.current();
        me = (Student) DataStore.get().getPersonById(ua.getPersonId());

        var header = new JLabel("Student — Registration, Transcript, Tuition");
        Theme.styleHeader(header);

        termBox = new JComboBox<>(ds.semesters.keySet().toArray(new String[0]));

        var tabs = new JTabbedPane();
        tabs.add("Course Search & Enroll", searchPanel());
        tabs.add("Transcript", transcriptPanel());
        tabs.add("Pay Tuition", payPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshOffers();
        refreshTranscript();
        refreshPayments();
    }

    private JPanel searchPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var byCourse = new JTextField(8);
        var byFaculty = new JTextField(10);
        var byTitle = new JTextField(12);
        var search1 = new JButton("By CourseID");
        var search2 = new JButton("By Faculty");
        var search3 = new JButton("By Title");
        var enroll = new JButton("Enroll");
        var drop = new JButton("Drop");

        search1.addActionListener(e->loadOffers(ds.searchOffersByCourseId((String) termBox.getSelectedItem(), byCourse.getText())));
        search2.addActionListener(e->loadOffers(ds.searchOffersByFaculty((String) termBox.getSelectedItem(), byFaculty.getText())));
        search3.addActionListener(e->loadOffers(ds.searchOffersByTitle((String) termBox.getSelectedItem(), byTitle.getText())));
        enroll.addActionListener(e->enrollSelected());
        drop.addActionListener(e->dropSelected());

        top.add(new JLabel("Term")); top.add(termBox);
        top.add(new JLabel("CourseID")); top.add(byCourse); top.add(search1);
        top.add(new JLabel("Faculty")); top.add(byFaculty); top.add(search2);
        top.add(new JLabel("Title")); top.add(byTitle); top.add(search3);
        top.add(enroll); top.add(drop);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(offerTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel transcriptPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var refresh = new JButton("Refresh");
        refresh.addActionListener(e->refreshTranscript());
        top.add(new JLabel("Term View")); top.add(termBox); top.add(refresh);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(transcriptTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel payPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var payBtn = new JButton("Pay Balance");
        payBtn.addActionListener(e->payBalance());
        top.add(payBtn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(payTable), BorderLayout.CENTER);
        return p;
    }

    private void refreshOffers(){
        loadOffers(ds.searchOffersByTitle((String) termBox.getSelectedItem(), ""));
    }
    private void loadOffers(List<CourseOffer> list){
        offerModel.setRowCount(0);
        for (var off: list){
            var c = ds.courses.get(off.getCourseId());
            var f = (Faculty) ds.getPersonById(off.getFacultyId());
            offerModel.addRow(new Object[]{off.getId(), c.getId()+" - "+c.getName(), f==null?"":f.fullName(), off.getSchedule(), off.getCapacity(), off.getEnrolledStudentIds().size(), off.isEnrollmentOpen()});
        }
    }

    private void enrollSelected(){
        int r = offerTable.getSelectedRow();
        if (r<0) return;
        String offId = (String) offerModel.getValueAt(r,0);
        var off = ds.offers.get(offId);
        var c = ds.courses.get(off.getCourseId());

        if (!off.isEnrollmentOpen()){
            JOptionPane.showMessageDialog(this,"Enrollment closed","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (off.getEnrolledStudentIds().contains(me.getId())){
            JOptionPane.showMessageDialog(this,"Already enrolled","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (me.getCurrentTermCredits() + c.getCredits() > 8){
            JOptionPane.showMessageDialog(this,"Credit limit (8) exceeded","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (off.getEnrolledStudentIds().size() >= off.getCapacity()){
            JOptionPane.showMessageDialog(this,"Course full","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        off.getEnrolledStudentIds().add(me.getId());
        me.setCurrentTermCredits(me.getCurrentTermCredits()+c.getCredits());
        me.getAccount().bill(c.getCredits()*ds.tuitionPerCredit(), "Enrolled "+off.getId());
        var rec = new GradeRecord(off.getSemesterCode(), off.getId(), off.getCourseId(), c.getCredits());
        me.getTranscript().computeIfAbsent(off.getSemesterCode(), t-> new ArrayList<>()).add(rec);
        refreshOffers();
        JOptionPane.showMessageDialog(this,"Enrolled successfully & billed tuition");
    }

    private void dropSelected(){
        int r = offerTable.getSelectedRow();
        if (r<0) return;
        String offId = (String) offerModel.getValueAt(r,0);
        var off = ds.offers.get(offId);
        var c = ds.courses.get(off.getCourseId());
        if (!off.getEnrolledStudentIds().remove(me.getId())){
            JOptionPane.showMessageDialog(this,"Not enrolled","Info",JOptionPane.INFORMATION_MESSAGE); return;
        }
        me.setCurrentTermCredits(Math.max(0, me.getCurrentTermCredits()-c.getCredits()));
        me.getAccount().refund(c.getCredits()*ds.tuitionPerCredit(), "Dropped "+off.getId());
        var recs = me.getTranscript().getOrDefault(off.getSemesterCode(), new ArrayList<>());
        recs.removeIf(r2->r2.getCourseOfferId().equals(off.getId()));
        refreshOffers();
        JOptionPane.showMessageDialog(this,"Dropped & refunded");
    }

    private void refreshTranscript(){
        transcriptModel.setRowCount(0);
        if (me.getAccount().getBalance() > 0.0){
            transcriptModel.addRow(new Object[]{"—","—","—","Pay tuition to view transcript","—","—","—"});
            return;
        }
        String filterTerm = (String) termBox.getSelectedItem();
        double overallQ=0, overallC=0;
        for (var entry: me.getTranscript().entrySet()){
            for (var r: entry.getValue()){
                if (!r.getLetter().equals("IP")){
                    overallQ += edu.univ.util.GradeMap.toPoints(r.getLetter()) * r.getCredits();
                    overallC += r.getCredits();
                }
            }
        }
        double overall = overallC==0?0.0:overallQ/overallC;

        for (var entry: me.getTranscript().entrySet()){
            String term = entry.getKey();
            if (filterTerm!=null && !filterTerm.equals(term)) continue;
            var list = entry.getValue();
            double termGpa = edu.univ.util.GpaUtil.termGpa(list);
            String standing;
            if (termGpa >= 3.0 && overall >= 3.0) standing = "Good Standing";
            else if (overall < 3.0) standing = "Academic Probation";
            else standing = "Academic Warning";

            for (var r: list){
                var course = ds.courses.get(r.getCourseId());
                transcriptModel.addRow(new Object[]{term, standing, course.getId(), course.getName(), r.getLetter(), String.format("%.2f", termGpa), String.format("%.2f", overall)});
            }
        }
    }

    private void refreshPayments(){
        payModel.setRowCount(0);
        for (var tx: me.getAccount().getHistory()){
            payModel.addRow(new Object[]{tx.getAt(), tx.getType(), String.format("$%.2f", tx.getAmount()), tx.getNote()});
        }
        payModel.addRow(new Object[]{"Balance", "", String.format("$%.2f", me.getAccount().getBalance()), ""});
    }

    private void payBalance(){
        double bal = me.getAccount().getBalance();
        if (bal <= 0.0){
            JOptionPane.showMessageDialog(this,"No balance to pay","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String amtStr = JOptionPane.showInputDialog(this,"Enter amount to pay", String.format("%.2f", bal));
        if (amtStr==null) return;
        try{
            double amt = Double.parseDouble(amtStr);
            if (amt<=0) throw new RuntimeException();
            me.getAccount().pay(amt, "Student payment");
            refreshPayments();
            JOptionPane.showMessageDialog(this,"Payment recorded");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid amount","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
