// ############################################################################
// # ไฟล์     : SampleData.java
// # ส่วน     : ข้อมูลตัวอย่าง
// # หน้าที่   : ใส่ทริปตัวอย่าง "เชียงใหม่ ไปด้วยกัน" 4 คน 8 รายการ สำหรับทดลอง/นำเสนอ
// # จุดสำคัญ : สร้างคลาสลูกของ Expense ครบทั้ง 4 แบบ (Polymorphism)
// ############################################################################
package tripsplit;

import tripsplit.model.ActivityExpense;
import tripsplit.model.FoodExpense;
import tripsplit.model.HotelExpense;
import tripsplit.model.Member;
import tripsplit.model.TransportExpense;
import tripsplit.model.Trip;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/** ข้อมูลทริปตัวอย่าง สำหรับทดลองใช้งาน/นำเสนอ */
public final class SampleData {

    private SampleData() {
    }

    // # 1. load — ล้างทริปเดิม ตั้งชื่อและวันที่ทริปตัวอย่าง
    public static void load(Trip trip) {
        trip.clear();
        trip.setName("เชียงใหม่ ไปด้วยกัน");
        trip.setDates(LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 18));

        // # 2. เพิ่มสมาชิก 4 คน
        Member ton = trip.addMember("ต้น");
        Member mint = trip.addMember("มิ้นท์");
        Member beer = trip.addMember("เบียร์");
        Member pla = trip.addMember("ปลา");
        List<Member> all = List.of(ton, mint, beer, pla);

        // # 3. เพิ่มค่าใช้จ่าย 8 รายการ (ใช้คลาสลูกครบ 4 แบบ)
        trip.addExpense(new TransportExpense("ค่าน้ำมันรถไป-กลับ", ton, all, 2400));
        trip.addExpense(new HotelExpense("โฮมสเตย์ย่านนิมมาน", mint, all, 1500, 2, 2));
        trip.addExpense(new FoodExpense("ข้าวซอยมื้อเที่ยง", beer, all, 620, false));
        trip.addExpense(new FoodExpense("หมูกระทะคืนแรก", pla, all, 1280, true));
        trip.addExpense(new ActivityExpense("ตั๋วเข้าสวนดอกไม้", ton, prices(ton, 150, mint, 150, beer, 0, pla, 150)));
        trip.addExpense(new TransportExpense("เหมารถแดงขึ้นดอยสุเทพ", beer, all, 600));
        trip.addExpense(new FoodExpense("คาเฟ่วิวดอย", mint, List.of(mint, pla), 340, false));
        trip.addExpense(new ActivityExpense("ล่องแพแม่วาง", pla, prices(ton, 450, mint, 450, beer, 450, pla, 450)));
    }

    /** ตัวช่วยสร้างราคารายคน: prices(คน1, ราคา1, คน2, ราคา2, ...) */
    private static Map<Member, Double> prices(Object... pairs) {
        Map<Member, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Member) pairs[i], ((Number) pairs[i + 1]).doubleValue());
        }
        return map;
    }
}
