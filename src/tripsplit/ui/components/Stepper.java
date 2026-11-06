// ############################################################################
// # ไฟล์     : Stepper.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ตัวเลือกตัวเลขแบบ [ - ] 2 คืน [ + ] ใช้กับจำนวนคืน/ห้อง
// # จุดสำคัญ : Encapsulation (ค่าถูกจำกัดช่วง min–max)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/** ตัวเลือกตัวเลขแบบปุ่ม [ - ] 2 คืน [ + ] */
public class Stepper extends JPanel {

    // # 1. ข้อมูล: ค่าต่ำสุด/สูงสุด, หน่วย, ค่าปัจจุบัน, listener
    private final int min;
    private final int max;
    private final String unit;
    private int value;
    private final JLabel label = new JLabel("", SwingConstants.CENTER);
    private final List<Runnable> listeners = new ArrayList<>();

    // # 2. Constructor — ปุ่ม [ - ] ค่า [ + ]
    public Stepper(int value, int min, int max, String unit) {
        super(new BorderLayout());
        this.min = min;
        this.max = max;
        this.unit = unit;
        setOpaque(false);
        setBorder(new RoundedBorder(10, new Insets(3, 3, 3, 3)));

        ModernButton minus = new ModernButton("-", ModernButton.Style.GHOST).withPadding(3, 13);
        ModernButton plus = new ModernButton("+", ModernButton.Style.GHOST).withPadding(3, 12);
        minus.setFont(Theme.font(Font.BOLD, 17));
        plus.setFont(Theme.font(Font.BOLD, 17));
        minus.setToolTipText("ลด");
        plus.setToolTipText("เพิ่ม");
        minus.addActionListener(e -> setValue(this.value - 1));
        plus.addActionListener(e -> setValue(this.value + 1));

        label.setFont(Theme.font(Font.BOLD, 14));
        label.setForeground(Theme.TEXT);

        add(minus, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        add(plus, BorderLayout.EAST);
        setValue(value);
    }

    // # 3. อ่าน / ตั้งค่า (จำกัดให้อยู่ในช่วง min–max)
    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        int clamped = Math.max(min, Math.min(max, newValue));
        boolean changed = clamped != value;
        value = clamped;
        label.setText(value + " " + unit);
        if (changed) {
            listeners.forEach(Runnable::run);
        }
    }

    // # 4. แจ้งเมื่อค่าเปลี่ยน (ใช้อัปเดตยอดตัวอย่าง)
    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }
}
