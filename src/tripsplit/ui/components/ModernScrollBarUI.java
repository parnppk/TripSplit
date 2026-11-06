// ############################################################################
// # ไฟล์     : ModernScrollBarUI.java
// # ส่วน     : UI – หน้าตา Scrollbar
// # หน้าที่   : แถบเลื่อนแบบบาง มุมโค้ง ไม่มีปุ่มลูกศร
// # จุดสำคัญ : Inheritance (extends BasicScrollBarUI) + override
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Scrollbar แบบบาง มุมโค้ง ไม่มีปุ่มลูกศร */
public class ModernScrollBarUI extends BasicScrollBarUI {

    // # 1. นำไปใช้กับ ScrollPane
    public static void apply(JScrollPane scrollPane, Color background) {
        style(scrollPane.getVerticalScrollBar(), background, true);
        style(scrollPane.getHorizontalScrollBar(), background, false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private static void style(JScrollBar bar, Color background, boolean vertical) {
        bar.setUI(new ModernScrollBarUI());
        bar.setBackground(background);
        bar.setPreferredSize(vertical ? new Dimension(10, 0) : new Dimension(0, 10));
    }

    // # 2. สีของแถบเลื่อน
    @Override
    protected void configureScrollBarColors() {
        thumbColor = Theme.BORDER_STRONG;
        trackColor = Theme.BG;
    }

    // # 3. ซ่อนปุ่มลูกศรขึ้น/ลง
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private static JButton zeroButton() {
        JButton b = new JButton();
        Dimension zero = new Dimension(0, 0);
        b.setPreferredSize(zero);
        b.setMinimumSize(zero);
        b.setMaximumSize(zero);
        return b;
    }

    // # 4. วาดรางและแท่งเลื่อนมุมโค้ง
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(c.getBackground());
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = Theme.aa(g);
        g2.setColor(isDragging || isThumbRollover() ? Theme.PLACEHOLDER : thumbColor);
        g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
        g2.dispose();
    }
}
