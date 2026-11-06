// ############################################################################
// # ไฟล์     : Trip.java
// # ส่วน     : MODEL – ศูนย์กลางของระบบ
// # หน้าที่   : เก็บข้อมูลทริป สมาชิก ค่าใช้จ่าย และ Logic การคำนวณทั้งหมด (UI แค่เรียกใช้)
// # จุดสำคัญ : Has-many (มี Member/Expense หลายตัว), Encapsulation, Polymorphism
// ############################################################################
package tripsplit.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ทริป 1 ทริป – เป็นศูนย์กลางของ Model
 * <ul>
 *   <li>Has-many: Trip มีสมาชิกหลายคน และมีค่าใช้จ่ายหลายรายการ</li>
 *   <li>รวม Logic การคำนวณทั้งหมดไว้ที่นี่ (UI แค่เรียกใช้)</li>
 * </ul>
 */
public class Trip {

    // # 1. ค่าคงที่: สมาชิกสูงสุด 20 คน / ทริปยาวสุด 60 วัน / ชื่อทริปยาวสุด 40 ตัวอักษร
    public static final int MAX_MEMBERS = 20;
    public static final int MAX_DAYS = 60;
    public static final int MAX_NAME_LENGTH = 40;

    // # 2. ข้อมูลทริป — Has-many: มีสมาชิกหลายคน และค่าใช้จ่ายหลายรายการ
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private final List<Member> members = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>();

    // # 3. Constructor — ค่าเริ่มต้น: วันนี้ ถึง อีก 2 วัน
    public Trip(String name) {
        setName(name);
        LocalDate today = LocalDate.now();
        setDates(today, today.plusDays(2));
    }

    // ===================== ข้อมูลทริป =====================

