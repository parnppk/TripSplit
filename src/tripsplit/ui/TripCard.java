// ############################################################################
// # ไฟล์     : TripCard.java
// # ส่วน     : UI – คลาสแม่ของการ์ด (abstract)
// # หน้าที่   : ส่วนที่การ์ดทุกใบใช้ร่วมกัน: อ้างอิง Trip, พื้นขาวมุมโค้ง, ระยะขอบ, หัวการ์ด
// # จุดสำคัญ : Abstraction, Inheritance (การ์ดสมาชิก/ฟอร์ม/ตาราง/สรุป extends คลาสนี้)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Trip;
import tripsplit.ui.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

/**
 * การ์ดบนหน้าหลักที่แสดงข้อมูลจาก Trip (Abstract Class)
 * <ul>
 *   <li>Abstraction  – สร้าง Object ตรง ๆ ไม่ได้ คลาสลูกต้องเขียน refresh() เอง</li>
 *   <li>Inheritance  – คลาสลูกได้ field trip, หน้าตาการ์ด และตัวช่วยสร้างหัวการ์ดไปใช้</li>
 * </ul>
 */
public abstract class TripCard extends RoundedPanel implements Refreshable {

    // # 1. ข้อมูลที่การ์ดทุกใบใช้: ทริปปัจจุบัน (protected = คลาสลูกอ่านได้)
    protected final Trip trip;

    // # 2. Constructor — พื้นขาว มุมโค้ง 18 และระยะขอบซ้าย-ขวา 18 เท่ากันทุกการ์ด
    protected TripCard(Trip trip, int topPadding, int bottomPadding) {
        super(18, Color.WHITE);
        this.trip = trip;
        setBorder(BorderFactory.createEmptyBorder(topPadding, 18, bottomPadding, 18));
    }

    // # 3. Abstract method — แต่ละการ์ดวาดข้อมูลของตัวเองต่างกัน (Polymorphism)
    @Override
    public abstract void refresh();

    // # 4. ตัวช่วยสร้างหัวการ์ด: หัวข้อ + คำอธิบาย ซ้อนกัน 2 บรรทัด
    protected static JPanel titleBlock(String title, String subtitle) {
        return titleBlock(title, UI.subtitle(subtitle));
    }

    /** ใช้แบบนี้เมื่อคำอธิบายต้องเปลี่ยนข้อความได้ภายหลัง */
    protected static JPanel titleBlock(String title, JLabel subtitle) {
        JPanel titles = UI.clear(new GridLayout(2, 1, 0, 2));
        titles.add(UI.title(title));
        titles.add(subtitle);
        return titles;
    }
}
