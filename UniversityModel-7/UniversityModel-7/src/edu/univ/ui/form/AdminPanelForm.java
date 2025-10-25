package edu.univ.ui.form;

/** 
 * Auto-generated editable GUI wrapper for edu.univ.ui.AdminPanel.
 * You can open this file in NetBeans Design tab and modify layout safely.
 * It embeds the existing edu.univ.ui.AdminPanel as a custom component so you keep functionality.
 */
public class AdminPanelForm extends javax.swing.JPanel {

    private edu.univ.ui.AdminPanel embedded;

    public AdminPanelForm() {
        initComponents();
        // Create and add the original panel as a custom component
        embedded = new edu.univ.ui.AdminPanel();
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        inner.add(embedded, gbc);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setLayout(new java.awt.GridBagLayout());

        header = new javax.swing.JLabel();
        header.setText("AdminPanelForm");
        header.setFont(header.getFont().deriveFont(header.getFont().getStyle() | java.awt.Font.BOLD, 16f));
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(8,8,8,8));
        header.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        add(header, new java.awt.GridBagConstraints());

        inner = new javax.swing.JPanel();
        inner.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(inner, gridBagConstraints);
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel header;
    private javax.swing.JPanel inner;
    // End of variables declaration//GEN-END:variables
}
