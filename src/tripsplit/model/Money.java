// ############################################################################
// # ไฟล์     : Money.java
// # ส่วน     : MODEL – คลาสเครื่องมือ
// # หน้าที่   : จัดรูปแบบตัวเลขเงิน เช่น 1234.5 → "1,234.50"
// # จุดสำคัญ : static method, ห้ามสร้าง Object
// ############################################################################
package tripsplit.model;

import java.text.DecimalFormat;

/** เครื่องมือจัดรูปแบบตัวเลขเงิน เช่น 1234.5 -> "1,234.50" */
public final class Money {

    // # 1. รูปแบบตัวเลข #,##0.00
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.00");

    // # 2. Constructor private = ห้ามสร้าง Object (เป็นคลาสเครื่องมือ)
    private Money() {
    }

    // # 3. format — แปลงตัวเลขเป็นข้อความเงิน (กัน -0.00)
    public static String format(double value) {
        if (Math.abs(value) < 0.005) {
            value = 0; // กัน "-0.00"
        }
        synchronized (FORMAT) {
            return FORMAT.format(value);
        }
    }

    // # 4. round — ปัดทศนิยม 2 ตำแหน่ง
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
