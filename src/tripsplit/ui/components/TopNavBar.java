// ############################################################################
// # ไฟล์     : TopNavBar.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : แถบเมนูด้านบน: โลโก้ | แท็บ ภาพรวม/รายการ/สรุป | ปุ่มด้านขวา
// # จุดสำคัญ : Inner class (Brand, NavTab), callback ด้วย Consumer
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * แถบเมนูด้านบนสุด: โลโก้ | แท็บเมนู | ปุ่มด้านขวา
 * แท็บที่เลือกอยู่จะเป็นสีหลักและมีเส้นใต้
 */
public class TopNavBar extends JPanel {

    // # 1. ค่าคงที่: ความสูงแถบเมนู
    public static final int BAR_HEIGHT = 62;

    // # 2. ส่วนประกอบ: รายการแท็บ, พื้นที่แท็บ, พื้นที่ปุ่ม, callback
    private final List<NavTab> tabs = new ArrayList<>();
    private final JPanel tabBar;
    private final JPanel actions;
    private final Consumer<String> onSelect;

    // # 3. Constructor — โลโก้ซ้าย / แท็บ / ปุ่มด้านขวา
    public TopNavBar(Consumer<String> onSelect) {
        super(new BorderLayout());
        this.onSelect = onSelect;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        Brand brand = new Brand();
        left.add(brand);

        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabBar.setOpaque(false);
        tabBar.setBorder(BorderFactory.createEmptyBorder(0, 48, 0, 0));
        left.add(tabBar);
        add(left, BorderLayout.WEST);

        actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JPanel actionsWrap = new JPanel(new GridBagLayout());
        actionsWrap.setOpaque(false);
        actionsWrap.add(actions);
        add(actionsWrap, BorderLayout.EAST);
    }

    // # 4. เพิ่มแท็บ / เพิ่มปุ่มด้านขวา
    public void addTab(String key, String text, Icon icon) {
        NavTab tab = new NavTab(key, text, icon);
        tabs.add(tab);
        tabBar.add(tab);
        if (tabs.size() == 1) {
            tab.active = true;
        }
    }

    public void addAction(JComponent component) {
        actions.add(component);
    }

    // # 5. เปลี่ยนแท็บที่เลือก (สีเขียว + เส้นใต้)
    /** เปลี่ยนแท็บที่เลือก (ไม่เรียก callback) */
    public void setActive(String key) {
        for (NavTab t : tabs) {
            t.active = t.key.equals(key);
            t.setForeground(t.active ? Theme.PRIMARY_DARK : Theme.TEXT_SOFT);
            t.repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, BAR_HEIGHT);
    }

    // # 6. วาดพื้นขาว + เส้นขอบล่าง
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Theme.BORDER);
        g.fillRect(0, getHeight() - 1, getWidth(), 1);
    }

    // # 7. โลโก้: เครื่องบินกระดาษ + "Trip Split"
    // =====================================================================
    // โลโก้ (เครื่องบินกระดาษสีหลัก + ชื่อโปรแกรม)
    // =====================================================================
    private static class Brand extends JComponent {
        private final Font font = Theme.font(Theme.DISPLAY, 21);

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(font);
            return new Dimension(36 + fm.stringWidth("Trip Split"), BAR_HEIGHT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.aa(g);
            int cy = getHeight() / 2;
            paintPlane(g2, 0, cy - 13, 27, Theme.HEADER_START, Theme.PRIMARY);

            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Theme.HEADER_START);
            g2.drawString("Trip Split", 36, cy + (fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    // # 8. วาดเครื่องบินกระดาษ (ใช้ร่วมกับแบนเนอร์)
    /** วาดเครื่องบินกระดาษ (ใช้ทั้งโลโก้และแบนเนอร์) */
    public static void paintPlane(Graphics2D g2, double x, double y, double s, Color dark, Color light) {
        Path2D.Double plane = new Path2D.Double();
        plane.moveTo(x, y + s * 0.45);
        plane.lineTo(x + s, y);
        plane.lineTo(x + s * 0.68, y + s * 0.98);
        plane.lineTo(x + s * 0.44, y + s * 0.62);
        plane.closePath();
        g2.setPaint(new GradientPaint((float) x, (float) (y + s), dark, (float) (x + s), (float) y, light));
        g2.fill(plane);

        Path2D.Double fold = new Path2D.Double();
        fold.moveTo(x + s * 0.44, y + s * 0.62);
        fold.lineTo(x + s, y);
        fold.lineTo(x + s * 0.38, y + s * 0.90);
        fold.closePath();
        g2.setColor(Theme.alpha(Color.WHITE, 90));
        g2.fill(fold);
    }

    // # 9. แท็บเมนู 1 อัน: ไอคอน + ข้อความ + เส้นใต้เมื่อถูกเลือก
    // =====================================================================
    // แท็บเมนู
    // =====================================================================
    private class NavTab extends JComponent {
        private final String key;
        private final String text;
        private final Icon icon;
        private boolean active;
        private boolean hover;
        private final Font font = Theme.font(Font.BOLD, 14);

        NavTab(String key, String text, Icon icon) {
            this.key = key;
            this.text = text;
            this.icon = icon;
            setForeground(Theme.TEXT_SOFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setActive(key);
                    onSelect.accept(key);
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

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(font);
            return new Dimension(icon.getIconWidth() + 10 + fm.stringWidth(text) + 28, BAR_HEIGHT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.aa(g);
            int w = getWidth();
            int h = getHeight();
            if (hover && !active) {
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(2, 12, w - 4, h - 24, 14, 14);
            }
            Component self = this;
            setForeground(active ? Theme.PRIMARY_DARK : hover ? Theme.TEXT : Theme.TEXT_SOFT);
            int x = 14;
            icon.paintIcon(self, g2, x, (h - icon.getIconHeight()) / 2);
            g2.setFont(font);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, x + icon.getIconWidth() + 10, h / 2 + (fm.getAscent() - fm.getDescent()) / 2);

            if (active) {
                g2.setColor(Theme.PRIMARY);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(10, h - 2, w - 10, h - 2);
            }
            g2.dispose();
        }
    }
}
