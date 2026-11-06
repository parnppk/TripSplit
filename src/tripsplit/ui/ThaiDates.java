// ############################################################################
// # ไฟล์     : ThaiDates.java
// # ส่วน     : UI – เครื่องมือวันที่
// # หน้าที่   : แปลงวันที่เป็นภาษาไทย ปี พ.ศ. เช่น "16 – 18 ตุลาคม 2569"
// # จุดสำคัญ : static method
// ############################################################################
package tripsplit.ui;

import java.time.LocalDate;

/** จัดรูปแบบวันที่เป็นภาษาไทย ปี พ.ศ. เช่น "16 – 18 ตุลาคม 2569" */
public final class ThaiDates {

    // # 1. ชื่อเดือน / ชื่อวันภาษาไทย
    public static final String[] MONTHS = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
            "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    public static final String[] SHORT_MONTHS = {"ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
            "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."};
    /** ชื่อวันแบบย่อ เรียงจากวันจันทร์ */
    public static final String[] WEEKDAYS = {"จ", "อ", "พ", "พฤ", "ศ", "ส", "อา"};

    private ThaiDates() {
    }

    // # 2. แปลง ค.ศ. → พ.ศ. (+543)
    public static int buddhistYear(LocalDate d) {
        return d.getYear() + 543;
    }

    // # 3. รูปแบบวันที่เต็ม "16 ตุลาคม 2569" / ย่อ "16 ต.ค. 2569"
    /** "16 ตุลาคม 2569" */
    public static String format(LocalDate d) {
        return d.getDayOfMonth() + " " + MONTHS[d.getMonthValue() - 1] + " " + buddhistYear(d);
    }

    /** "16 ต.ค. 2569" */
    public static String formatShort(LocalDate d) {
        return d.getDayOfMonth() + " " + SHORT_MONTHS[d.getMonthValue() - 1] + " " + buddhistYear(d);
    }

    // # 4. ช่วงวันที่ เช่น "16 – 18 ตุลาคม 2569"
    /**
     * ช่วงวันที่แบบอ่านง่าย
     * <ul>
     *   <li>วันเดียว: 16 ตุลาคม 2569</li>
     *   <li>เดือนเดียวกัน: 16 – 18 ตุลาคม 2569</li>
     *   <li>ข้ามเดือน: 30 ต.ค. – 2 พ.ย. 2569</li>
     *   <li>ข้ามปี: 30 ธ.ค. 2569 – 2 ม.ค. 2570</li>
     * </ul>
     */
    public static String formatRange(LocalDate start, LocalDate end) {
        if (start.equals(end)) {
            return format(start);
        }
        if (start.getYear() != end.getYear()) {
            return formatShort(start) + " – " + formatShort(end);
        }
        if (start.getMonthValue() != end.getMonthValue()) {
            return start.getDayOfMonth() + " " + SHORT_MONTHS[start.getMonthValue() - 1] + " – "
                    + formatShort(end);
        }
        return start.getDayOfMonth() + " – " + format(end);
    }

    // # 5. จำนวนวัน เช่น "3 วัน 2 คืน"
    /** "3 วัน 2 คืน" หรือ "1 วัน" */
    public static String duration(int days) {
        return days <= 1 ? "1 วัน" : days + " วัน " + (days - 1) + " คืน";
    }
}
