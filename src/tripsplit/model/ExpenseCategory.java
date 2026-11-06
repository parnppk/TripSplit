// ############################################################################
// # ไฟล์     : ExpenseCategory.java
// # ส่วน     : MODEL – enum
// # หน้าที่   : หมวดหมู่ค่าใช้จ่าย 4 หมวด พร้อมชื่อภาษาไทย
// # จุดสำคัญ : Encapsulation (ชื่อหมวดเป็น private)
// ############################################################################
package tripsplit.model;

/** หมวดหมู่ของค่าใช้จ่าย */
public enum ExpenseCategory {
    // # 1. ค่าของ enum 4 หมวด พร้อมชื่อภาษาไทย
    FOOD("อาหาร"),
    TRANSPORT("เดินทาง"),
    HOTEL("ที่พัก"),
    ACTIVITY("กิจกรรม");

    // # 2. ชื่อที่แสดงบนหน้าจอ
    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    // # 3. Getter + toString
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
