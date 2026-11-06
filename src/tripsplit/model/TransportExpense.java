// ############################################################################
// # ไฟล์     : TransportExpense.java
// # ส่วน     : MODEL – คลาสลูก
// # หน้าที่   : ค่าเดินทาง เช่น น้ำมัน ทางด่วน ค่ารถ หารเท่ากัน
// # จุดสำคัญ : Inheritance (extends Expense), Polymorphism (override getTotal)
// ############################################################################
package tripsplit.model;

import java.util.List;

/**
 * ค่าเดินทาง เช่น ค่าน้ำมัน ค่าทางด่วน ค่ารถ
 * สูตร: ยอดรวม = จำนวนเงินที่จ่าย แล้วหารเท่ากัน
 */
public class TransportExpense extends Expense {

    // # 1. ข้อมูล: จำนวนเงินค่าเดินทาง
    private final double amount;

    // # 2. Constructor — ส่งข้อมูลร่วมให้คลาสแม่ด้วย super(...)
    public TransportExpense(String title, Member payer, List<Member> participants, double amount) {
        super(title, payer, participants);
        this.amount = requirePositive(amount, "ค่าเดินทาง");
    }

    // # 3. Override จากคลาสแม่ — ยอดรวม = จำนวนเงิน (หารเท่ากัน)
    @Override
    public ExpenseCategory getCategory() {
        return ExpenseCategory.TRANSPORT;
    }

    @Override
    public double getTotal() {
        return amount;
    }

    @Override
    public String getDetail() {
        return equalSplitDetail();
    }

    // # 4. Getter
    public double getAmount() {
        return amount;
    }
}
