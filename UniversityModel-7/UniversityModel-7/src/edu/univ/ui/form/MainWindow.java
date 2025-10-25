package edu.univ.ui.form;

/** 
 * Auto-generated editable GUI wrapper for MainWindow.
 * You can open this file in NetBeans Design tab and modify layout safely.
 * It embeds the existing MainWindow as a custom component so you keep functionality.
 */
public class MainWindow extends javax.swing.JFrame {

    public MainWindow() {
        initComponents();
        setLocationRelativeTo(null);
        // Add wrapper panels into tabs
        tabs.addTab("Login", new LoginForm());
        tabs.addTab("Admin", new AdminPanelForm());
        tabs.addTab("Faculty", new FacultyPanelForm());
        tabs.addTab("Registrar", new RegistrarPanelForm());
        tabs.addTab("Student", new StudentPanelForm());
        setTitle("Digital University — Editable UI Shell");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabs = new javax.swing.JTabbedPane();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);
        pack();
        setSize(1000, 700);
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignore) { }
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
