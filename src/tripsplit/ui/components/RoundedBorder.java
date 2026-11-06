// ############################################################################
// # ไฟล์     : RoundedBorder.java
// # ส่วน     : UI – เส้นขอบ
// # หน้าที่   : เส้นขอบมุมโค้งของช่องกรอก เปลี่ยนเป็นสีหลักเมื่อได้รับ focus
// # จุดสำคัญ : Inheritance (extends AbstractBorder)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.border.AbstractBorder;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

/** เส้นขอบมุมโค้งของช่องกรอกข้อมูล – เปลี่ยนเป็นสีหลักเมื่อได้รับ focus */
public class RoundedBorder extends AbstractBorder {

    // # 1. ข้อมูล: ความโค้ง และระยะขอบด้านใน
    private final int radius;
    private final Insets insets;

    // # 2. Constructor (Overloading: แบบค่าเริ่มต้น / แบบกำหนดเอง)
    public RoundedBorder() {
        this(10, new Insets(8, 12, 8, 12));
    }

    public RoundedBorder(int radius, Insets insets) {
        this.radius = radius;
        this.insets = insets;
    }

    // # 3. วาดเส้นขอบ (ได้รับ focus = สีหลัก หนาขึ้น)
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = Theme.aa(g);
        boolean focused = c.isFocusOwner();
        g2.setColor(focused ? Theme.PRIMARY : Theme.BORDER_STRONG);
        g2.setStroke(new BasicStroke(focused ? 1.6f : 1.1f));
        g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius * 2, radius * 2);
        g2.dispose();
    }

    // # 4. ระยะขอบด้านใน
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets target) {
        target.set(insets.top, insets.left, insets.bottom, insets.right);
        return target;
    }
}
