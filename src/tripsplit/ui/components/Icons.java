// ############################################################################
// # ไฟล์     : Icons.java
// # ส่วน     : UI – ไอคอน
// # หน้าที่   : ไอคอนทั้งหมดวาดด้วย Graphics2D (ไม่ใช้ไฟล์รูป) คมชัดทุกขนาด
// # จุดสำคัญ : ทุกคลาส implements Icon (Polymorphism ผ่าน interface)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.model.ExpenseCategory;
import tripsplit.ui.Theme;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * ไอคอนทั้งหมดของโปรแกรม วาดด้วย Graphics2D (ไม่ต้องใช้ไฟล์รูป)
 * จึงคมชัดทุกขนาด และแสดงผลเหมือนกันทุกเครื่อง
 */
public final class Icons {

    private Icons() {
    }

    // # 1. ไอคอนหมวด: ชามข้าว / รถ / เตียง / ดาว
    // =====================================================================
    // ไอคอนหมวดหมู่ค่าใช้จ่าย
    // =====================================================================
    public static class CategoryIcon implements Icon {
        private final ExpenseCategory category;
        private final int size;
        private final boolean solid;

        public CategoryIcon(ExpenseCategory category, int size, boolean solid) {
            this.category = category;
            this.size = size;
            this.solid = solid;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.translate(x, y);
            Color color = Theme.categoryColor(category);
            Color bg = solid ? color : Theme.soft(color);
            Color fg = solid ? Color.WHITE : color;
            double s = size;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, s, s, s * 0.6, s * 0.6));
            g2.setColor(fg);

