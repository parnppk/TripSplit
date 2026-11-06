// ############################################################################
// # ไฟล์     : Expense.java
// # ส่วน     : MODEL – คลาสแม่ (abstract)
// # หน้าที่   : ค่าใช้จ่าย 1 รายการ: ชื่อ, ผู้จ่าย, ผู้ร่วมหาร และพฤติกรรมพื้นฐาน
// # จุดสำคัญ : Abstraction, Encapsulation, Inheritance (เป็นคลาสแม่), Has-a (มี Member)
// ############################################################################
package tripsplit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * ค่าใช้จ่าย 1 รายการ (Abstract Class)
 * <ul>
 *   <li>Abstraction  – สร้าง Object ตรง ๆ ไม่ได้ ต้องใช้คลาสลูก</li>
 *   <li>Encapsulation – field เป็น private ทั้งหมด เข้าถึงผ่าน getter</li>
 *   <li>Has-a        – Expense มีผู้จ่าย (Member) และมีผู้ร่วมหารหลายคน</li>
 *   <li>Polymorphism – คลาสลูกแต่ละแบบคำนวณ getTotal() ไม่เหมือนกัน</li>
 * </ul>
 */
public abstract class Expense implements Splittable {

    // # 1. ข้อมูล (field) — private ทั้งหมด = Encapsulation
    private final String title;
    private final Member payer;
    private final List<Member> participants;

    // # 2. Constructor — ตรวจข้อมูลก่อนสร้าง (protected = ให้คลาสลูกเรียกผ่าน super เท่านั้น)
    protected Expense(String title, Member payer, List<Member> participants) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกชื่อรายการค่าใช้จ่าย");
        }
        if (payer == null) {
            throw new IllegalArgumentException("กรุณาเลือกผู้จ่ายเงิน");
        }
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("กรุณาเลือกผู้ร่วมหารอย่างน้อย 1 คน");
        }
        this.title = title.trim();
        this.payer = payer;
        // ตัดชื่อซ้ำออก และ copy list เพื่อไม่ให้ภายนอกแก้ไขข้อมูลภายในได้
        this.participants = new ArrayList<>(new LinkedHashSet<>(participants));
    }

    // ---------- abstract methods: คลาสลูกต้องกำหนดเอง ----------

    // # 3. Abstract methods — คลาสลูกต้องเขียนเอง แต่ละแบบคิดต่างกัน (Polymorphism)
    /** หมวดหมู่ของค่าใช้จ่าย */
    public abstract ExpenseCategory getCategory();

    /** ยอดรวม – แต่ละประเภทมีสูตรต่างกัน (Polymorphism) */
    @Override
    public abstract double getTotal();

    /** คำอธิบายสั้น ๆ ว่ายอดนี้มาจากไหน เช่น "2 ห้อง × 3 คืน × 1,200.00" */
    public abstract String getDetail();

    // ---------- พฤติกรรมพื้นฐาน (คลาสลูก override ได้) ----------

    // # 4. getShareFor — ค่าเริ่มต้น: หารเท่ากันทุกคน (คลาสลูก override ได้)
    /** ค่าเริ่มต้น: หารเท่ากันทุกคนที่ร่วมหาร */
    @Override
    public double getShareFor(Member member) {
        if (!participants.contains(member)) {
            return 0;
        }
        return getTotal() / participants.size();
    }

    // # 5. involves — เป็นผู้จ่ายหรือผู้ร่วมหาร
    @Override
    public boolean involves(Member member) {
        return payer.equals(member) || participants.contains(member);
    }

    // ---------- getters ----------

    // # 6. Getters — อ่านค่าได้อย่างเดียว / getParticipants คืน list ที่แก้ไม่ได้
    public String getTitle() {
        return title;
    }

    public Member getPayer() {
        return payer;
    }

    public List<Member> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    // ---------- helper สำหรับตรวจสอบข้อมูลในคลาสลูก ----------

    /** คำอธิบายแบบหารเท่ากัน เช่น "หารเท่ากัน 4 คน" (คลาสลูกใช้ร่วมกันใน getDetail) */
    protected String equalSplitDetail() {
        return "หารเท่ากัน " + participants.size() + " คน";
    }

    // # 7. ตัวช่วยตรวจตัวเลขต้องมากกว่า 0 (คลาสลูกใช้ร่วมกัน)
    protected static double requirePositive(double value, String label) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0) {
            throw new IllegalArgumentException(label + " ต้องมากกว่า 0");
        }
        return value;
    }

    protected static int requirePositive(int value, String label) {
        if (value <= 0) {
            throw new IllegalArgumentException(label + " ต้องมากกว่า 0");
        }
        return value;
    }

    // # 8. toString — ข้อความสรุปของรายการ
    @Override
    public String toString() {
        return getCategory().getDisplayName() + ": " + title + " (" + Money.format(getTotal()) + " บาท)";
    }
}
