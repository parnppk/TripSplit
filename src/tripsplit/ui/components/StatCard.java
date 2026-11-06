// ############################################################################
// # ไฟล์     : StatCard.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : การ์ดตัวเลขสรุป 4 ใบด้านบน (ยอดรวม, เฉลี่ย, รายการ, สมาชิก)
// # จุดสำคัญ : Inheritance (extends RoundedPanel)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.geom.RoundRectangle2D;

/**
 * การ์ดแสดงตัวเลขสรุป เช่น ยอดใช้จ่ายรวม
 * เนื้อหา (ไอคอน + ตัวเลข) จัดอยู่กึ่งกลางการ์ดทั้งแนวนอนและแนวตั้ง
 * มีแถบสีประจำการ์ดด้านบน และบรรทัดสรุปสั้น ๆ ใต้ตัวเลข
 */
public class StatCard extends RoundedPanel {

    // # 1. ข้อมูล: สีประจำการ์ด, ตัวเลข, บรรทัดสรุป
    private final Color accent;
    private final JLabel valueLabel = new JLabel("0");
    private final JLabel noteLabel = new JLabel(" ");

    // # 2. Constructor — จัดกลุ่ม ไอคอน + ข้อความ ให้อยู่กึ่งกลางการ์ด (GridBagLayout)
    public StatCard(String title, String unit, Icons.StatIcon.Type type, Color accent) {
        super(16, Color.WHITE);
        this.accent = accent;
        setLayout(new GridBagLayout()); // ทำให้กลุ่มเนื้อหาอยู่กึ่งกลางการ์ด
        setBorder(BorderFactory.createEmptyBorder(18, 14, 19, 14));

        // ---------- ข้อความ: หัวข้อ / ตัวเลข + หน่วย / บรรทัดสรุป ----------
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.font(Font.BOLD, 13));
        titleLabel.setForeground(Theme.MUTED);

        valueLabel.setFont(Theme.font(Theme.DISPLAY, 26));
        valueLabel.setForeground(Theme.TEXT);
        JLabel unitLabel = new JLabel(unit);
        unitLabel.setFont(Theme.font(Font.PLAIN, 13));
        unitLabel.setForeground(Theme.MUTED);

        // ตัวเลข + หน่วย วางบนเส้นฐานเดียวกัน และไม่ตัดบรรทัดแม้การ์ดแคบ (GridBag BASELINE)
        JPanel valueRow = new JPanel(new java.awt.GridBagLayout());
        valueRow.setOpaque(false);
        java.awt.GridBagConstraints vc = new java.awt.GridBagConstraints();
        vc.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        valueRow.add(valueLabel, vc);
        vc.insets = new java.awt.Insets(0, 6, 0, 0);
        vc.weightx = 1;
        valueRow.add(unitLabel, vc);

        noteLabel.setFont(Theme.font(Font.PLAIN, 12));
        noteLabel.setForeground(Theme.mix(accent, Theme.MUTED, 0.35));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{titleLabel, valueRow, noteLabel}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
        }
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(1));
        text.add(valueRow);
        text.add(Box.createVerticalStrut(2));
        text.add(noteLabel);

        // ---------- กลุ่มเนื้อหา: ไอคอน + ข้อความ ----------
        JPanel group = new JPanel(new BorderLayout(14, 0));
        group.setOpaque(false);
        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(new JLabel(new Icons.StatIcon(type, accent, 50)));
        group.add(iconWrap, BorderLayout.WEST);
        group.add(text, BorderLayout.CENTER);

        add(group);
    }

    // # 3. ขนาดการ์ด: กว้างเท่ากันทุกใบ สูงตามเนื้อหา
    /** ความกว้างให้ GridLayout แบ่งเท่ากัน ส่วนความสูงคำนวณจากเนื้อหาจริง */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(160, Math.max(118, super.getPreferredSize().height));
    }

    // # 4. ตั้งค่าตัวเลข / บรรทัดสรุป
    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /** บรรทัดสรุปสั้น ๆ ใต้ตัวเลข */
    public void setNote(String note) {
        noteLabel.setText(note == null || note.isEmpty() ? " " : note);
        noteLabel.setToolTipText(note);
    }

    // # 5. วาดการ์ด + แถบสีด้านบน
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // แถบสีด้านบนการ์ด (ตัดให้โค้งตามมุมการ์ด)
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight() - 3;
        g2.clip(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 32, 32));
        g2.setColor(accent);
        g2.fillRect(0, 0, w, 4);
        g2.dispose();
    }
}