            switch (category) {
                case FOOD: {
                    // ชาม + ตะเกียบ
                    g2.setStroke(new BasicStroke((float) (s * 0.065), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(new Line2D.Double(s * 0.42, s * 0.47, s * 0.62, s * 0.20));
                    g2.draw(new Line2D.Double(s * 0.52, s * 0.48, s * 0.76, s * 0.25));
                    g2.fill(new Arc2D.Double(s * 0.20, s * 0.28, s * 0.60, s * 0.50, 180, 180, Arc2D.CHORD));
                    g2.fill(new RoundRectangle2D.Double(s * 0.16, s * 0.49, s * 0.68, s * 0.07, s * 0.07, s * 0.07));
                    g2.fill(new RoundRectangle2D.Double(s * 0.40, s * 0.74, s * 0.20, s * 0.06, s * 0.05, s * 0.05));
                    break;
                }
                case TRANSPORT: {
                    // รถยนต์
                    g2.fill(new RoundRectangle2D.Double(s * 0.30, s * 0.28, s * 0.40, s * 0.22, s * 0.14, s * 0.14));
                    g2.fill(new RoundRectangle2D.Double(s * 0.16, s * 0.42, s * 0.68, s * 0.22, s * 0.10, s * 0.10));
                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Double(s * 0.35, s * 0.32, s * 0.13, s * 0.09, s * 0.04, s * 0.04));
                    g2.fill(new RoundRectangle2D.Double(s * 0.52, s * 0.32, s * 0.13, s * 0.09, s * 0.04, s * 0.04));
                    double r = s * 0.10;
                    g2.fill(new Ellipse2D.Double(s * 0.32 - r - s * 0.03, s * 0.66 - r - s * 0.03, (r + s * 0.03) * 2, (r + s * 0.03) * 2));
                    g2.fill(new Ellipse2D.Double(s * 0.68 - r - s * 0.03, s * 0.66 - r - s * 0.03, (r + s * 0.03) * 2, (r + s * 0.03) * 2));
                    g2.setColor(fg);
                    g2.fill(new Ellipse2D.Double(s * 0.32 - r, s * 0.66 - r, r * 2, r * 2));
                    g2.fill(new Ellipse2D.Double(s * 0.68 - r, s * 0.66 - r, r * 2, r * 2));
                    break;
                }
                case HOTEL: {
                    // เตียง
                    g2.fill(new RoundRectangle2D.Double(s * 0.18, s * 0.26, s * 0.08, s * 0.50, s * 0.05, s * 0.05));
                    g2.fill(new RoundRectangle2D.Double(s * 0.18, s * 0.52, s * 0.66, s * 0.14, s * 0.06, s * 0.06));
                    g2.fill(new RoundRectangle2D.Double(s * 0.76, s * 0.58, s * 0.08, s * 0.18, s * 0.05, s * 0.05));
                    g2.fill(new RoundRectangle2D.Double(s * 0.30, s * 0.40, s * 0.16, s * 0.10, s * 0.07, s * 0.07));
                    g2.fill(new RoundRectangle2D.Double(s * 0.49, s * 0.38, s * 0.35, s * 0.14, s * 0.08, s * 0.08));
                    break;
                }
                case ACTIVITY: {
                    // ดาว
                    Path2D.Double star = new Path2D.Double();
                    double cx = s / 2, cy = s * 0.53, outer = s * 0.30, inner = s * 0.13;
                    for (int i = 0; i < 10; i++) {
                        double angle = Math.toRadians(-90 + i * 36);
                        double rad = (i % 2 == 0) ? outer : inner;
                        double px = cx + Math.cos(angle) * rad;
                        double py = cy + Math.sin(angle) * rad;
                        if (i == 0) star.moveTo(px, py); else star.lineTo(px, py);
                    }
                    star.closePath();
                    g2.setStroke(new BasicStroke((float) (s * 0.04), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.fill(star);
                    g2.draw(star);
                    break;
                }
                default:
                    break;
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // # 2. Avatar วงกลมพร้อมอักษรตัวแรกของชื่อ
    // =====================================================================
    // Avatar วงกลมพร้อมอักษรตัวแรกของชื่อ
    // =====================================================================
    public static class AvatarIcon implements Icon {
        private final String name;
        private final int size;

        public AvatarIcon(String name, int size) {
            this.name = name;
            this.size = size;
        }

        /** ตัดสระหน้า (เ แ โ ใ ไ) ออก เพื่อให้ได้พยัญชนะตัวแรก เช่น "เบียร์" -> "บ" */
        public static String initialOf(String name) {
            String n = name.trim();
            int i = 0;
            while (i < n.length() - 1 && "เแโใไ".indexOf(n.charAt(i)) >= 0) {
                i++;
            }
            return n.isEmpty() ? "?" : n.substring(i, i + 1).toUpperCase();
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.setColor(Theme.avatarColor(name));
            g2.fill(new Ellipse2D.Double(x, y, size, size));
            g2.setColor(Color.WHITE);
            g2.setFont(Theme.font(Font.BOLD, size * 0.46f));
            FontMetrics fm = g2.getFontMetrics();
            String initial = initialOf(name);
            int tx = x + (size - fm.stringWidth(initial)) / 2;
            int ty = y + (size - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent();
            g2.drawString(initial, tx, ty);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // # 3. ไอคอน Checkbox มุมโค้ง
    // =====================================================================
    // Checkbox แบบมุมโค้ง
    // =====================================================================
    public static class CheckIcon implements Icon {
        private static final int SIZE = 18;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = Theme.aa(g);
            boolean selected = b.isSelected();
            if (selected) {
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(x, y, SIZE, SIZE, 9, 9);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D.Double check = new Path2D.Double();
                check.moveTo(x + 4.5, y + 9.5);
                check.lineTo(x + 7.8, y + 12.8);
                check.lineTo(x + 13.5, y + 5.8);
                g2.draw(check);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, SIZE, SIZE, 9, 9);
                g2.setColor(b.getModel().isRollover() ? Theme.PRIMARY : Theme.BORDER_STRONG);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, 9, 9);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }

    // # 4. ไอคอนกากบาท (ปุ่มลบสมาชิก)
    // =====================================================================
    // กากบาท (ปุ่มลบ) – ใช้สีตามตัวอักษรของปุ่ม จึงเปลี่ยนสีตอน hover ได้
    // =====================================================================
    public static class CloseIcon implements Icon {
        private final int size;

        public CloseIcon(int size) {
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.setColor(c != null ? c.getForeground() : Theme.MUTED);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int p = 2;
            g2.drawLine(x + p, y + p, x + size - p, y + size - p);
            g2.drawLine(x + size - p, y + p, x + p, y + size - p);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // # 5. ไอคอนเส้นสำหรับเมนูและปุ่ม (บ้าน, รายการ, กราฟ, ปฏิทิน ฯลฯ)
    // =====================================================================
    // ไอคอนเส้น (line icon) สำหรับเมนูและปุ่ม – ใช้สีตัวอักษรของ component
    // =====================================================================
    public static class LineIcon implements Icon {
        public enum Type { HOME, LIST, PIE, BRIEFCASE, PLUS, CALENDAR, EDIT, SPARKLE, CHEVRON_LEFT, CHEVRON_RIGHT, USERS }

        private final Type type;
        private final int size;
        private final Color color;

        public LineIcon(Type type, int size) {
            this(type, size, null);
        }

        public LineIcon(Type type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.translate(x, y);
            double s = size;
            g2.setColor(color != null ? color : c != null ? c.getForeground() : Theme.TEXT);
            g2.setStroke(new BasicStroke((float) Math.max(1.6, s * 0.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case HOME: {
                    Path2D.Double p = new Path2D.Double();
                    p.moveTo(s * 0.12, s * 0.48);
                    p.lineTo(s * 0.50, s * 0.13);
                    p.lineTo(s * 0.88, s * 0.48);
                    g2.draw(p);
                    Path2D.Double body = new Path2D.Double();
                    body.moveTo(s * 0.24, s * 0.40);
                    body.lineTo(s * 0.24, s * 0.86);
                    body.lineTo(s * 0.76, s * 0.86);
                    body.lineTo(s * 0.76, s * 0.40);
                    g2.draw(body);
                    g2.fill(new RoundRectangle2D.Double(s * 0.42, s * 0.58, s * 0.16, s * 0.28, s * 0.05, s * 0.05));
                    break;
                }
                case LIST: {
                    for (int i = 0; i < 3; i++) {
                        double yy = s * (0.22 + i * 0.28);
                        g2.fill(new Ellipse2D.Double(s * 0.08, yy - s * 0.07, s * 0.14, s * 0.14));
                        g2.draw(new Line2D.Double(s * 0.36, yy, s * 0.92, yy));
                    }
                    break;
                }
                case PIE: {
                    g2.draw(new Arc2D.Double(s * 0.10, s * 0.14, s * 0.74, s * 0.74, 90, 270, Arc2D.OPEN));
                    Path2D.Double slice = new Path2D.Double();
                    slice.moveTo(s * 0.55, s * 0.08);
                    slice.lineTo(s * 0.55, s * 0.44);
                    slice.lineTo(s * 0.91, s * 0.44);
                    slice.append(new Arc2D.Double(s * 0.19, s * 0.08, s * 0.72, s * 0.72, 0, 90, Arc2D.OPEN), true);
                    g2.draw(slice);
                    break;
                }
                case BRIEFCASE: {
                    g2.draw(new RoundRectangle2D.Double(s * 0.10, s * 0.30, s * 0.80, s * 0.56, s * 0.16, s * 0.16));
                    g2.draw(new RoundRectangle2D.Double(s * 0.35, s * 0.14, s * 0.30, s * 0.16, s * 0.08, s * 0.08));
                    g2.draw(new Line2D.Double(s * 0.10, s * 0.54, s * 0.90, s * 0.54));
                    break;
                }
                case PLUS: {
                    g2.draw(new Line2D.Double(s * 0.5, s * 0.16, s * 0.5, s * 0.84));
                    g2.draw(new Line2D.Double(s * 0.16, s * 0.5, s * 0.84, s * 0.5));
                    break;
                }
                case CALENDAR: {
                    g2.draw(new RoundRectangle2D.Double(s * 0.12, s * 0.20, s * 0.76, s * 0.68, s * 0.16, s * 0.16));
                    g2.draw(new Line2D.Double(s * 0.12, s * 0.42, s * 0.88, s * 0.42));
                    g2.draw(new Line2D.Double(s * 0.34, s * 0.10, s * 0.34, s * 0.28));
                    g2.draw(new Line2D.Double(s * 0.66, s * 0.10, s * 0.66, s * 0.28));
                    break;
                }
                case EDIT: {
                    Path2D.Double pen = new Path2D.Double();
                    pen.moveTo(s * 0.18, s * 0.82);
                    pen.lineTo(s * 0.22, s * 0.62);
                    pen.lineTo(s * 0.66, s * 0.18);
                    pen.lineTo(s * 0.82, s * 0.34);
                    pen.lineTo(s * 0.38, s * 0.78);
                    pen.closePath();
                    g2.draw(pen);
                    break;
                }
                case SPARKLE: {
                    Path2D.Double star = new Path2D.Double();
                    star.moveTo(s * 0.45, s * 0.10);
                    star.quadTo(s * 0.50, s * 0.45, s * 0.82, s * 0.50);
                    star.quadTo(s * 0.50, s * 0.55, s * 0.45, s * 0.90);
                    star.quadTo(s * 0.40, s * 0.55, s * 0.08, s * 0.50);
                    star.quadTo(s * 0.40, s * 0.45, s * 0.45, s * 0.10);
                    g2.fill(star);
                    g2.fill(new Ellipse2D.Double(s * 0.74, s * 0.08, s * 0.16, s * 0.16));
                    break;
                }
                case CHEVRON_LEFT:
                case CHEVRON_RIGHT: {
                    boolean left = type == Type.CHEVRON_LEFT;
                    Path2D.Double p = new Path2D.Double();
                    p.moveTo(s * (left ? 0.62 : 0.38), s * 0.20);
                    p.lineTo(s * (left ? 0.32 : 0.68), s * 0.50);
                    p.lineTo(s * (left ? 0.62 : 0.38), s * 0.80);
                    g2.draw(p);
                    break;
                }
                case USERS: {
                    g2.draw(new Ellipse2D.Double(s * 0.18, s * 0.14, s * 0.30, s * 0.30));
                    g2.draw(new Arc2D.Double(s * 0.06, s * 0.52, s * 0.54, s * 0.56, 0, 180, Arc2D.OPEN));
                    g2.draw(new Arc2D.Double(s * 0.58, s * 0.20, s * 0.24, s * 0.24, -60, 260, Arc2D.OPEN));
                    g2.draw(new Arc2D.Double(s * 0.58, s * 0.56, s * 0.38, s * 0.44, 0, 110, Arc2D.OPEN));
                    break;
                }
                default:
                    break;
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // # 6. ลูกศร → (แถวโอนเงิน)
    // =====================================================================
    // ลูกศร →
    // =====================================================================
    public static class ArrowIcon implements Icon {
        private final Color color;
        private final int width;

        public ArrowIcon(Color color, int width) {
            this.color = color;
            this.width = width;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cy = y + 7;
            g2.drawLine(x + 1, cy, x + width - 2, cy);
            g2.drawLine(x + width - 7, cy - 5, x + width - 2, cy);
            g2.drawLine(x + width - 7, cy + 5, x + width - 2, cy);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return 14;
        }
    }

    // # 7. โลโก้เครื่องบินกระดาษ (ไอคอน Taskbar)
    // =====================================================================
    // โลโก้ (เครื่องบินกระดาษ)
    // =====================================================================
    public static class LogoIcon implements Icon {
        private final int size;

        public LogoIcon(int size) {
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.translate(x, y);
            double s = size;
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fill(new RoundRectangle2D.Double(0, 0, s, s, s * 0.5, s * 0.5));
            g2.setColor(new Color(255, 255, 255, 90));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, s - 1, s - 1, s * 0.5, s * 0.5));

            Path2D.Double plane = new Path2D.Double();
            plane.moveTo(s * 0.20, s * 0.50);
            plane.lineTo(s * 0.80, s * 0.24);
            plane.lineTo(s * 0.62, s * 0.78);
            plane.lineTo(s * 0.48, s * 0.58);
            plane.closePath();
            g2.setColor(Color.WHITE);
            g2.fill(plane);

            Path2D.Double fold = new Path2D.Double();
            fold.moveTo(s * 0.48, s * 0.58);
            fold.lineTo(s * 0.80, s * 0.24);
            fold.lineTo(s * 0.44, s * 0.74);
            fold.closePath();
            g2.setColor(new Color(0xB8EDE6));
            g2.fill(fold);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // # 8. ไอคอนของการ์ดสรุป: กระเป๋าเงิน / หาร / รายการ / คน
    // =====================================================================
    // ไอคอนสำหรับการ์ดสถิติ
    // =====================================================================
    public static class StatIcon implements Icon {
        public enum Type { MONEY, AVERAGE, LIST, PEOPLE }

        private final Type type;
        private final Color color;
        private final int size;

        public StatIcon(Type type, Color color, int size) {
            this.type = type;
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Theme.aa(g);
            g2.translate(x, y);
            double s = size;
            g2.setColor(Theme.soft(color));
            g2.fill(new RoundRectangle2D.Double(0, 0, s, s, s * 0.6, s * 0.6));
            g2.setColor(color);
            g2.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case MONEY: {
                    // กระเป๋าเงิน
                    g2.draw(new RoundRectangle2D.Double(s * 0.24, s * 0.32, s * 0.52, s * 0.38, s * 0.12, s * 0.12));
                    g2.draw(new Line2D.Double(s * 0.30, s * 0.26, s * 0.62, s * 0.26));
                    g2.fill(new Ellipse2D.Double(s * 0.58, s * 0.47, s * 0.08, s * 0.08));
                    break;
                }
                case AVERAGE: {
                    // เครื่องหมายหาร
                    g2.draw(new Line2D.Double(s * 0.28, s * 0.50, s * 0.72, s * 0.50));
                    g2.fill(new Ellipse2D.Double(s * 0.445, s * 0.27, s * 0.11, s * 0.11));
                    g2.fill(new Ellipse2D.Double(s * 0.445, s * 0.62, s * 0.11, s * 0.11));
                    break;
                }
                case LIST: {
                    // รายการ
                    for (int i = 0; i < 3; i++) {
                        double yy = s * (0.32 + i * 0.18);
                        g2.fill(new Ellipse2D.Double(s * 0.26, yy - s * 0.04, s * 0.08, s * 0.08));
                        g2.draw(new Line2D.Double(s * 0.42, yy, s * 0.74, yy));
                    }
                    break;
                }
                case PEOPLE: {
                    // คนสองคน
                    g2.fill(new Ellipse2D.Double(s * 0.30, s * 0.24, s * 0.18, s * 0.18));
                    g2.fill(new Arc2D.Double(s * 0.20, s * 0.46, s * 0.38, s * 0.36, 0, 180, Arc2D.CHORD));
                    g2.fill(new Ellipse2D.Double(s * 0.56, s * 0.30, s * 0.15, s * 0.15));
                    g2.fill(new Arc2D.Double(s * 0.50, s * 0.50, s * 0.30, s * 0.30, 0, 180, Arc2D.CHORD));
                    break;
                }
                default:
                    break;
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
