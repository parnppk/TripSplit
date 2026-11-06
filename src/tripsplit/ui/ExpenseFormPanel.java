// ############################################################################
// # ไฟล์     : ExpenseFormPanel.java
// # ส่วน     : UI – ฟอร์มด้านซ้าย
// # หน้าที่   : ฟอร์มเพิ่มค่าใช้จ่าย: เลือกหมวด กรอกจำนวน เลือกผู้จ่าย/ผู้ร่วมหาร แล้วบันทึก
// # จุดสำคัญ : Inheritance (extends TripCard), เลือกสร้าง Object คลาสลูกตามหมวด แล้วส่งให้ Trip (UI → Model)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.ActivityExpense;
import tripsplit.model.Expense;
import tripsplit.model.ExpenseCategory;
import tripsplit.model.FoodExpense;
import tripsplit.model.HotelExpense;
import tripsplit.model.Member;
import tripsplit.model.Money;
import tripsplit.model.TransportExpense;
import tripsplit.model.Trip;
import tripsplit.ui.components.CategoryButton;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.RoundedPanel;
import tripsplit.ui.components.Stepper;
import tripsplit.ui.components.StyledTextField;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ฟอร์มเพิ่มค่าใช้จ่าย
 * ช่องกรอกจะเปลี่ยนตามหมวดหมู่ที่เลือก แล้วสร้าง Object ของคลาสลูกที่ตรงกับหมวดนั้น
 */
public class ExpenseFormPanel extends TripCard {

    // # 1. callback แจ้งหน้าหลักให้วาดใหม่ (trip ได้มาจาก TripCard)
    private final Runnable onChange;

    // # 2. หมวดหมู่ที่เลือก + ปุ่มหมวด 4 ปุ่ม
    private ExpenseCategory category = ExpenseCategory.FOOD;
    private final Map<ExpenseCategory, CategoryButton> categoryButtons = new EnumMap<>(ExpenseCategory.class);

    // # 3. ช่องกรอก: ชื่อรายการ + ช่องเฉพาะของแต่ละหมวด (แสดงเฉพาะหมวดที่เลือก) + ช่องราคารายคนของกิจกรรม
    private final StyledTextField titleField = new StyledTextField("เช่น หมูกระทะ, ค่าน้ำมัน, ตั๋วเข้าชม");

    private final JPanel detailPanel = UI.clear(new BorderLayout()); // แสดงเฉพาะช่องของหมวดที่เลือก
    private final Map<ExpenseCategory, JPanel> detailCards = new EnumMap<>(ExpenseCategory.class);
    private final StyledTextField foodAmount = new StyledTextField("0.00");
    private final JCheckBox serviceCharge = UI.checkBox("บวก Service Charge 10%");
    private final StyledTextField transportAmount = new StyledTextField("0.00");
    private final StyledTextField hotelPrice = new StyledTextField("0.00");
    private final Stepper hotelNights = new Stepper(1, 1, 30, "คืน");
    private final Stepper hotelRooms = new Stepper(1, 1, 20, "ห้อง");
    // ช่องราคารายคนของหมวดกิจกรรม (1 ช่องต่อสมาชิก 1 คน)
    private final JPanel activityRows = UI.clear(new GridBagLayout());
    private final Map<Member, StyledTextField> activityPrices = new LinkedHashMap<>();
    private final ModernButton samePriceButton = new ModernButton("ใช้ราคาเดียวกันทุกคน", ModernButton.Style.GHOST)
            .withPadding(3, 8).withRadius(8).withFont(Theme.font(Font.BOLD, 13));
    private final DocumentListener previewListener = new DocumentListener() {
        @Override public void insertUpdate(DocumentEvent e) { updatePreview(); }
        @Override public void removeUpdate(DocumentEvent e) { updatePreview(); }
        @Override public void changedUpdate(DocumentEvent e) { updatePreview(); }
    };

    // # 4. ผู้จ่าย (ComboBox) + ผู้ร่วมหาร (Checkbox)
    private final JComboBox<Member> payerCombo = new JComboBox<>();
    private final JPanel participantHeader = UI.clear(new BorderLayout());
    private final JPanel participantPanel = UI.clear(new GridLayout(0, 2, 6, 6));
    private final Map<Member, JCheckBox> participantChecks = new LinkedHashMap<>();
    private final ModernButton toggleAllButton = new ModernButton("เลือกทั้งหมด", ModernButton.Style.GHOST)
            .withPadding(3, 8).withRadius(8).withFont(Theme.font(Font.BOLD, 13));

