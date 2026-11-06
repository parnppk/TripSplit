// ############################################################################
// # ไฟล์     : FoodExpense.java
// # ส่วน     : MODEL – คลาสลูก
// # หน้าที่   : ค่าอาหาร เลือกบวก Service Charge 10% ได้
// # จุดสำคัญ : Inheritance (extends Expense), Polymorphism (override getTotal)
// ############################################################################
package tripsplit.model;

import java.util.List;

/**
 * ค่าอาหาร – เลือกบวก Service Charge 10% ได้
 * สูตร: ยอดรวม = ค่าอาหาร × 1.10 (ถ้ามี Service Charge)
 */
public class FoodExpense extends Expense {

    // # 1. ค่าคงที่: อัตรา Service Charge 10%
    public static final double SERVICE_CHARGE_RATE = 0.10;

    // # 2. ข้อมูลเฉพาะของค่าอาหาร
    private final double amount;
    private final boolean serviceCharge;

    // # 3. Constructor — ส่งข้อมูลร่วม (ชื่อ ผู้จ่าย ผู้ร่วมหาร) ให้คลาสแม่ด้วย super(...)
    public FoodExpense(String title, Member payer, List<Member> participants,
                       double amount, boolean serviceCharge) {
        super(title, payer, participants);
        this.amount = requirePositive(amount, "ค่าอาหาร");
        this.serviceCharge = serviceCharge;
    }

    // # 4. Override จากคลาสแม่ — ยอดรวม = ค่าอาหาร × 1.10 (ถ้ามี SC)
    @Override
    public ExpenseCategory getCategory() {
        return ExpenseCategory.FOOD;
    }

    @Override
    public double getTotal() {
        return serviceCharge ? amount * (1 + SERVICE_CHARGE_RATE) : amount;
    }

    @Override
    public String getDetail() {
        if (serviceCharge) {
            return Money.format(amount) + " + SC 10%";
        }
        return equalSplitDetail();
    }

    // # 5. Getters
    public double getAmount() {
        return amount;
    }

    public boolean hasServiceCharge() {
        return serviceCharge;
    }
}
