// ############################################################################
// # ไฟล์     : CategoryButton.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ปุ่มเลือกหมวดหมู่ (ไอคอน + ชื่อหมวด) ในฟอร์มเพิ่มค่าใช้จ่าย
// # จุดสำคัญ : Inheritance (extends JComponent), วาดเองใน paintComponent
// ############################################################################
package tripsplit.ui.components;

import tripsplit.model.ExpenseCategory;
import tripsplit.ui.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** ปุ่มเลือกหมวดหมู่ค่าใช้จ่าย (ไอคอน + ชื่อหมวด) */
public class CategoryButton extends JComponent {

    // # 1. ข้อมูล: หมวด, ไอคอน, สถานะเลือก / hover
    private final ExpenseCategory category;
    private final Icons.CategoryIcon icon;
    private boolean selected;
    private boolean hover;

    // # 2. Constructor — คลิกแล้วแจ้งฟอร์มผ่าน callback
    public CategoryButton(ExpenseCategory category, Consumer<ExpenseCategory> onSelect) {
        this.category = category;
        this.icon = new Icons.CategoryIcon(category, 28, false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("หมวด" + category.getDisplayName());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(category);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    // # 3. ตั้งสถานะถูกเลือก
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    // # 4. ขนาดปุ่ม
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(140, 46);
    }

    // # 5. วาดปุ่ม: พื้น → ขอบสีหมวด → ไอคอน → ชื่อ → จุดยืนยัน
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        Color color = Theme.categoryColor(category);
        int w = getWidth();
        int h = getHeight();

        g2.setColor(selected ? Theme.mix(color, Color.WHITE, 0.92) : hover ? Theme.SURFACE : Color.WHITE);
        g2.fillRoundRect(1, 1, w - 3, h - 3, 20, 20);
        g2.setStroke(new BasicStroke(selected ? 1.8f : 1.1f));
        g2.setColor(selected ? color : hover ? Theme.BORDER_STRONG : Theme.BORDER);
        g2.drawRoundRect(1, 1, w - 3, h - 3, 20, 20);

        icon.paintIcon(this, g2, 10, (h - icon.getIconHeight()) / 2);

        g2.setFont(Theme.font(Font.BOLD, 14));
        g2.setColor(selected ? Theme.mix(color, Color.BLACK, 0.25) : Theme.TEXT_SOFT);
        FontMetrics fm = g2.getFontMetrics();
        int ty = (h - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent();
        g2.drawString(category.getDisplayName(), 48, ty);

        if (selected) {
            // จุดยืนยันการเลือกที่มุมขวา
            g2.setColor(color);
            g2.fillOval(w - 18, h / 2 - 4, 8, 8);
        }
        g2.dispose();
    }
}
