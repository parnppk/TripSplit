// ############################################################################
// # ไฟล์     : TripDialog.java
// # ส่วน     : UI – หน้าต่างย่อย
// # หน้าที่   : หน้าต่างสร้างทริปใหม่ / แก้ไขชื่อและวันที่ของทริป
// # จุดสำคัญ : ตรวจข้อมูลด้วยกฎเดียวกับ Model (Trip.setDates)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Trip;
import tripsplit.ui.components.DatePickerField;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.RoundedPanel;
import tripsplit.ui.components.StyledTextField;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

/** หน้าต่างสร้างทริปใหม่ / แก้ไขชื่อและวันที่ของทริป */
public class TripDialog extends JDialog {

    // # 1. ผลลัพธ์ที่ผู้ใช้กรอก (ชื่อ, วันไป, วันกลับ)
    /** ผลลัพธ์ที่ผู้ใช้กรอก (null = กดยกเลิก) */
    public static final class Result {
        public final String name;
        public final LocalDate start;
        public final LocalDate end;

        Result(String name, LocalDate start, LocalDate end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    // # 2. ช่องกรอกในหน้าต่าง: ชื่อทริป, วันเดินทาง, วันกลับ
    private final StyledTextField nameField = new StyledTextField("เช่น เชียงใหม่ ไปด้วยกัน");
    private final DatePickerField startField;
    private final DatePickerField endField;
    private final JLabel durationLabel = UI.label(" ", Font.BOLD, 13, Theme.PRIMARY_DARK);
    private Result result;

    // # 3. Constructor — จัดวางหัวข้อ ช่องกรอก ปุ่ม และผูก Listener (Esc = ปิด, Enter = บันทึก)
    private TripDialog(Window owner, boolean create, String name, LocalDate start, LocalDate end) {
        super(owner, create ? "สร้างทริปใหม่" : "แก้ไขข้อมูลทริป", ModalityType.APPLICATION_MODAL);
        startField = new DatePickerField(start);
        endField = new DatePickerField(end);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JPanel head = UI.clear(new BorderLayout(14, 0));
        RoundedPanel icon = new RoundedPanel(new GridBagLayout(), 12, Theme.PRIMARY_SOFT, null, false);
        icon.setPreferredSize(new Dimension(44, 44));
        icon.add(new JLabel(new Icons.LineIcon(
                create ? Icons.LineIcon.Type.PLUS : Icons.LineIcon.Type.EDIT, 20, Theme.PRIMARY)));
        JPanel iconWrap = UI.clear(new BorderLayout());
        iconWrap.add(icon, BorderLayout.NORTH);
        head.add(iconWrap, BorderLayout.WEST);
        JPanel titles = UI.clear(new GridLayout(2, 1, 0, 2));
        titles.add(UI.label(create ? "สร้างทริปใหม่" : "แก้ไขข้อมูลทริป", Theme.HEADING, 19, Theme.TEXT));
        titles.add(UI.subtitle(create ? "ตั้งชื่อทริปและเลือกวันเดินทาง ข้อมูลเดิมจะถูกล้าง"
                : "ชื่อและวันที่จะแสดงบนแบนเนอร์ด้านบน"));
        head.add(titles, BorderLayout.CENTER);
        UI.addRow(content, head, 0);

        UI.addRow(content, UI.fieldLabel("ชื่อทริป"), 20);
        nameField.setText(name);
        UI.addRow(content, nameField, 6);

        JPanel dates = UI.clear(new GridLayout(2, 2, 12, 6));
        dates.add(UI.fieldLabel("วันเดินทาง"));
        dates.add(UI.fieldLabel("วันกลับ"));
        dates.add(startField);
        dates.add(endField);
        UI.addRow(content, dates, 16);
        UI.addRow(content, durationLabel, 10);

        ModernButton cancel = new ModernButton("ยกเลิก", ModernButton.Style.SECONDARY).withPadding(9, 20);
        ModernButton save = new ModernButton(create ? "สร้างทริป" : "บันทึก", ModernButton.Style.PRIMARY)
                .withPadding(9, 24);
        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> onSave());
        nameField.addActionListener(e -> onSave());
        JPanel buttons = UI.clear(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(cancel);
        buttons.add(save);
        UI.addRow(content, buttons, 22);

        // วันกลับต้องไม่ก่อนวันเดินทาง: ขยับให้อัตโนมัติ
        startField.addChangeListener(() -> {
            endField.setMinDate(startField.getValue());
            if (endField.getValue().isBefore(startField.getValue())) {
                endField.setValue(startField.getValue());
            }
            updateDuration();
        });
        endField.addChangeListener(this::updateDuration);
        endField.setMinDate(start);
        updateDuration();

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(save);

        setContentPane(content);
        pack();
        setSize(Math.max(getWidth(), 470), getHeight());
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // # 4. แสดงจำนวนวันอัตโนมัติเมื่อเปลี่ยนวันที่
    private void updateDuration() {
        int days = Trip.countDays(startField.getValue(), endField.getValue());
        durationLabel.setText("รวม " + ThaiDates.duration(days) + "   ·   "
                + ThaiDates.formatRange(startField.getValue(), endField.getValue()));
    }

    // # 5. กดบันทึก → ตรวจข้อมูลด้วยกฎของ Model ก่อนปิดหน้าต่าง
    private void onSave() {
        String name;
        try {
            // ตรวจด้วยกฎเดียวกับ Model (static method ของ Trip) ไม่ต้องสร้าง Trip ชั่วคราว
            name = Trip.validateName(nameField.getText());
            Trip.validateDates(startField.getValue(), endField.getValue());
        } catch (IllegalArgumentException ex) {
            UI.warn(this, ex.getMessage());
            nameField.requestFocusInWindow();
            return;
        }
        result = new Result(name, startField.getValue(), endField.getValue());
        dispose();
    }

    // # 6. เปิดหน้าต่างแบบแก้ไข / แบบสร้างทริปใหม่
    /** แสดงหน้าต่างแก้ไขทริป คืนค่า null ถ้ากดยกเลิก */
    public static Result showEdit(Window owner, Trip trip) {
        TripDialog d = new TripDialog(owner, false, trip.getName(), trip.getStartDate(), trip.getEndDate());
        d.setVisible(true);
        return d.result;
    }

    /** แสดงหน้าต่างสร้างทริปใหม่ คืนค่า null ถ้ากดยกเลิก */
    public static Result showCreate(Window owner) {
        LocalDate today = LocalDate.now();
        TripDialog d = new TripDialog(owner, true, "", today, today.plusDays(2));
        d.setVisible(true);
        return d.result;
    }
}
