
package edu.univ.ui;

import javax.swing.*;
import java.awt.*;

public class Theme {
    public static Color BG = new Color(248, 249, 252);
    public static Color CARD = Color.WHITE;
    public static Color ACCENT = new Color(33, 111, 237);
    public static Font H1 = new Font("SansSerif", Font.BOLD, 18);
    public static Font H2 = new Font("SansSerif", Font.BOLD, 14);
    public static void styleHeader(JLabel l){ l.setFont(H1); }
    public static JPanel card(){
        var p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        return p;
    }
}
