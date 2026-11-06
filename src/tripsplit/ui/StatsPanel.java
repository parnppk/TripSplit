// ############################################################################
// # ไฟล์     : StatsPanel.java
// # ส่วน     : UI – แถวการ์ดสรุป
// # หน้าที่   : การ์ดตัวเลข 4 ใบด้านบน: ยอดรวม · เฉลี่ยต่อคน · จำนวนรายการ · จำนวนสมาชิก
// # จุดสำคัญ : implements Refreshable, ตัวเลขทั้งหมดมาจาก Trip (ไม่มีการคำนวณในคลาสนี้)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.ExpenseCategory;
import tripsplit.model.Money;
import tripsplit.model.Trip;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.StatCard;

import javax.swing.JPanel;
import java.awt.GridLayout;

/** แถวการ์ดตัวเลขสรุป 4 ใบ */
public class StatsPanel extends JPanel implements Refreshable {

    // # 1. อ้างอิง Model + การ์ด 4 ใบ
    private final Trip trip;
    private final StatCard totalCard = new StatCard("ยอดใช้จ่ายรวม", "บาท",
            Icons.StatIcon.Type.MONEY, Theme.PRIMARY);
    private final StatCard averageCard = new StatCard("เฉลี่ยต่อคน", "บาท",
            Icons.StatIcon.Type.AVERAGE, Theme.ACCENT);
    private final StatCard countCard = new StatCard("รายการทั้งหมด", "รายการ",
            Icons.StatIcon.Type.LIST, Theme.INFO);
    private final StatCard memberCard = new StatCard("สมาชิก", "คน",
            Icons.StatIcon.Type.PEOPLE, Theme.VIOLET);

    // # 2. Constructor — วางการ์ด 4 ใบเรียงแนวนอน กว้างเท่ากัน
    public StatsPanel(Trip trip) {
        super(new GridLayout(1, 4, 14, 0));
        this.trip = trip;
        setOpaque(false);
        add(totalCard);
        add(averageCard);
        add(countCard);
        add(memberCard);
    }

    // # 3. ตัวเลขหลักของการ์ดแต่ละใบ + บรรทัดสรุปใต้ตัวเลข
    @Override
    public void refresh() {
        int days = trip.getDayCount();
        boolean noExpense = trip.getExpenseCount() == 0;

        totalCard.setValue(Money.format(trip.getTotalSpending()));
        totalCard.setNote(noExpense ? "ยังไม่มีค่าใช้จ่าย"
                : "เฉลี่ยวันละ " + Money.format(trip.getTotalSpending() / days) + " บาท");

        averageCard.setValue(Money.format(trip.getAveragePerPerson()));
        averageCard.setNote(trip.getMemberCount() == 0 ? "เพิ่มสมาชิกเพื่อหารเงิน"
                : "หารกัน " + trip.getMemberCount() + " คน");

        countCard.setValue(String.valueOf(trip.getExpenseCount()));
        ExpenseCategory top = trip.getTopCategory();
        countCard.setNote(top == null ? "เพิ่มได้จากฟอร์มด้านซ้าย"
                : "ใช้มากสุด: " + top.getDisplayName() + " " + topCategoryPercent(top) + "%");

        memberCard.setValue(String.valueOf(trip.getMemberCount()));
        memberCard.setNote(ThaiDates.duration(days));
    }

    private long topCategoryPercent(ExpenseCategory top) {
        return Math.round(trip.getTotalByCategory().get(top) / trip.getTotalSpending() * 100);
    }
}
