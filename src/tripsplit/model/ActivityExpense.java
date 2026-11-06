// ############################################################################
// # ไฟล์     : ActivityExpense.java
// # ส่วน     : MODEL – คลาสลูก
// # หน้าที่   : ค่ากิจกรรม/ตั๋ว กรอกราคาแยกรายคน (คนที่ไม่ได้ไป/ไม่ได้จ่าย กรอก 0)
// # จุดสำคัญ : Inheritance, Polymorphism (override ทั้ง getTotal และ getShareFor)
// ############################################################################
package tripsplit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ค่ากิจกรรม/ตั๋ว – แต่ละคนจ่ายไม่เท่ากันได้
 * สูตร: ยอดรวม = ผลรวมราคาของทุกคน และแต่ละคนรับผิดชอบเท่าราคาของตัวเอง
 * ตัวอย่าง: arm 450, bill 300, tiw 0 → ยอดรวม 750 (tiw ไม่ต้องจ่าย)
 */
public class ActivityExpense extends Expense {

    // # 1. ข้อมูล: ราคาของสมาชิกแต่ละคน (เรียงตามลำดับที่กรอก)
    private final Map<Member, Double> prices;

    // # 2. Constructor — ตรวจราคาทุกช่อง (≥ 0) และต้องมีอย่างน้อย 1 คนที่ราคามากกว่า 0
    public ActivityExpense(String title, Member payer, Map<Member, Double> pricePerMember) {
        super(title, payer, participantsOf(pricePerMember));
        Map<Member, Double> copy = new LinkedHashMap<>();
        for (Map.Entry<Member, Double> e : pricePerMember.entrySet()) {
            if (e.getValue() > 0) {
                copy.put(e.getKey(), e.getValue());
            }
        }
        this.prices = Collections.unmodifiableMap(copy); // Encapsulation: ภายนอกแก้ไม่ได้
    }

    /** คนที่ราคามากกว่า 0 = ผู้ร่วมกิจกรรม (ตรวจข้อมูลก่อนส่งให้คลาสแม่) */
    private static List<Member> participantsOf(Map<Member, Double> pricePerMember) {
        if (pricePerMember == null || pricePerMember.isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอกราคาของสมาชิก");
        }
        List<Member> list = new ArrayList<>();
        for (Map.Entry<Member, Double> e : pricePerMember.entrySet()) {
            double v = e.getValue() == null ? 0 : e.getValue();
            if (Double.isNaN(v) || Double.isInfinite(v) || v < 0) {
                throw new IllegalArgumentException("ราคาของ " + e.getKey().getName() + " ต้องไม่ติดลบ");
            }
            if (v > 0) {
                list.add(e.getKey());
            }
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอกราคาอย่างน้อย 1 คน (ที่เหลือกรอก 0 ได้)");
        }
        return list;
    }

    // # 3. Override — ยอดรวม = ผลรวมราคาของทุกคน
    @Override
    public ExpenseCategory getCategory() {
        return ExpenseCategory.ACTIVITY;
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (double v : prices.values()) {
            total += v;
        }
        return total;
    }

    // # 4. Override getShareFor — แต่ละคนจ่ายเท่าราคาของตัวเอง (ต่างจากคลาสแม่ที่หารเท่ากัน)
    @Override
    public double getShareFor(Member member) {
        return prices.getOrDefault(member, 0.0);
    }

    /** เช่น "arm 450.00 · bill 300.00" */
    @Override
    public String getDetail() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Member, Double> e : prices.entrySet()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(e.getKey().getName()).append(' ').append(Money.format(e.getValue()));
        }
        return sb.toString();
    }

    // # 5. Getter — ราคาของแต่ละคน (อ่านได้อย่างเดียว)
    public Map<Member, Double> getPrices() {
        return prices;
    }
}
