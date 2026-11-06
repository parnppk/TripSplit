// ############################################################################
// # ไฟล์     : BalanceTableModel.java
// # ส่วน     : UI – ตัวเชื่อมตาราง
// # หน้าที่   : ส่งข้อมูลยอดรายคนจาก Trip ไปแสดงใน JTable (แท็บ "ยอดรายคน")
// # จุดสำคัญ : Inheritance (extends TripTableModel)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Member;
import tripsplit.model.Trip;

/** ตารางสรุปยอดของสมาชิกแต่ละคน */
public class BalanceTableModel extends TripTableModel {

    // # 1. หัวคอลัมน์ และชนิดข้อมูลของแต่ละคอลัมน์ (ใช้เลือก Renderer)
    private static final String[] COLUMNS = {"สมาชิก", "จ่ายไปแล้ว", "ส่วนที่ต้องจ่ายจริง", "สถานะ"};
    private static final Class<?>[] TYPES = {Member.class, Double.class, Double.class, Double.class};

    // # 2. Constructor — ส่งหัวคอลัมน์ให้คลาสแม่
    public BalanceTableModel(Trip trip) {
        super(trip, COLUMNS, TYPES);
    }

    // # 3. จำนวนแถว = จำนวนสมาชิก
    @Override
    public int getRowCount() {
        return trip.getMemberCount();
    }

    // # 4. ค่าในแต่ละช่อง: สมาชิก, จ่ายไปแล้ว, ส่วนที่ต้องจ่าย, ยอดสุทธิ
    @Override
    public Object getValueAt(int row, int column) {
        Member m = trip.getMembers().get(row);
        return switch (column) {
            case 0 -> m;
            case 1 -> trip.getTotalPaidBy(m);
            case 2 -> trip.getTotalShareOf(m);
            case 3 -> trip.getBalance(m);
            default -> null;
        };
    }
}
