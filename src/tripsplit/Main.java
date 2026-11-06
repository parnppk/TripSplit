// ############################################################################
// # ไฟล์     : Main.java
// # ส่วน     : จุดเริ่มต้นโปรแกรม
// # หน้าที่   : ตั้งค่าความคมของตัวอักษร โหลดธีม แล้วเปิดหน้าต่างหลัก TripSplitForm
// # จุดสำคัญ : รัน --demo เพื่อเปิดพร้อมข้อมูลตัวอย่าง
// ############################################################################
package tripsplit;

import tripsplit.ui.Theme;
import tripsplit.ui.TripSplitForm;

import javax.swing.SwingUtilities;

/**
 * จุดเริ่มต้นโปรแกรม Trip Split
 * รันแบบมีข้อมูลตัวอย่างได้ด้วย:  java -cp out tripsplit.Main --demo
 */
public class Main {

    // # 1. main — เมธอดแรกที่ Java เรียกเมื่อรันโปรแกรม
    public static void main(String[] args) {
        // ทำให้ตัวอักษรคมชัด
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // # 2. ตรวจ argument --demo (เปิดมาพร้อมข้อมูลตัวอย่าง)
        boolean demo = args.length > 0 && args[0].equalsIgnoreCase("--demo");

        // # 3. สร้างหน้าจอบน Event Dispatch Thread (EDT) ตามกฎของ Swing
        SwingUtilities.invokeLater(() -> {
            Theme.applyGlobalDefaults();
            TripSplitForm form = new TripSplitForm();
            if (demo) {
                form.loadSampleData(false);
            }
            form.setVisible(true);
        });
    }
}