    // # 4. ชื่อทริป (getter / setter พร้อมตรวจข้อมูล)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = validateName(name);
    }

    /** ตรวจชื่อทริป แล้วคืนชื่อที่ตัดช่องว่างแล้ว (static = หน้าต่าง TripDialog เรียกตรวจก่อนบันทึกได้) */
    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกชื่อทริป");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("ชื่อทริปยาวได้ไม่เกิน " + MAX_NAME_LENGTH + " ตัวอักษร");
        }
        return trimmed;
    }

    // ===================== วันที่เดินทาง =====================

    // # 5. วันเดินทาง — ตรวจวันกลับต้องไม่ก่อนวันไป และไม่เกิน 60 วัน
    /** กำหนดวันเริ่มและวันสิ้นสุดของทริป (วันสิ้นสุดต้องไม่ก่อนวันเริ่ม) */
    public void setDates(LocalDate start, LocalDate end) {
        validateDates(start, end);
        this.startDate = start;
        this.endDate = end;
    }

    /** ตรวจวันที่เดินทาง (ใช้กฎเดียวกันทั้ง Model และหน้าต่าง TripDialog) */
    public static void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("กรุณาระบุวันที่เดินทาง");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("วันกลับต้องไม่ก่อนวันเดินทาง");
        }
        if (countDays(start, end) > MAX_DAYS) {
            throw new IllegalArgumentException("ทริปยาวได้ไม่เกิน " + MAX_DAYS + " วัน");
        }
    }

    /** จำนวนวันระหว่าง start ถึง end (นับวันแรกและวันสุดท้ายด้วย) */
    public static int countDays(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // # 6. จำนวนวัน / จำนวนคืน
    /** จำนวนวันของทริป (นับวันแรกและวันสุดท้ายด้วย) */
    public int getDayCount() {
        return countDays(startDate, endDate);
    }

    /** จำนวนคืน = จำนวนวัน - 1 */
    public int getNightCount() {
        return getDayCount() - 1;
    }

    // # 7. clear — ล้างสมาชิกและค่าใช้จ่าย เพื่อเริ่มทริปใหม่
    /** ล้างข้อมูลทั้งหมด เพื่อเริ่มทริปใหม่ */
    public void clear() {
        members.clear();
        expenses.clear();
    }

    /** มีข้อมูลอยู่แล้วหรือไม่ (ใช้ถามยืนยันก่อนล้างทริป) */
    public boolean hasData() {
        return !members.isEmpty() || !expenses.isEmpty();
    }

    // ===================== สมาชิก =====================

    // # 8. เพิ่มสมาชิก — ห้ามชื่อซ้ำ และไม่เกิน 20 คน
    public Member addMember(String memberName) {
        Member member = new Member(memberName);
        if (members.size() >= MAX_MEMBERS) {
            throw new IllegalArgumentException("เพิ่มสมาชิกได้สูงสุด " + MAX_MEMBERS + " คน");
        }
        if (members.contains(member)) {
            throw new IllegalArgumentException("มีชื่อ \"" + member.getName() + "\" อยู่ในทริปแล้ว");
        }
        members.add(member);
        return member;
    }

    // # 9. ลบสมาชิก — ห้ามลบคนที่ยังมีรายการค่าใช้จ่ายเกี่ยวข้อง
    /** ลบสมาชิกได้เฉพาะคนที่ยังไม่มีรายการค่าใช้จ่ายเกี่ยวข้อง */
    public void removeMember(Member member) {
        for (Expense e : expenses) {
            if (e.involves(member)) {
                throw new IllegalStateException("ไม่สามารถลบ \"" + member.getName() + "\" ได้\n"
                        + "เพราะมีรายการค่าใช้จ่ายที่เกี่ยวข้อง กรุณาลบรายการนั้นก่อน");
            }
        }
        members.remove(member);
    }

    // # 10. อ่านรายชื่อสมาชิก (unmodifiableList = ภายนอกแก้ list ตรง ๆ ไม่ได้)
    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public int getMemberCount() {
        return members.size();
    }

    // ===================== ค่าใช้จ่าย =====================

    // # 11. เพิ่มค่าใช้จ่าย — ผู้จ่ายและผู้ร่วมหารต้องอยู่ในทริปนี้
    public void addExpense(Expense expense) {
        Objects.requireNonNull(expense, "expense");
        if (!members.contains(expense.getPayer())) {
            throw new IllegalArgumentException("ผู้จ่าย \"" + expense.getPayer() + "\" ไม่ได้อยู่ในทริปนี้");
        }
        for (Member p : expense.getParticipants()) {
            if (!members.contains(p)) {
                throw new IllegalArgumentException("\"" + p + "\" ไม่ได้อยู่ในทริปนี้");
            }
        }
        expenses.add(expense);
    }

    // # 12. ลบ / อ่านรายการค่าใช้จ่าย
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public int getExpenseCount() {
        return expenses.size();
    }

    // ===================== การคำนวณ =====================

    // # 13. ยอดรวมทั้งทริป — เรียก getTotal() ของแต่ละรายการ (Polymorphism)
    /** ยอดใช้จ่ายรวมทั้งทริป */
    public double getTotalSpending() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getTotal(); // Polymorphism: เรียกเมธอดเดียว แต่ละประเภทคิดคนละสูตร
        }
        return total;
    }

    // # 14. เฉลี่ยต่อคน = ยอดรวม ÷ จำนวนสมาชิก
    public double getAveragePerPerson() {
        return members.isEmpty() ? 0 : getTotalSpending() / members.size();
    }

    // # 15. เงินที่สมาชิกคนนี้ควักจ่ายไปแล้ว
    /** เงินที่สมาชิกคนนี้ควักจ่ายไปแล้ว */
    public double getTotalPaidBy(Member member) {
        double total = 0;
        for (Expense e : expenses) {
            if (e.getPayer().equals(member)) {
                total += e.getTotal();
            }
        }
        return total;
    }

    // # 16. ส่วนที่สมาชิกคนนี้ต้องรับผิดชอบจริง (เรียก getShareFor ของแต่ละรายการ)
    /** ส่วนที่สมาชิกคนนี้ต้องรับผิดชอบจริง */
    public double getTotalShareOf(Member member) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getShareFor(member);
        }
        return total;
    }

    // # 17. ยอดสุทธิ = จ่ายไป − ส่วนของตัวเอง (บวก = ได้เงินคืน, ลบ = ต้องจ่ายเพิ่ม)
    /** ยอดสุทธิ: บวก = ต้องได้เงินคืน, ลบ = ต้องจ่ายเพิ่ม */
    public double getBalance(Member member) {
        return getTotalPaidBy(member) - getTotalShareOf(member);
    }

    // # 18. ยอดแยกตามหมวด (ใช้วาดกราฟโดนัท)
    /** ยอดรวมแยกตามหมวดหมู่ (ใช้วาดกราฟ) */
    public Map<ExpenseCategory, Double> getTotalByCategory() {
        Map<ExpenseCategory, Double> map = new EnumMap<>(ExpenseCategory.class);
        for (ExpenseCategory c : ExpenseCategory.values()) {
            map.put(c, 0.0);
        }
        for (Expense e : expenses) {
            map.merge(e.getCategory(), e.getTotal(), Double::sum);
        }
        return map;
    }

    /** หมวดที่ใช้เงินมากที่สุด (null ถ้ายังไม่มีค่าใช้จ่าย) */
    public ExpenseCategory getTopCategory() {
        ExpenseCategory top = null;
        double max = 0;
        for (Map.Entry<ExpenseCategory, Double> e : getTotalByCategory().entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                top = e.getKey();
            }
        }
        return top;
    }

    // # 19. ★ หัวใจของระบบ: คำนวณว่าใครต้องโอนให้ใคร ให้โอนน้อยครั้งที่สุด (Greedy)
    /**
     * คำนวณว่าใครต้องโอนให้ใคร โดยให้จำนวนครั้งที่โอนน้อยที่สุดเท่าที่ทำได้ (Greedy)
     * 1) แยกคนที่ต้องได้เงินคืน (เจ้าหนี้) กับคนที่ต้องจ่ายเพิ่ม (ลูกหนี้)
     * 2) เรียงจากยอดมากไปน้อย แล้วจับคู่ทีละคู่จนยอดเป็นศูนย์
     * คำนวณเป็นหน่วย "สตางค์" (long) เพื่อกันปัญหาทศนิยมคลาดเคลื่อน
     */
    public List<Settlement> calculateSettlements() {
        List<Debt> creditors = new ArrayList<>();
        List<Debt> debtors = new ArrayList<>();
        for (Member m : members) {
            long satang = Math.round(getBalance(m) * 100);
            if (satang > 0) {
                creditors.add(new Debt(m, satang));
            } else if (satang < 0) {
                debtors.add(new Debt(m, -satang));
            }
        }
        // เรียงจากยอดมากไปน้อย (sort แบบ stable: ยอดเท่ากันคงลำดับสมาชิกเดิม)
        Comparator<Debt> largestFirst = Comparator.comparingLong((Debt d) -> d.satang).reversed();
        creditors.sort(largestFirst);
        debtors.sort(largestFirst);

        List<Settlement> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            Debt creditor = creditors.get(i);
            Debt debtor = debtors.get(j);
            long pay = Math.min(creditor.satang, debtor.satang);
            result.add(new Settlement(debtor.member, creditor.member, pay / 100.0));
            creditor.satang -= pay;
            debtor.satang -= pay;
            if (creditor.satang == 0) i++;
            if (debtor.satang == 0) j++;
        }
        return result;
    }

    // # 20. ตัวช่วย: ยอดค้างของสมาชิก 1 คน (หน่วยสตางค์) ใช้ภายในการคำนวณเท่านั้น
    /** คู่ "สมาชิก + ยอดคงเหลือ" แทนการเก็บ List สองชุดคู่กัน */
    private static final class Debt {
        private final Member member;
        private long satang;

        Debt(Member member, long satang) {
            this.member = member;
            this.satang = satang;
        }
    }

    // # 21. สร้างข้อความสรุป สำหรับปุ่ม "คัดลอกสรุป" ไปวางในแชท
    /** ข้อความสรุปสำหรับคัดลอกไปวางในแชท */
    public String buildSummaryText() {
        StringBuilder sb = new StringBuilder();
        sb.append("สรุปค่าใช้จ่ายทริป: ").append(name).append('\n');
        sb.append("ยอดรวม ").append(Money.format(getTotalSpending())).append(" บาท")
          .append(" | สมาชิก ").append(members.size()).append(" คน")
          .append(" | ").append(expenses.size()).append(" รายการ\n\n");

        sb.append("[ ยอดของแต่ละคน ]\n");
        for (Member m : members) {
            sb.append("- ").append(m.getName())
              .append(": จ่ายไป ").append(Money.format(getTotalPaidBy(m)))
              .append(" / ส่วนของตัวเอง ").append(Money.format(getTotalShareOf(m)))
              .append('\n');
        }

        sb.append("\n[ ใครต้องโอนให้ใคร ]\n");
        List<Settlement> settlements = calculateSettlements();
        if (settlements.isEmpty()) {
            sb.append("- ไม่ต้องโอน ทุกคนจ่ายพอดีแล้ว\n");
        } else {
            for (Settlement s : settlements) {
                sb.append("- ").append(s).append('\n');
            }
        }
        sb.append("\nสร้างด้วย Trip Split");
        return sb.toString();
    }
}
