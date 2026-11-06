// ############################################################################
// # ไฟล์     : Member.java
// # ส่วน     : MODEL
// # หน้าที่   : สมาชิกในทริป 1 คน
// # จุดสำคัญ : Encapsulation (ชื่อ private final + ตรวจข้อมูลใน constructor)
// ############################################################################
package tripsplit.model;

import java.util.Locale;

/**
 * สมาชิกในทริป
 * <p>
 * Encapsulation: ชื่อเป็น private final แก้ไขจากภายนอกไม่ได้
 * และตรวจสอบความถูกต้องตั้งแต่ตอนสร้าง Object
 */
public class Member {

    // # 1. ค่าคงที่: ความยาวชื่อสูงสุด
    public static final int MAX_NAME_LENGTH = 30;

    // # 2. ข้อมูล: ชื่อ (private final = แก้ไขไม่ได้หลังสร้าง)
    private final String name;

    // # 3. Constructor — ตรวจชื่อว่าง/ยาวเกิน แล้วตัดช่องว่างหน้า-หลัง
    public Member(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกชื่อสมาชิก");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("ชื่อสมาชิกยาวได้ไม่เกิน " + MAX_NAME_LENGTH + " ตัวอักษร");
        }
        this.name = trimmed;
    }

    // # 4. Getter
    public String getName() {
        return name;
    }

    // # 5. equals / hashCode — ชื่อเดียวกัน = คนเดียวกัน (ใช้กันชื่อซ้ำ)
    /** สมาชิกสองคนถือว่าเป็นคนเดียวกันถ้าชื่อเหมือนกัน (ไม่สนตัวพิมพ์เล็ก-ใหญ่) */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        return name.equalsIgnoreCase(((Member) o).name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.ROOT).hashCode();
    }

    // # 6. toString — ให้ ComboBox / ข้อความแสดงชื่อ
    @Override
    public String toString() {
        return name;
    }
}
