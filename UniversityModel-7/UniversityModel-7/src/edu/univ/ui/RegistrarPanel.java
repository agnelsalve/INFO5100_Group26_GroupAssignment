
package edu.univ.ui;

import edu.univ.acl.AuthManager;
import edu.univ.acl.Role;
import edu.univ.model.CourseOffer;
import edu.univ.repo.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RegistrarPanel extends JPanel {
    private final DataStore ds = DataStore.get();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"OfferID","Course","Faculty","Term","Sched","Cap"},0);
    private final JTable table = new JTable(model);

    public RegistrarPanel(){
        setLayout(new BorderLayout(8,8));
        setBackground(Theme.BG);
        var header = new JLabel("Registrar — Course Offerings");
        Theme.styleHeader(header);

        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var add = new JButton("Add Offering");
        var edit = new JButton("Edit");
        var del = new JButton("Delete");
        top.add(add); top.add(edit); top.add(del);
        add.addActionListener(e->addOffer());
        edit.addActionListener(e->editOffer());
        del.addActionListener(e->delOffer());

        add(header, BorderLayout.NORTH);
        add(top, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        refresh();
    }

    private void refresh(){
        model.setRowCount(0);
        for (var off: ds.offers.values()){
            var c = ds.courses.get(off.getCourseId());
            var f = ds.getFaculty(off.getFacultyId());
            model.addRow(new Object[]{off.getId(), c.getId(), f==null?"":f.fullName(), off.getSemesterCode(), off.getSchedule(), off.getCapacity()});
        }
    }

    private void addOffer(){
        if (!AuthManager.require(Role.REGISTRAR)){
            JOptionPane.showMessageDialog(this,"Not authorized","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = JOptionPane.showInputDialog(this,"Offer ID (e.g., INFO5100-F25-02)");
        String courseId = JOptionPane.showInputDialog(this,"Course ID");
        String facultyId = JOptionPane.showInputDialog(this,"Faculty Person ID");
        String term = JOptionPane.showInputDialog(this,"Term code");
        String sched = JOptionPane.showInputDialog(this,"Schedule");
        String capStr = JOptionPane.showInputDialog(this,"Capacity");
        try{
            int cap = Integer.parseInt(capStr);
            var off = new CourseOffer(id, courseId, facultyId, term, sched, cap);
            ds.offers.put(off.getId(), off);
            refresh();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid data","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void editOffer(){
        int r = table.getSelectedRow();
        if (r<0) return;
        String id = (String) model.getValueAt(r,0);
        var off = ds.offers.get(id);
        String sched = JOptionPane.showInputDialog(this,"Schedule", off.getSchedule());
        String capStr = JOptionPane.showInputDialog(this,"Capacity", off.getCapacity());
        try{
            off.setSchedule(sched);
            off.setCapacity(Integer.parseInt(capStr));
            refresh();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void delOffer(){
        int r = table.getSelectedRow();
        if (r<0) return;
        String id = (String) model.getValueAt(r,0);
        ds.offers.remove(id);
        refresh();
    }
}
