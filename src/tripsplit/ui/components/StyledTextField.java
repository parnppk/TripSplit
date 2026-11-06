// ############################################################################
// # ไฟล์     : StyledTextField.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ช่องกรอกข้อความมุมโค้ง + ข้อความตัวอย่าง + อ่านค่าเป็นตัวเลข
// # จุดสำคัญ : Inheritance (extends JTextField)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** ช่องกรอกข้อความมุมโค้ง พร้อมข้อความตัวอย่าง (placeholder) */
public class StyledTextField extends JTextField {

    // # 1. ข้อความตัวอย่างตอนช่องว่าง
    private final String placeholder;

    // # 2. Constructor — ฟอนต์, สี, เส้นขอบมุมโค้ง
    public StyledTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(Theme.font(Font.PLAIN, 14));
        setForeground(Theme.TEXT);
        setBackground(Color.WHITE);
        setCaretColor(Theme.PRIMARY);
        setBorder(new RoundedBorder());
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    // # 3. ★ อ่านค่าเป็นตัวเลข + ตรวจข้อมูล (ว่าง / ไม่ใช่ตัวเลข / ≤ 0 / มากเกิน)
    /** อ่านค่าเป็นตัวเลข (รองรับการพิมพ์มีจุลภาค เช่น 1,200) */
    public double getNumber(String label) {
        String text = getText().trim().replace(",", "");
        if (text.isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอก" + label);
        }
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " ต้องเป็นตัวเลขเท่านั้น");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(label + " ต้องมากกว่า 0");
        }
        if (value > 10_000_000) {
            throw new IllegalArgumentException(label + " สูงเกินไป (ไม่เกิน 10,000,000 บาท)");
        }
        return value;
    }

    // # 4. วาดข้อความตัวอย่าง (placeholder)
    /** อ่านค่าเป็นตัวเลขที่เป็น 0 ได้ (ช่องว่าง = 0) ใช้กับราคารายคนของกิจกรรม */
    public double getAmountOrZero(String label) {
        String text = getText().trim().replace(",", "");
        if (text.isEmpty()) {
            return 0;
        }
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " ต้องเป็นตัวเลขเท่านั้น");
        }
        if (value < 0) {
            throw new IllegalArgumentException(label + " ต้องไม่ติดลบ (ไม่ได้จ่ายให้กรอก 0)");
        }
        if (value > 10_000_000) {
            throw new IllegalArgumentException(label + " สูงเกินไป (ไม่เกิน 10,000,000 บาท)");
        }
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder != null && getText().isEmpty()) {
            Graphics2D g2 = Theme.aa(g);
            g2.setColor(Theme.PLACEHOLDER);
            g2.setFont(getFont());
            Insets in = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = in.top + (getHeight() - in.top - in.bottom - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, in.left + 1, y);
            g2.dispose();
        }
    }
}
