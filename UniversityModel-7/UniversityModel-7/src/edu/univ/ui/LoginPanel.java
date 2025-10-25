
package edu.univ.ui;

import edu.univ.acl.AuthManager;
import edu.univ.acl.Role;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public interface LoginHandler { void onLoginSuccess(Role role); }

    public LoginPanel(LoginHandler handler){
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        var gb = new GridBagConstraints();
        gb.insets = new Insets(6,6,6,6);
        gb.fill = GridBagConstraints.HORIZONTAL;

        var card = Theme.card();
        var cgb = new GridBagConstraints();
        card.setLayout(new GridBagLayout());

        var title = new JLabel("Digital University — Sign In");
        Theme.styleHeader(title);

        var email = new JTextField("admin@univ.edu", 20);
        var pass = new JPasswordField("admin123", 20);

        var demo = new JTextArea(
            "Demo Accounts:\n" +
            "- Admin: admin@univ.edu / admin123\n" +
            "- Registrar: registrar@univ.edu / reg123\n" +
            "- Faculty: prof0@univ.edu / fac0\n" +
            "- Student: stu0@univ.edu / stu0\n"
        );
        demo.setEditable(false);
        demo.setBackground(new Color(245,245,245));

        var login = new JButton("Login");
        login.addActionListener(e->{
            if (AuthManager.login(email.getText().trim(), new String(pass.getPassword()))){
                handler.onLoginSuccess(AuthManager.current().getRole());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cgb.gridy=0; card.add(title, cgb);
        cgb.gridy=1; card.add(new JLabel("Email"), cgb);
        cgb.gridy=2; card.add(email, cgb);
        cgb.gridy=3; card.add(new JLabel("Password"), cgb);
        cgb.gridy=4; card.add(pass, cgb);
        cgb.gridy=5; card.add(login, cgb);
        cgb.gridy=6; cgb.fill = GridBagConstraints.BOTH; cgb.weightx=1; cgb.weighty=1;
        card.add(demo, cgb);

        gb.gridx=0; gb.gridy=0; gb.weightx=1; gb.weighty=1; gb.fill=GridBagConstraints.BOTH;
        add(card, gb);
    }
}
