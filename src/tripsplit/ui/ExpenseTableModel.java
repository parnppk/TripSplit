// ############################################################################
// # ไฟล์     : ExpenseTableModel.java
// # ส่วน     : UI – ตัวเชื่อมตาราง
// # หน้าที่   : ส่งรายการค่าใช้จ่ายจาก Trip ไปแสดงใน JTable
// # จุดสำคัญ : Inheritance (extends TripTableModel), เรียก Polymorphism ของ Expense
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Expense;
import tripsplit.model.ExpenseCategory;
import tripsplit.model.Member;
import tripsplit.model.Trip;

import java.util.StringJoiner;

/** ตัวเชื่อมข้อมูลรายการค่าใช้จ่ายใน Trip เข้ากับ JTable */
public class ExpenseTableModel extends TripTableModel {

    // # 1. หัวคอลัมน์ และชนิดข้อมูลของแต่ละคอลัมน์
    private static final String[] COLUMNS = {"หมวดหมู่", "รายการ", "รายละเอียด", "ผู้จ่าย", "ผู้ร่วมหาร", "ยอดรวม (บาท)"};
    private static final Class<?>[] TYPES = {ExpenseCategory.class, String.class, String.class, Member.class, String.class, Double.class};

    // # 2. Constructor — ส่งหัวคอลัมน์ให้คลาสแม่
    public ExpenseTableModel(Trip trip) {
        super(trip, COLUMNS, TYPES);
    }

    // # 3. ดึง Expense ของแถวที่เลือก (ใช้ตอนลบรายการ)
    public Expense getExpenseAt(int row) {
        return trip.getExpenses().get(row);
    }

    // # 4. จำนวนแถว = จำนวนรายการค่าใช้จ่าย
    @Override
    public int getRowCount() {
        return trip.getExpenseCount();
    }

    // # 5. ค่าในแต่ละช่อง — เรียก getTotal() / getDetail() ของคลาสลูก (Polymorphism)
    @Override
    public Object getValueAt(int row, int column) {
        Expense e = getExpenseAt(row);
        return switch (column) {
            case 0 -> e.getCategory();
            case 1 -> e.getTitle();
            case 2 -> e.getDetail();
            case 3 -> e.getPayer();
            case 4 -> participantText(e);
            case 5 -> e.getTotal();
            default -> null;
        };
    }

    // # 6. ข้อความผู้ร่วมหาร: "ทุกคน (4 คน)" หรือรายชื่อ
    private String participantText(Expense e) {
        if (e.getParticipantCount() == trip.getMemberCount()) {
            return "ทุกคน (" + e.getParticipantCount() + " คน)";
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Member m : e.getParticipants()) {
            joiner.add(m.getName());
        }
        return joiner.toString();
    }
}
