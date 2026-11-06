// ############################################################################
// # ไฟล์     : DonutChart.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : กราฟโดนัทแสดงสัดส่วนค่าใช้จ่ายแยกตามหมวด
// # จุดสำคัญ : วาดเองด้วย Graphics2D ไม่ใช้ไลบรารีภายนอก
// ############################################################################
package tripsplit.ui.components;

import tripsplit.model.ExpenseCategory;
import tripsplit.model.Money;
import tripsplit.ui.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.Map;

/** กราฟโดนัทแสดงสัดส่วนค่าใช้จ่ายแยกตามหมวดหมู่ พร้อมคำอธิบายด้านขวา */
public class DonutChart extends JComponent {

    // # 1. ข้อมูลยอดแยกตามหมวด
    private final Map<ExpenseCategory, Double> data = new EnumMap<>(ExpenseCategory.class);

    // # 2. รับข้อมูลใหม่แล้ววาดใหม่
    public void setData(Map<ExpenseCategory, Double> newData) {
        data.clear();
        data.putAll(newData);
        repaint();
    }

    // # 3. วาดกราฟ: โดนัท → ข้อความกลาง → คำอธิบาย + แถบเปอร์เซ็นต์ด้านขวา
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight();

        double total = 0;
        for (double v : data.values()) {
            total += v;
        }

        // ---------- โดนัท ----------
        int size = (int) Math.max(80, Math.min(h - 24, w * 0.40));
        int cx = (int) Math.max(12, w * 0.06);
        int cy = (h - size) / 2;
        float thickness = size * 0.17f;
        double inset = thickness / 2.0;
        Arc2D.Double arc = new Arc2D.Double();

        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.setColor(Theme.DIVIDER);
        g2.draw(new Ellipse2D.Double(cx + inset, cy + inset, size - thickness, size - thickness));

        if (total > 0) {
            double start = 90;
            long nonZero = data.values().stream().filter(v -> v > 0).count();
            for (ExpenseCategory c : ExpenseCategory.values()) {
                double value = data.getOrDefault(c, 0.0);
                if (value <= 0) continue;
                double extent = value / total * 360.0;
                double gap = nonZero > 1 ? Math.min(2.0, extent / 3) : 0;
                arc.setArc(cx + inset, cy + inset, size - thickness, size - thickness,
                        start - gap / 2, -(extent - gap), Arc2D.OPEN);
                g2.setColor(Theme.categoryColor(c));
                g2.draw(arc);
                start -= extent;
            }
        }

        // ---------- ข้อความกลางโดนัท ----------
        g2.setFont(Theme.font(Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        String caption = total > 0 ? "ยอดรวม (บาท)" : "ยังไม่มีข้อมูล";
        g2.setColor(Theme.MUTED);
        int centerX = cx + size / 2;
        int centerY = cy + size / 2;
        g2.drawString(caption, centerX - fm.stringWidth(caption) / 2, centerY - (total > 0 ? 6 : -4));
        if (total > 0) {
            g2.setFont(Theme.font(Theme.DISPLAY, size > 170 ? 22 : 17));
            fm = g2.getFontMetrics();
            String amount = Money.format(total);
            g2.setColor(Theme.TEXT);
            g2.drawString(amount, centerX - fm.stringWidth(amount) / 2, centerY + fm.getAscent() - 2);
        }

        // ---------- คำอธิบาย (Legend) ----------
        int lx = cx + size + (int) Math.max(24, w * 0.05);
        int lw = Math.max(120, w - lx - 20);
        int rowH = 46;
        int ly = Math.max(8, (h - rowH * ExpenseCategory.values().length) / 2);

        for (ExpenseCategory c : ExpenseCategory.values()) {
            double value = data.getOrDefault(c, 0.0);
            double pct = total > 0 ? value / total : 0;
            Color color = Theme.categoryColor(c);

            g2.setColor(color);
            g2.fillRoundRect(lx, ly + 5, 10, 10, 4, 4);

            g2.setFont(Theme.font(Font.BOLD, 13));
            fm = g2.getFontMetrics();
            g2.setColor(Theme.TEXT);
            g2.drawString(c.getDisplayName(), lx + 18, ly + 15);

            String right = Money.format(value) + "   " + String.format("%.0f%%", pct * 100);
            g2.setFont(Theme.font(Font.PLAIN, 13));
            fm = g2.getFontMetrics();
            g2.setColor(Theme.MUTED);
            g2.drawString(right, lx + lw - fm.stringWidth(right), ly + 15);

            int barY = ly + 24;
            g2.setColor(Theme.DIVIDER);
            g2.fillRoundRect(lx, barY, lw, 7, 7, 7);
            g2.setColor(color);
            int fill = (int) Math.round(lw * pct);
            if (fill > 0) {
                g2.fillRoundRect(lx, barY, Math.max(7, fill), 7, 7, 7);
            }
            ly += rowH;
        }
        g2.dispose();
    }
}
