package edu.univ.ui;

import edu.univ.acl.*;
import javax.swing.*;
import java.awt.*;

public class MainApp {
    private final JFrame frame = new JFrame("Digital University — ACL");
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Lazily created after login
    private AdminPanel adminPanel;
    private FacultyPanel facultyPanel;
    private StudentPanel studentPanel;
    private RegistrarPanel registrarPanel;

    public MainApp(){
        // Login panel with callback
        var login = new LoginPanel(role -> {
            switch (role){
                case ADMIN -> {
                    if (adminPanel == null) {
                        adminPanel = new AdminPanel();
                        root.add(adminPanel, "admin");
                    }
                    show("admin");
                }
                case FACULTY -> {
                    if (facultyPanel == null) {
                        facultyPanel = new FacultyPanel();
                        root.add(facultyPanel, "faculty");
                    }
                    show("faculty");
                }
                case STUDENT -> {
                    if (studentPanel == null) {
                        studentPanel = new StudentPanel();
                        root.add(studentPanel, "student");
                    }
                    show("student");
                }
                case REGISTRAR -> {
                    if (registrarPanel == null) {
                        registrarPanel = new RegistrarPanel();
                        root.add(registrarPanel, "registrar");
                    }
                    show("registrar");
                }
            }
        });

        root.add(login, "login");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1050, 680);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(topBar(), BorderLayout.NORTH);
        frame.add(root, BorderLayout.CENTER);
        frame.setVisible(true);

        show("login");
    }

    private JPanel topBar(){
        var p = new JPanel(new BorderLayout());
        p.setBackground(new java.awt.Color(245,247,250));
        var title = new JLabel("Digital University");
        title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
        p.add(title, BorderLayout.WEST);
        var logout = new JButton("Logout");
        logout.addActionListener(e -> {
            AuthManager.logout();
            show("login");
        });
        p.add(logout, BorderLayout.EAST);
        return p;
    }

    private void show(String name){
        cards.show(root, name);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(MainApp::new);
    }
}
