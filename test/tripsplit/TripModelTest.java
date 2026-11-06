// ############################################################################
// # ไฟล์     : TripModelTest.java
// # ส่วน     : TEST – ทดสอบ Model
// # หน้าที่   : Test Case 22 กรณี ตรวจสูตรคำนวณ การโอนเงิน และการตรวจข้อมูล
// # จุดสำคัญ : รันได้โดยไม่ต้องติดตั้ง JUnit
// ############################################################################
package tripsplit;

import tripsplit.model.ActivityExpense;
import tripsplit.model.Expense;
import tripsplit.model.FoodExpense;
import tripsplit.model.HotelExpense;
import tripsplit.model.Member;
import tripsplit.model.Settlement;
import tripsplit.model.TransportExpense;
import tripsplit.model.Trip;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test Case ของ Model (รันได้โดยไม่ต้องติดตั้ง JUnit)
 * รัน:  java -cp out tripsplit.TripModelTest
 */
public class TripModelTest {

    private static int passed = 0;
    private static int failed = 0;

    // # 1. main — รันทุก Test Case แล้วสรุปผล
    public static void main(String[] args) {
        System.out.println("=== Trip Split : Model Test ===\n");

        // # 2. เตรียมข้อมูล: ทริปทดสอบ 3 คน (A, B, C)
        Trip trip = new Trip("ทดสอบ");
        Member a = trip.addMember("A");
        Member b = trip.addMember("B");
        Member c = trip.addMember("C");
        List<Member> all = List.of(a, b, c);

        // # 3. TC01–TC06: สูตรของแต่ละประเภท (Polymorphism) + กิจกรรมราคารายคน
        // ---------- Polymorphism: แต่ละประเภทคิดยอดคนละสูตร ----------
        Expense food = new FoodExpense("อาหาร", a, all, 1000, true);
        check("TC01 อาหาร + Service Charge 10% = 1,100", food.getTotal(), 1100);

        Expense hotel = new HotelExpense("ที่พัก", b, all, 1200, 2, 2);
        check("TC02 ที่พัก 2 ห้อง × 2 คืน × 1,200 = 4,800", hotel.getTotal(), 4800);

        Map<Member, Double> ticketPrices = new LinkedHashMap<>();
        ticketPrices.put(a, 200.0);
        ticketPrices.put(b, 0.0);   // ไม่ได้ไป กรอก 0
        ticketPrices.put(c, 100.0);
        Expense ticket = new ActivityExpense("ตั๋ว", c, ticketPrices);
        check("TC03 กิจกรรมราคารายคน 200 + 0 + 100 = 300", ticket.getTotal(), 300);
        check("TC04 คนที่กรอก 0 ส่วนแบ่ง = 0", ticket.getShareFor(b), 0);
        check("TC05 แต่ละคนรับผิดชอบเท่าราคาของตัวเอง (A = 200)", ticket.getShareFor(a), 200);

        Expense transport = new TransportExpense("น้ำมัน", a, all, 900);
        check("TC06 ค่าเดินทาง 900 หาร 3 = 300/คน", transport.getShareFor(c), 300);

        trip.addExpense(food);
        trip.addExpense(hotel);
        trip.addExpense(ticket);
        trip.addExpense(transport);

        // # 4. TC07–TC08: ยอดรวม และผลรวมยอดสุทธิต้องเป็น 0
        // ---------- ยอดรวม และยอดสุทธิ ----------
        check("TC07 ยอดรวมทั้งทริป = 7,100", trip.getTotalSpending(), 7100);
        double sum = 0;
        for (Member m : trip.getMembers()) sum += trip.getBalance(m);
        check("TC08 ผลรวมยอดสุทธิทุกคน = 0", sum, 0);

        // # 5. TC09–TC10: โอนตามผลแล้วทุกคนต้องเคลียร์ และโอนไม่เกิน n−1 ครั้ง
        // ---------- การเคลียร์เงิน ----------
        List<Settlement> settlements = trip.calculateSettlements();
        Map<Member, Double> after = new HashMap<>();
        for (Member m : trip.getMembers()) after.put(m, trip.getBalance(m));
        for (Settlement s : settlements) {
            after.merge(s.getFrom(), s.getAmount(), Double::sum);
            after.merge(s.getTo(), -s.getAmount(), Double::sum);
        }
        boolean cleared = after.values().stream().allMatch(v -> Math.abs(v) < 0.02);
        checkTrue("TC09 โอนตามผลลัพธ์แล้ว ทุกคนยอดเป็น 0", cleared);
        checkTrue("TC10 จำนวนครั้งที่โอน ≤ จำนวนสมาชิก - 1", settlements.size() <= trip.getMemberCount() - 1);

        // # 6. TC11–TC16: ข้อมูลไม่ถูกต้องต้องแจ้ง Error
        // ---------- การตรวจสอบข้อมูล ----------
        expectError("TC11 เพิ่มชื่อซ้ำไม่ได้", () -> trip.addMember("a"));
        expectError("TC12 ชื่อว่างไม่ได้", () -> trip.addMember("   "));
        expectError("TC13 จำนวนเงินต้องมากกว่า 0", () -> new FoodExpense("x", a, all, 0, false));
        expectError("TC14 ต้องมีผู้ร่วมหารอย่างน้อย 1 คน", () -> new TransportExpense("x", a, List.of(), 100));
        expectError("TC15 ลบสมาชิกที่มีรายการเกี่ยวข้องไม่ได้", () -> trip.removeMember(a));

        Member d = trip.addMember("D");
        trip.removeMember(d);
        check("TC16 ลบสมาชิกที่ไม่มีรายการได้", trip.getMemberCount(), 3);

        // # 7. TC17–TC19: หาร 100 บาท 3 คน + กิจกรรมกรอก 0 ทุกคน / ติดลบ
        // ---------- หาร 100 บาท 3 คน (ทศนิยมไม่ลงตัว) ----------
        Trip t2 = new Trip("ทศนิยม");
        Member x = t2.addMember("X");
        Member y = t2.addMember("Y");
        Member z = t2.addMember("Z");
        t2.addExpense(new TransportExpense("แท็กซี่", x, List.of(x, y, z), 100));
        double transferred = t2.calculateSettlements().stream().mapToDouble(Settlement::getAmount).sum();
        check("TC17 หาร 100/3: เพื่อนโอนคืนรวม 66.66", transferred, 66.66);

        // # 8. TC20–TC22: วันเดินทาง
        Map<Member, Double> allZero = new LinkedHashMap<>();
        allZero.put(x, 0.0);
        allZero.put(y, 0.0);
        expectError("TC18 กิจกรรมกรอก 0 ทุกคนไม่ได้", () -> new ActivityExpense("x", x, allZero));
        Map<Member, Double> negative = new LinkedHashMap<>();
        negative.put(x, -50.0);
        expectError("TC19 ราคาติดลบไม่ได้", () -> new ActivityExpense("x", x, negative));

        // ---------- วันที่เดินทาง ----------
        Trip t3 = new Trip("วันที่");
        t3.setDates(java.time.LocalDate.of(2026, 10, 16), java.time.LocalDate.of(2026, 10, 18));
        check("TC20 16-18 ต.ค. = 3 วัน", t3.getDayCount(), 3);
        check("TC21 3 วัน = 2 คืน", t3.getNightCount(), 2);
        expectError("TC22 วันกลับก่อนวันไปไม่ได้", () -> t3.setDates(
                java.time.LocalDate.of(2026, 10, 18), java.time.LocalDate.of(2026, 10, 16)));

        System.out.println("\nผลลัพธ์: ผ่าน " + passed + " / " + (passed + failed) + " กรณี");
        if (failed > 0) System.exit(1);
    }

    // # 9. ตัวช่วยตรวจผล: เทียบตัวเลข / เงื่อนไข / ต้องเกิด Error
    private static void check(String name, double actual, double expected) {
        checkTrue(name + "  (ได้ " + actual + ")", Math.abs(actual - expected) < 0.01);
    }

    private static void checkTrue(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "[PASS] " : "[FAIL] ") + name);
    }

    private static void expectError(String name, Runnable action) {
        try {
            action.run();
            checkTrue(name, false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            checkTrue(name + "  -> \"" + ex.getMessage().replace('\n', ' ') + "\"", true);
        }
    }
}
