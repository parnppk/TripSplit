// ############################################################################
// # ไฟล์     : ModernButton.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ปุ่มแบน มุมโค้ง มี hover รองรับหลายสไตล์
// # จุดสำคัญ : Inheritance (extends JButton), enum Style
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** ปุ่มแบบแบน มุมโค้ง มี hover effect รองรับหลายสไตล์ */
public class ModernButton extends JButton {

    // # 1. สไตล์ปุ่มที่รองรับ
    public enum Style { PRIMARY, SECONDARY, DANGER_OUTLINE, GHOST, LIGHT, TAB, TAB_ACTIVE, ICON }

    // # 2. สถานะของปุ่ม: สไตล์, hover, กดค้าง, มุมโค้ง
    private Style style;
    private boolean hover;
    private boolean pressed;
    private int radius = 10;

    // # 3. Constructor — ปิดหน้าตาเดิมของ Swing แล้วติดตามเมาส์เอง
    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(Theme.font(Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        applyForeground();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                applyForeground();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                applyForeground();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    // # 4. ตั้งค่าแบบต่อกัน (method chaining) เช่น new ModernButton(...).withPadding(9, 16)
    public ModernButton withPadding(int vertical, int horizontal) {
        setBorder(BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal));
        return this;
    }

    public ModernButton withRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public ModernButton withIcon(Icon icon, int iconTextGap) {
        setIcon(icon);
        setIconTextGap(iconTextGap);
        return this;
    }

    public ModernButton withFont(Font font) {
        setFont(font);
        return this;
    }

    // # 5. เปลี่ยนสไตล์ / สีตัวอักษรตามสถานะ
    public void setStyle(Style style) {
        this.style = style;
        applyForeground();
        repaint();
    }

    private void applyForeground() {
        switch (style) {
            case PRIMARY:
            case LIGHT:
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                setForeground(Theme.TEXT_SOFT);
                break;
            case DANGER_OUTLINE:
                setForeground(Theme.DANGER);
                break;
            case GHOST:
                setForeground(Theme.PRIMARY_DARK);
                break;
            case TAB:
                setForeground(hover ? Theme.TEXT : Theme.MUTED);
                break;
            case TAB_ACTIVE:
                setForeground(Theme.PRIMARY_DARK);
                break;
            case ICON:
                setForeground(hover ? Theme.DANGER : Theme.PLACEHOLDER);
                break;
            default:
                setForeground(Theme.TEXT);
        }
    }

    // # 6. วาดพื้นและขอบปุ่มตามสไตล์ แล้วให้ Swing วาดข้อความทับ
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight();
        int arc = radius * 2;
        Color bg = null;
        Color border = null;

        switch (style) {
            case PRIMARY:
                bg = pressed ? Theme.PRIMARY_DARK.darker() : hover ? Theme.PRIMARY_DARK : Theme.PRIMARY;
                break;
            case SECONDARY:
                bg = hover ? new Color(0xF1F5F9) : Color.WHITE;
                border = Theme.BORDER_STRONG;
                break;
            case DANGER_OUTLINE:
                bg = hover ? Theme.DANGER_SOFT : Color.WHITE;
                border = new Color(0xFCA5A5);
                break;
            case GHOST:
                bg = hover ? Theme.PRIMARY_SOFT : null;
                break;
            case LIGHT:
                bg = new Color(255, 255, 255, pressed ? 80 : hover ? 60 : 34);
                border = new Color(255, 255, 255, 70);
                break;
            case TAB:
                bg = hover ? new Color(0xF1F5F9) : null;
                break;
            case TAB_ACTIVE:
                bg = Theme.PRIMARY_SOFT;
                break;
            case ICON:
                bg = hover ? Theme.DANGER_SOFT : null;
                break;
            default:
                break;
        }

        if (!isEnabled()) {
            bg = new Color(0xE2E8F0);
            border = null;
        }
        if (bg != null) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }
        super.paintComponent(g2);
        g2.dispose();
    }
}
