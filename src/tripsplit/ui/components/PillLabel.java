// ############################################################################
// # ไฟล์     : PillLabel.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ป้ายข้อความทรงแคปซูล (badge) เช่น "4 คน", "1,987.00 บาท"
// # จุดสำคัญ : Inheritance (extends JLabel)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** ป้ายข้อความทรงแคปซูล (badge) */
public class PillLabel extends JLabel {

    // # 1. สีพื้นของป้าย
    private Color background;

    // # 2. Constructor
    public PillLabel(String text, Color background, Color foreground) {
        super(text, SwingConstants.CENTER);
        this.background = background;
        setForeground(foreground);
        setFont(Theme.font(Font.BOLD, 12));
        setBorder(BorderFactory.createEmptyBorder(4, 11, 4, 11));
        setOpaque(false);
    }

    // # 3. เปลี่ยนสีพื้น / สีตัวอักษร
    public void setColors(Color background, Color foreground) {
        this.background = background;
        setForeground(foreground);
        repaint();
    }

    // # 4. วาดทรงแคปซูล แล้ววาดข้อความทับ
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        g2.setColor(background);
        int h = getHeight();
        g2.fillRoundRect(0, 0, getWidth(), h, h, h);
        g2.dispose();
        super.paintComponent(g);
    }
}
