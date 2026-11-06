// ############################################################################
// # ไฟล์     : HotelExpense.java
// # ส่วน     : MODEL – คลาสลูก
// # หน้าที่   : ค่าที่พัก = ราคาต่อคืน × จำนวนคืน × จำนวนห้อง
// # จุดสำคัญ : Inheritance (extends Expense), Polymorphism (override getTotal)
// ############################################################################
package tripsplit.model;

import java.util.List;

/**
 * ค่าที่พัก
 * สูตร: ยอดรวม = ราคาต่อคืน × จำนวนคืน × จำนวนห้อง
 */
public class HotelExpense extends Expense {

    // # 1. ข้อมูล: ราคาต่อคืน, จำนวนคืน, จำนวนห้อง
    private final double pricePerNight;
    private final int nights;
    private final int rooms;

    // # 2. Constructor — ตรวจตัวเลขทุกช่องต้องมากกว่า 0
    public HotelExpense(String title, Member payer, List<Member> participants,
                        double pricePerNight, int nights, int rooms) {
        super(title, payer, participants);
        this.pricePerNight = requirePositive(pricePerNight, "ราคาต่อคืน");
        this.nights = requirePositive(nights, "จำนวนคืน");
        this.rooms = requirePositive(rooms, "จำนวนห้อง");
    }

    // # 3. Override จากคลาสแม่ — ยอดรวม = ราคา × คืน × ห้อง
    @Override
    public ExpenseCategory getCategory() {
        return ExpenseCategory.HOTEL;
    }

    @Override
    public double getTotal() {
        return pricePerNight * nights * rooms;
    }

    @Override
    public String getDetail() {
        return rooms + " ห้อง × " + nights + " คืน × " + Money.format(pricePerNight);
    }

    // # 4. Getters
    public double getPricePerNight() {
        return pricePerNight;
    }

    public int getNights() {
        return nights;
    }

    public int getRooms() {
        return rooms;
    }
}