    // # 5. กล่องแสดงยอดตัวอย่าง + ปุ่มบันทึก
    private final RoundedPanel previewBox = new RoundedPanel(12, Theme.PRIMARY_SOFT, null, false);
    private final JLabel previewTotal = UI.label("0.00 บาท", Theme.DISPLAY, 22, Theme.PRIMARY_DARK);
    private final JLabel previewDetail = UI.label(" ", Font.PLAIN, 13, Theme.TEXT_SOFT);
    private final ModernButton addButton = new ModernButton("+   บันทึกค่าใช้จ่าย", ModernButton.Style.PRIMARY)
            .withPadding(12, 16).withFont(Theme.font(Theme.SEMIBOLD, 15));

    // # 6. Constructor — จัดวางฟอร์มจากบนลงล่าง แล้วผูก Listener
    public ExpenseFormPanel(Trip trip, Runnable onChange) {
        super(trip, 18, 22);
        this.onChange = onChange;
        setLayout(new GridBagLayout());

        // ---------- หัวการ์ด ----------
        addRow(UI.title("เพิ่มค่าใช้จ่าย"), 0);
        addRow(UI.subtitle("เลือกหมวดหมู่ แล้วระบบจะคำนวณยอดให้อัตโนมัติ"), 2);

        // ---------- หมวดหมู่ ----------
        addRow(UI.fieldLabel("หมวดหมู่"), 16);
        JPanel categoryGrid = UI.clear(new GridLayout(2, 2, 8, 8));
        for (ExpenseCategory c : ExpenseCategory.values()) {
            CategoryButton b = new CategoryButton(c, this::selectCategory);
            categoryButtons.put(c, b);
            categoryGrid.add(b);
        }
        addRow(categoryGrid, 8);

        // ---------- ชื่อรายการ ----------
        addRow(UI.fieldLabel("ชื่อรายการ"), 16);
        addRow(titleField, 6);

        // ---------- รายละเอียดตามหมวด ----------
        detailCards.put(ExpenseCategory.FOOD, buildFoodCard());
        detailCards.put(ExpenseCategory.TRANSPORT, buildTransportCard());
        detailCards.put(ExpenseCategory.HOTEL, buildHotelCard());
        detailCards.put(ExpenseCategory.ACTIVITY, buildActivityCard());
        addRow(detailPanel, 14);

        // ---------- ผู้จ่าย ----------
        addRow(UI.fieldLabel("ใครเป็นคนจ่าย"), 14);
        UI.styleMemberCombo(payerCombo);
        addRow(payerCombo, 6);

        // ---------- ผู้ร่วมหาร ----------
        participantHeader.add(UI.fieldLabel("หารกับใครบ้าง"), BorderLayout.WEST);
        participantHeader.add(toggleAllButton, BorderLayout.EAST);
        addRow(participantHeader, 14);
        addRow(participantPanel, 4);

        // ---------- ตัวอย่างยอด ----------
        previewBox.setLayout(new GridLayout(3, 1, 0, 0));
        previewBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 12, 14));
        previewBox.add(UI.label("ยอดรวมรายการนี้", Font.PLAIN, 12, Theme.PRIMARY_DARK));
        previewBox.add(previewTotal);
        previewBox.add(previewDetail);
        addRow(previewBox, 16);

        // ---------- ปุ่มบันทึก ----------
        addRow(addButton, 12);

        // ---------- Listener ----------
        addButton.addActionListener(e -> onAdd());
        titleField.addActionListener(e -> onAdd());
        toggleAllButton.addActionListener(e -> toggleAll());
        samePriceButton.addActionListener(e -> fillSamePrice());
        for (StyledTextField f : new StyledTextField[]{foodAmount, transportAmount, hotelPrice}) {
            f.getDocument().addDocumentListener(previewListener);
            f.addActionListener(e -> onAdd());
        }
        serviceCharge.addItemListener(e -> updatePreview());
        hotelNights.addChangeListener(this::updatePreview);
        hotelRooms.addChangeListener(this::updatePreview);
        payerCombo.addActionListener(e -> updatePreview());

        selectCategory(ExpenseCategory.FOOD);
        refresh();
    }

    // ===================== สร้างส่วนรายละเอียดของแต่ละหมวด =====================

    // # 7. สร้างช่องกรอกของแต่ละหมวด: อาหาร / เดินทาง / ที่พัก / กิจกรรม (ราคารายคน)
    private JPanel buildFoodCard() {
        JPanel p = UI.clear(new GridBagLayout());
        add(p, UI.fieldLabel("ค่าอาหาร (บาท)"), 0);
        add(p, foodAmount, 6);
        add(p, serviceCharge, 8);
        UI.pushUp(p);
        return p;
    }

    private JPanel buildTransportCard() {
        JPanel p = UI.clear(new GridBagLayout());
        add(p, UI.fieldLabel("ค่าเดินทาง (บาท)"), 0);
        add(p, transportAmount, 6);
        add(p, UI.label("ค่าน้ำมัน ค่าทางด่วน ค่ารถ ฯลฯ  หารเท่ากันทุกคน", Font.PLAIN, 12, Theme.MUTED), 8);
        UI.pushUp(p);
        return p;
    }

    private JPanel buildHotelCard() {
        JPanel p = UI.clear(new GridBagLayout());
        add(p, UI.fieldLabel("ราคาต่อห้อง ต่อคืน (บาท)"), 0);
        add(p, hotelPrice, 6);
        JPanel steppers = UI.clear(new GridLayout(2, 2, 10, 6));
        steppers.add(UI.fieldLabel("จำนวนคืน"));
        steppers.add(UI.fieldLabel("จำนวนห้อง"));
        steppers.add(hotelNights);
        steppers.add(hotelRooms);
        add(p, steppers, 12);
        UI.pushUp(p);
        return p;
    }

    /** หมวดกิจกรรม: ช่องราคาแยกรายคน พร้อมชื่อกำกับ (ไม่ได้ไป/ไม่ได้จ่าย กรอก 0 หรือเว้นว่าง) */
    private JPanel buildActivityCard() {
        JPanel p = UI.clear(new GridBagLayout());
        JPanel header = UI.clear(new BorderLayout());
        header.add(UI.fieldLabel("ราคาของแต่ละคน (บาท)"), BorderLayout.WEST);
        samePriceButton.setToolTipText("คัดลอกราคาช่องแรกที่กรอกไว้ ไปใส่ทุกช่อง");
        header.add(samePriceButton, BorderLayout.EAST);
        add(p, header, 0);
        add(p, activityRows, 6);
        // คำอธิบาย 2 บรรทัด (บรรทัดเดียวยาวเกินความกว้างคอลัมน์ซ้าย)
        add(p, UI.label("ไม่ได้ไป / ไม่ได้จ่าย ให้กรอก 0 หรือเว้นว่าง", Font.PLAIN, 12, Theme.MUTED), 8);
        add(p, UI.label("ยอดรวม = ผลรวมของทุกช่อง", Font.PLAIN, 12, Theme.MUTED), 2);
        UI.pushUp(p);
        return p;
    }

    /** สร้างช่องราคารายคนใหม่ตามรายชื่อสมาชิก (เก็บค่าที่กรอกไว้เดิม) */
    private void rebuildActivityRows() {
        Map<Member, String> previous = new HashMap<>();
        activityPrices.forEach((m, f) -> previous.put(m, f.getText()));
        activityPrices.clear();
        activityRows.removeAll();

        if (trip.getMemberCount() == 0) {
            activityRows.add(UI.label("เพิ่มสมาชิกก่อน แล้วช่องราคาจะแสดงที่นี่", Font.PLAIN, 13, Theme.MUTED));
        }
        for (Member m : trip.getMembers()) {
            JLabel name = UI.memberLabel(m, 24);
            name.setIconTextGap(8);
            name.setPreferredSize(new Dimension(118, 28));

            StyledTextField price = new StyledTextField("0.00");
            price.setText(previous.getOrDefault(m, ""));
            price.getDocument().addDocumentListener(previewListener);
            price.addActionListener(e -> onAdd());
            activityPrices.put(m, price);

            JPanel line = UI.clear(new BorderLayout(10, 0));
            line.add(name, BorderLayout.WEST);
            line.add(price, BorderLayout.CENTER);
            line.add(UI.label("บาท", Font.PLAIN, 13, Theme.MUTED), BorderLayout.EAST);
            UI.addRow(activityRows, line, activityRows.getComponentCount() == 0 ? 0 : 6);
        }
        samePriceButton.setVisible(trip.getMemberCount() > 1);
        activityRows.revalidate();
        activityRows.repaint();
    }

    /** ปุ่ม "ใช้ราคาเดียวกันทุกคน": เอาราคาช่องแรกที่กรอกไว้ ไปใส่ทุกช่อง */
    private void fillSamePrice() {
        String first = null;
        for (StyledTextField f : activityPrices.values()) {
            if (!f.getText().trim().isEmpty()) {
                first = f.getText().trim();
                break;
            }
        }
        if (first == null) {
            UI.info(this, "กรอกราคาในช่องแรกก่อน แล้วกดปุ่มนี้เพื่อใส่ราคาเดียวกันทุกคน");
            return;
        }
        for (StyledTextField f : activityPrices.values()) {
            f.setText(first);
        }
    }

    // # 8. ตัวช่วยจัดวางทีละแถว (เรียก UI.addRow ที่ใช้ร่วมกันทุกฟอร์ม)
    /** เพิ่มแถวในการ์ดย่อยของแต่ละหมวด */
    private static void add(JPanel panel, JComponent c, int top) {
        UI.addRow(panel, c, top);
    }

    /** เพิ่มแถวในฟอร์มหลัก */
    private void addRow(JComponent c, int top) {
        UI.addRow(this, c, top);
    }

    // ===================== การทำงาน =====================

    // # 9. เลือกหมวด → ไฮไลต์ปุ่ม สลับช่องกรอก (กิจกรรมซ่อนส่วน "หารกับใครบ้าง")
    private void selectCategory(ExpenseCategory selected) {
        category = selected;
        categoryButtons.forEach((c, b) -> b.setSelected(c == selected));
        detailPanel.removeAll();
        detailPanel.add(detailCards.get(selected), BorderLayout.CENTER);
        // หมวดกิจกรรมกรอกราคารายคนอยู่แล้ว จึงไม่ต้องเลือกผู้ร่วมหาร
        boolean needParticipants = selected != ExpenseCategory.ACTIVITY;
        participantHeader.setVisible(needParticipants);
        participantPanel.setVisible(needParticipants);
        revalidate();
        repaint();
        updatePreview();
    }

    // # 10. อัปเดตรายชื่อผู้จ่าย / ผู้ร่วมหาร เมื่อสมาชิกเปลี่ยน
    /** อัปเดตรายชื่อผู้จ่าย และ checkbox ผู้ร่วมหาร เมื่อสมาชิกเปลี่ยน */
    @Override
    public void refresh() {
        Member previousPayer = (Member) payerCombo.getSelectedItem();
        Map<Member, Boolean> previousChecks = new HashMap<>();
        participantChecks.forEach((m, cb) -> previousChecks.put(m, cb.isSelected()));

        payerCombo.removeAllItems();
        for (Member m : trip.getMembers()) {
            payerCombo.addItem(m);
        }
        if (previousPayer != null && trip.getMembers().contains(previousPayer)) {
            payerCombo.setSelectedItem(previousPayer);
        }

        participantChecks.clear();
        participantPanel.removeAll();
        if (trip.getMemberCount() == 0) {
            participantPanel.setLayout(new BorderLayout());
            participantPanel.add(UI.label("เพิ่มสมาชิกก่อน แล้วรายชื่อจะแสดงที่นี่", Font.PLAIN, 13, Theme.MUTED));
        } else {
            participantPanel.setLayout(new GridLayout(0, 2, 6, 6));
            for (Member m : trip.getMembers()) {
                JCheckBox cb = UI.checkBox(m.getName());
                cb.setSelected(previousChecks.getOrDefault(m, true)); // คนใหม่เลือกไว้ให้อัตโนมัติ
                cb.addItemListener(e -> {
                    updateToggleAllText();
                    updatePreview();
                });
                participantChecks.put(m, cb);
                participantPanel.add(cb);
            }
        }
        toggleAllButton.setVisible(trip.getMemberCount() > 0);
        rebuildActivityRows();
        participantPanel.revalidate();
        participantPanel.repaint();
        updateToggleAllText();
        updatePreview();
    }

    // # 11. ปุ่ม "เลือกทั้งหมด / ไม่เลือกทั้งหมด" และอ่านคนที่ถูกเลือก
    private void toggleAll() {
        boolean allSelected = participantChecks.values().stream().allMatch(JCheckBox::isSelected);
        participantChecks.values().forEach(cb -> cb.setSelected(!allSelected));
    }

    private void updateToggleAllText() {
        boolean allSelected = !participantChecks.isEmpty()
                && participantChecks.values().stream().allMatch(JCheckBox::isSelected);
        toggleAllButton.setText(allSelected ? "ไม่เลือกทั้งหมด" : "เลือกทั้งหมด");
    }

    private List<Member> selectedParticipants() {
        List<Member> list = new ArrayList<>();
        participantChecks.forEach((m, cb) -> {
            if (cb.isSelected()) list.add(m);
        });
        return list;
    }

    // # 12. ★ สร้าง Object คลาสลูกตามหมวดที่เลือก (จุดเชื่อม UI → Model)
    /**
     * สร้าง Object ค่าใช้จ่ายตามหมวดที่เลือก
     * (ตรงนี้คือจุดที่ UI เลือกว่าจะสร้างคลาสลูกตัวไหน – หลังจากนี้ใช้งานผ่าน Expense ตัวเดียว)
     */
    private Expense buildExpense(String title) {
        Member payer = (Member) payerCombo.getSelectedItem();
        List<Member> participants = selectedParticipants();
        return switch (category) {
            case FOOD -> new FoodExpense(title, payer, participants,
                    foodAmount.getNumber("ค่าอาหาร"), serviceCharge.isSelected());
            case TRANSPORT -> new TransportExpense(title, payer, participants,
                    transportAmount.getNumber("ค่าเดินทาง"));
            case HOTEL -> new HotelExpense(title, payer, participants,
                    hotelPrice.getNumber("ราคาที่พักต่อคืน"), hotelNights.getValue(), hotelRooms.getValue());
            case ACTIVITY -> new ActivityExpense(title, payer, readActivityPrices());
        };
    }

    /** อ่านราคารายคนของหมวดกิจกรรม (ช่องว่าง = 0) */
    private Map<Member, Double> readActivityPrices() {
        Map<Member, Double> prices = new LinkedHashMap<>();
        activityPrices.forEach((m, field) ->
                prices.put(m, field.getAmountOrZero("ราคาของ " + m.getName())));
        return prices;
    }

    // # 13. คำนวณยอดตัวอย่างแบบสดขณะพิมพ์
    private void updatePreview() {
        try {
            Expense preview = buildExpense("preview");
            int n = preview.getParticipantCount();
            previewTotal.setText(Money.format(preview.getTotal()) + " บาท");
            if (preview instanceof ActivityExpense) {
                int skipped = trip.getMemberCount() - n;
                previewDetail.setText("จ่ายจริง " + n + " คน" + (skipped > 0 ? "  ·  ไม่ได้จ่าย " + skipped + " คน" : ""));
            } else {
                previewDetail.setText("หาร " + n + " คน  ·  เฉลี่ยคนละ "
                        + Money.format(preview.getTotal() / n) + " บาท");
            }
            previewDetail.setForeground(Theme.TEXT_SOFT);
        } catch (RuntimeException ex) {
            previewTotal.setText("0.00 บาท");
            previewDetail.setText(trip.getMemberCount() == 0
                    ? "เพิ่มสมาชิกในทริปก่อนนะ"
                    : category == ExpenseCategory.ACTIVITY
                    ? "กรอกราคาของแต่ละคนเพื่อดูยอด"
                    : "กรอกจำนวนเงินและเลือกผู้ร่วมหารเพื่อดูยอด");
            previewDetail.setForeground(Theme.MUTED);
        }
    }

    // # 14. กดบันทึก → ตรวจข้อมูล → trip.addExpense() → แจ้งเตือนด้วย JOptionPane ถ้าผิด
    private void onAdd() {
        if (trip.getMemberCount() == 0) {
            UI.warn(this, "กรุณาเพิ่มสมาชิกในทริปก่อน");
            return;
        }
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            UI.warn(this, "กรุณากรอกชื่อรายการค่าใช้จ่าย");
            titleField.requestFocusInWindow();
            return;
        }
        try {
            Expense expense = buildExpense(title);
            trip.addExpense(expense);
            clearInputs();
            onChange.run();
            flashSuccess("บันทึก \"" + expense.getTitle() + "\" เรียบร้อย");
        } catch (IllegalArgumentException ex) {
            UI.warn(this, ex.getMessage());
        }
    }

    // # 15. ล้างฟอร์มหลังบันทึกสำเร็จ
    private void clearInputs() {
        titleField.setText("");
        foodAmount.setText("");
        transportAmount.setText("");
        hotelPrice.setText("");
        activityPrices.values().forEach(f -> f.setText(""));
        serviceCharge.setSelected(false);
        hotelNights.setValue(1);
        hotelRooms.setValue(1);
        titleField.requestFocusInWindow();
    }

    // # 16. แสดงข้อความสำเร็จสีเขียวชั่วคราว 1.8 วินาที
    /** แสดงข้อความสำเร็จสีเขียวในกล่องตัวอย่างชั่วคราว */
    private void flashSuccess(String message) {
        previewBox.setFill(Theme.SUCCESS_SOFT);
        previewDetail.setText(message);
        previewDetail.setForeground(Theme.SUCCESS);
        Timer timer = new Timer(1800, e -> {
            previewBox.setFill(Theme.PRIMARY_SOFT);
            updatePreview();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
