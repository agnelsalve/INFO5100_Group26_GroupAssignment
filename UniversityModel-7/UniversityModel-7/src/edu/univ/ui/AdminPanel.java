
package edu.univ.ui;

import edu.univ.acl.AuthManager;
import edu.univ.acl.Role;
import edu.univ.acl.UserAccount;
import edu.univ.model.*;
import edu.univ.repo.DataStore;
import edu.univ.util.IdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AdminPanel extends JPanel {
    private final DataStore ds = DataStore.get();
    private final DefaultTableModel peopleModel = new DefaultTableModel(new String[]{"ID","Name","Email","Dept","Type"},0);
    private final JTable peopleTable = new JTable(peopleModel);

    private final DefaultTableModel analyticsModel = new DefaultTableModel(new String[]{"Metric","Value"},0);
    private final JTable analyticsTable = new JTable(analyticsModel);

    public AdminPanel(){
        setLayout(new BorderLayout(12,12));
        setBackground(Theme.BG);

        var header = new JLabel("Admin — Accounts, Registry, Analytics");
        Theme.styleHeader(header);

        var tabs = new JTabbedPane();
        tabs.add("Registry", registryPanel());
        tabs.add("Analytics", analyticsPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshPeople();
        refreshAnalytics();
    }

    private JPanel registryPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        p.setBackground(Theme.BG);
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var addBtn = new JButton("Register Person");
        var delBtn = new JButton("Delete");
        var editBtn = new JButton("Edit Email");
        var searchName = new JTextField(12);
        var searchId = new JTextField(8);
        var searchDept = new JTextField(8);
        var searchBtn = new JButton("Search (Name/ID/Dept)");

        addBtn.addActionListener(e->registerDialog());
        delBtn.addActionListener(e->deleteSelected());
        editBtn.addActionListener(e->editEmailSelected());
        searchBtn.addActionListener(e->searchPeople(searchName.getText(), searchId.getText(), searchDept.getText()));

        top.add(addBtn); top.add(delBtn); top.add(editBtn);
        top.add(new JLabel("Name")); top.add(searchName);
        top.add(new JLabel("ID")); top.add(searchId);
        top.add(new JLabel("Dept")); top.add(searchDept);
        top.add(searchBtn);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(peopleTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel analyticsPanel(){
        var p = new JPanel(new BorderLayout(8,8));
        p.setBackground(Theme.BG);
        var refresh = new JButton("Refresh");
        refresh.addActionListener(e->refreshAnalytics());
        p.add(refresh, BorderLayout.NORTH);
        p.add(new JScrollPane(analyticsTable), BorderLayout.CENTER);
        return p;
    }

    private void registerDialog(){
        if (!AuthManager.require(Role.ADMIN)){
            JOptionPane.showMessageDialog(this, "Not authorized", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        var fn = JOptionPane.showInputDialog(this,"First name");
        var ln = JOptionPane.showInputDialog(this,"Last name");
        var email = JOptionPane.showInputDialog(this,"Email");
        if (email!=null) email = email.toLowerCase();
        var type = (String) JOptionPane.showInputDialog(this,"Type","Select", JOptionPane.PLAIN_MESSAGE,null,
                new String[]{"Student","Faculty","Registrar"}, "Student");
        if (fn==null||ln==null||email==null||type==null||fn.isBlank()||ln.isBlank()||email.isBlank()) return;

        for (var p: ds.persons.values()){
            if (p.getEmail().equalsIgnoreCase(email)){
                JOptionPane.showMessageDialog(this, "Email already registered", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        var id = edu.univ.util.IdGenerator.nextId();
        var dept = "IS";
        switch (type){
            case "Student" -> {
                var s = new Student(id, fn, ln, email, dept);
                ds.persons.put(id, s);
                ds.users.put(email, new UserAccount(email, "changeme", Role.STUDENT, id));
            }
            case "Faculty" -> {
                var f = new Faculty(id, fn, ln, email, dept);
                ds.persons.put(id, f);
                ds.users.put(email, new UserAccount(email, "changeme", Role.FACULTY, id));
            }
            case "Registrar" -> {
                var r = new Registrar(id, fn, ln, email, dept);
                ds.persons.put(id, r);
                ds.users.put(email, new UserAccount(email, "changeme", Role.REGISTRAR, id));
            }
        }
        refreshPeople();
        JOptionPane.showMessageDialog(this, "Created "+type+" with ID: "+id+" (password=changeme)");
    }

    private void deleteSelected(){
        int r = peopleTable.getSelectedRow();
        if (r<0) return;
        String id = (String) peopleModel.getValueAt(r,0);
        var person = ds.persons.remove(id);
        if (person!=null){
            ds.users.remove(person.getEmail().toLowerCase());
            refreshPeople();
        }
    }
    private void editEmailSelected(){
        int r = peopleTable.getSelectedRow();
        if (r<0) return;
        String id = (String) peopleModel.getValueAt(r,0);
        var person = ds.persons.get(id);
        var newEmail = JOptionPane.showInputDialog(this, "New email", person.getEmail());
        if (newEmail==null || newEmail.isBlank()) return;
        if (ds.getUserByEmail(newEmail)!=null){
            JOptionPane.showMessageDialog(this,"Email already exists","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        var ua = ds.getUserByEmail(person.getEmail());
        ds.users.remove(person.getEmail());
        person.setEmail(newEmail);
        if (ua!=null){
            ds.users.put(newEmail.toLowerCase(), new UserAccount(newEmail, ua.getPassword(), ua.getRole(), ua.getPersonId()));
        }
        refreshPeople();
    }

    private void searchPeople(String name, String id, String dept){
        var rows = ds.persons.values().stream().filter(p->{
            boolean ok = true;
            if (name!=null && !name.isBlank()){
                ok &= (p.fullName().toLowerCase().contains(name.toLowerCase()));
            }
            if (id!=null && !id.isBlank()){
                ok &= p.getId().toLowerCase().contains(id.toLowerCase());
            }
            if (dept!=null && !dept.isBlank()){
                ok &= (p.getDepartmentId().equalsIgnoreCase(dept));
            }
            return ok;
        }).collect(Collectors.toList());
        loadPeople(rows);
    }

    private void refreshPeople(){
        loadPeople(new ArrayList<>(ds.persons.values()));
    }
    private void loadPeople(List<Person> list){
        peopleModel.setRowCount(0);
        for (var p: list){
            String type = p.getClass().getSimpleName();
            peopleModel.addRow(new Object[]{p.getId(), p.fullName(), p.getEmail(), p.getDepartmentId(), type});
        }
    }

    private void refreshAnalytics(){
        analyticsModel.setRowCount(0);
        long admins = ds.persons.values().stream().filter(p->p instanceof Admin).count();
        long regs = ds.persons.values().stream().filter(p->p instanceof Registrar).count();
        long facs = ds.persons.values().stream().filter(p->p instanceof Faculty).count();
        long studs = ds.persons.values().stream().filter(p->p instanceof Student).count();
        analyticsModel.addRow(new Object[]{"Active users by role", "Admin="+admins+", Registrar="+regs+", Faculty="+facs+", Student="+studs});

        Map<String, Long> byTerm = ds.offers.values().stream().collect(Collectors.groupingBy(CourseOffer::getSemesterCode, Collectors.counting()));
        for (var e: byTerm.entrySet()){
            analyticsModel.addRow(new Object[]{"Courses offered ("+e.getKey()+")", e.getValue()});
        }

        for (var off: ds.offers.values()){
            analyticsModel.addRow(new Object[]{"Enrollments "+off.getId(), off.getEnrolledStudentIds().size()});
        }

        double revenue = ds.persons.values().stream().filter(p->p instanceof Student).map(p->(Student)p)
                .mapToDouble(s->{
                    double billed = 0.0;
                    for (var tx: s.getAccount().getHistory()){
                        if (tx.getType()== Payment.Type.BILL) billed += tx.getAmount();
                        if (tx.getType()== Payment.Type.REFUND) billed -= tx.getAmount();
                    }
                    return billed;
                }).sum();
        analyticsModel.addRow(new Object[]{"Tuition revenue (billed minus refunds)", String.format("$%.2f", revenue)});
    }
}
