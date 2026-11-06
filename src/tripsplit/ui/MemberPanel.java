// ############################################################################
// # ไฟล์     : MemberPanel.java
// # ส่วน     : UI – การ์ดสมาชิก
// # หน้าที่   : เพิ่ม/ลบสมาชิก และแสดงรายชื่อเป็นป้ายชื่อ
// # จุดสำคัญ : Inheritance (extends TripCard), เรียก trip.addMember / removeMember (Logic อยู่ใน Model)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Member;
import tripsplit.model.Trip;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.PillLabel;
import tripsplit.ui.components.RoundedPanel;
import tripsplit.ui.components.StyledTextField;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;

/** การ์ดจัดการสมาชิกในทริป (เพิ่ม / ลบ / แสดงรายชื่อ) */
public class MemberPanel extends TripCard {

    // # 1. callback แจ้งหน้าหลัก + ช่องกรอกชื่อ + พื้นที่รายชื่อ (trip ได้มาจาก TripCard)
    private final Runnable onChange;
    private final StyledTextField nameField = new StyledTextField("พิมพ์ชื่อเพื่อน แล้วกด Enter");
    private final JPanel listPanel = UI.clear(new GridLayout(0, 2, 8, 8));
    private final PillLabel countBadge = new PillLabel("0 คน", Theme.PRIMARY_SOFT, Theme.PRIMARY_DARK);

    // # 2. Constructor — หัวการ์ด, ช่องกรอก, ปุ่มเพิ่ม (กด Enter ก็ได้)
    public MemberPanel(Trip trip, Runnable onChange) {
        super(trip, 18, 21);
        this.onChange = onChange;
        setLayout(new BorderLayout(0, 14));

        // ---------- หัวการ์ด ----------
        JPanel header = UI.clear(new BorderLayout());
        header.add(titleBlock("สมาชิกในทริป", "ใส่ชื่อทุกคนที่ร่วมหารค่าใช้จ่าย"), BorderLayout.CENTER);
        JPanel badgeWrap = UI.clear(new BorderLayout());
        badgeWrap.add(countBadge, BorderLayout.NORTH);
        header.add(badgeWrap, BorderLayout.EAST);

        // ---------- ช่องกรอกชื่อ ----------
        ModernButton addButton = new ModernButton("เพิ่ม", ModernButton.Style.PRIMARY).withPadding(8, 18);
        JPanel inputRow = UI.clear(new BorderLayout(8, 0));
        inputRow.add(nameField, BorderLayout.CENTER);
        inputRow.add(addButton, BorderLayout.EAST);

        JPanel top = UI.clear(new BorderLayout(0, 14));
        top.add(header, BorderLayout.NORTH);
        top.add(inputRow, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);

        nameField.addActionListener(e -> addMember());
        addButton.addActionListener(e -> addMember());
        refresh();
    }

    // # 3. ไปที่ช่องกรอกชื่อ (เรียกจากปุ่ม + บนแบนเนอร์)
    /** เลื่อนมาที่ช่องกรอกชื่อและพร้อมพิมพ์ */
    public void focusNameField() {
        nameField.scrollRectToVisible(nameField.getBounds());
        scrollRectToVisible(new Rectangle(0, 0, getWidth(), getHeight()));
        nameField.requestFocusInWindow();
    }

    // # 4. เพิ่มสมาชิก → trip.addMember() (ชื่อว่าง/ซ้ำ จะแจ้งเตือน)
    private void addMember() {
        try {
            trip.addMember(nameField.getText());
            nameField.setText("");
            nameField.requestFocusInWindow();
            onChange.run();
        } catch (IllegalArgumentException ex) {
            UI.warn(this, ex.getMessage());
            nameField.requestFocusInWindow();
        }
    }

    // # 5. ลบสมาชิก (ถามยืนยันก่อน)
    private void removeMember(Member member) {
        if (!UI.confirm(this, "ต้องการลบ \"" + member.getName() + "\" ออกจากทริปใช่หรือไม่?")) {
            return;
        }
        try {
            trip.removeMember(member);
            onChange.run();
        } catch (IllegalStateException ex) {
            UI.warn(this, ex.getMessage());
        }
    }

    // # 6. วาดรายชื่อใหม่ตามข้อมูลใน Trip
    /** วาดรายชื่อสมาชิกใหม่ตามข้อมูลใน Trip */
    @Override
    public void refresh() {
        listPanel.removeAll();
        countBadge.setText(trip.getMemberCount() + " คน");

        if (trip.getMemberCount() == 0) {
            listPanel.setLayout(new BorderLayout());
            RoundedPanel empty = new RoundedPanel(12, Theme.SURFACE, Theme.BORDER, false);
            empty.setLayout(new BorderLayout());
            empty.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
            JLabel text = UI.label("ยังไม่มีสมาชิก  เพิ่มอย่างน้อย 2 คนเพื่อเริ่มหารเงิน",
                    Font.PLAIN, 13, Theme.MUTED);
            text.setHorizontalAlignment(JLabel.CENTER);
            empty.add(text);
            listPanel.add(empty, BorderLayout.CENTER);
        } else {
            listPanel.setLayout(new GridLayout(0, 2, 8, 8));
            for (Member m : trip.getMembers()) {
                listPanel.add(createChip(m));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // # 7. ป้ายชื่อสมาชิก 1 คน: avatar + ชื่อ + ปุ่ม ×
    private JPanel createChip(Member member) {
        RoundedPanel chip = new RoundedPanel(12, Theme.SURFACE, Theme.BORDER, false);
        chip.setLayout(new BorderLayout(8, 0));
        chip.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 4));

        JLabel name = UI.memberLabel(member, 28);

        ModernButton remove = new ModernButton("", ModernButton.Style.ICON)
                .withPadding(7, 7).withRadius(8).withIcon(new Icons.CloseIcon(12), 0);
        remove.setToolTipText("ลบ " + member.getName());
        remove.addActionListener(e -> removeMember(member));

        chip.add(name, BorderLayout.CENTER);
        chip.add(remove, BorderLayout.EAST);
        return chip;
    }
}
