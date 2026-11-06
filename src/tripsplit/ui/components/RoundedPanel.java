// ############################################################################
// # ไฟล์     : RoundedPanel.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : Panel มุมโค้งมีเงา ใช้เป็น "การ์ด" ทั้งโปรแกรม
// # จุดสำคัญ : Inheritance (extends JPanel), Constructor Overloading
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

/** Panel มุมโค้ง มีเงาบาง ๆ ใช้เป็น "การ์ด" ทั่วทั้งโปรแกรม */
public class RoundedPanel extends JPanel {

    // # 1. ข้อมูล: ความโค้ง, สีพื้น, สีขอบ, มีเงาหรือไม่
    private final int radius;
    private Color fill;
    private Color borderColor;
    private final boolean shadow;

    // # 2. Constructor หลายแบบ (Overloading)
    public RoundedPanel(int radius, Color fill) {
        this(radius, fill, Theme.BORDER, true);
    }

    public RoundedPanel(int radius, Color fill, Color borderColor, boolean shadow) {
        this.radius = radius;
        this.fill = fill;
        this.borderColor = borderColor;
        this.shadow = shadow;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout, int radius, Color fill, Color borderColor, boolean shadow) {
        this(radius, fill, borderColor, shadow);
        setLayout(layout);
    }

    // # 3. เปลี่ยนสีพื้น / สีขอบ
    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    // # 4. วาดการ์ด: เงา → พื้น → ขอบ
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight();
        int arc = radius * 2;
        int bottom = shadow ? 3 : 0;

        if (shadow) {
            g2.setColor(new Color(15, 23, 42, 8));
            g2.fillRoundRect(0, 3, w - 1, h - 3, arc, arc);
            g2.setColor(new Color(15, 23, 42, 10));
            g2.fillRoundRect(0, 1, w - 1, h - 2, arc, arc);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1 - bottom, arc, arc);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, w - 1, h - 1 - bottom, arc, arc);
        }
        g2.dispose();
    }
}
