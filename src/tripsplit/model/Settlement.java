// ############################################################################
// # ไฟล์     : Settlement.java
// # ส่วน     : MODEL
// # หน้าที่   : ผลการเคลียร์เงิน 1 รายการ: ใครโอนให้ใคร เท่าไร
// # จุดสำคัญ : Encapsulation (ข้อมูลอ่านได้อย่างเดียว)
// ############################################################################
package tripsplit.model;

/** ผลการเคลียร์เงิน 1 รายการ: from ต้องโอนให้ to เป็นจำนวน amount บาท */
public class Settlement {

    // # 1. ข้อมูล: ใครโอน (from) → ให้ใคร (to) → เท่าไร (amount)
    private final Member from;
    private final Member to;
    private final double amount;

    // # 2. Constructor
    public Settlement(Member from, Member to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    // # 3. Getters
    public Member getFrom() {
        return from;
    }

    public Member getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    // # 4. toString — เช่น "เบียร์ โอนให้ มิ้นท์  1,987.00 บาท"
    @Override
    public String toString() {
        return from.getName() + " โอนให้ " + to.getName() + "  " + Money.format(amount) + " บาท";
    }
}
