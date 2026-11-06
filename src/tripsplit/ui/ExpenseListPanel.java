// ############################################################################
// # ไฟล์     : ExpenseListPanel.java
// # ส่วน     : UI – การ์ดตารางค่าใช้จ่าย
// # หน้าที่   : แสดงรายการค่าใช้จ่ายทั้งหมดเป็นตาราง + ลบรายการที่เลือก
// # จุดสำคัญ : Inheritance (extends TripCard), เรียก trip.removeExpense (Logic อยู่ใน Model)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Expense;
import tripsplit.model.ExpenseCategory;
import tripsplit.model.Money;
import tripsplit.model.Trip;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.RoundedPanel;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/** การ์ดตารางรายการค่าใช้จ่าย (เลือกแถวแล้วกดปุ่มลบ หรือปุ่ม Delete บนคีย์บอร์ด) */
public class ExpenseListPanel extends TripCard {

    // # 1. ชื่อหน้าของ CardLayout: ตาราง / หน้าว่าง
    private static final String CARD_TABLE = "table";
    private static final String CARD_EMPTY = "empty";

    // # 2. callback แจ้งหน้าหลักให้วาดใหม่ + ส่วนประกอบของการ์ด
    private final Runnable onChange;
    private final ExpenseTableModel tableModel;
    private final JTable table;
    private final CardLayout cards = new CardLayout();
    private final JPanel tableArea = UI.clear(cards);
    private final JLabel subtitle = UI.subtitle(" ");
    private final ModernButton deleteButton =
            new ModernButton("ลบรายการที่เลือก", ModernButton.Style.DANGER_OUTLINE)
                    .withPadding(7, 14).withFont(Theme.font(Font.BOLD, 13));

    // # 3. Constructor — หัวการ์ด + ตาราง + หน้าว่าง
    public ExpenseListPanel(Trip trip, Runnable onChange, Runnable onLoadSample) {
        super(trip, 16, 18);
        this.onChange = onChange;
        this.tableModel = new ExpenseTableModel(trip);
        this.table = new JTable(tableModel);
        setLayout(new BorderLayout(0, 12));

        // ---------- หัวการ์ด ----------
        JPanel header = UI.clear(new BorderLayout());
        header.add(titleBlock("รายการค่าใช้จ่าย", subtitle), BorderLayout.CENTER);
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        JPanel btnWrap = UI.clear(new GridBagLayout());
        btnWrap.add(deleteButton);
        header.add(btnWrap, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------- ตาราง + หน้าว่าง ----------
        tableArea.add(buildTable(), CARD_TABLE);
        tableArea.add(buildEmptyState(onLoadSample), CARD_EMPTY);
        add(tableArea, BorderLayout.CENTER);
    }

    // # 4. ตาราง: Renderer ของแต่ละคอลัมน์ + กดปุ่ม Delete เพื่อลบ
    private JComponent buildTable() {
        UI.styleTable(table);
        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(1).setCellRenderer(new TableRenderers.BoldRenderer());
        columns.getColumn(2).setCellRenderer(new TableRenderers.MutedRenderer());
        columns.getColumn(4).setCellRenderer(new TableRenderers.MutedRenderer());
        int[] widths = {140, 210, 200, 120, 140, 120};
        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getSelectionModel().addListSelectionListener(e -> updateDeleteButton());
        table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteExpense");
        table.getActionMap().put("deleteExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedExpense();
            }
        });

        JScrollPane scroll = UI.scroll(table, Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return scroll;
    }

    // # 5. หน้าว่างตอนยังไม่มีรายการ + ปุ่มลองใช้ข้อมูลตัวอย่าง
    private JComponent buildEmptyState(Runnable onLoadSample) {
        RoundedPanel empty = new RoundedPanel(12, Theme.SURFACE, Theme.BORDER, false);
        empty.setLayout(new GridBagLayout());
        JPanel content = UI.clear(null);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JPanel icons = UI.clear(new FlowLayout(FlowLayout.CENTER, 8, 0));
        for (ExpenseCategory c : ExpenseCategory.values()) {
            icons.add(new JLabel(new Icons.CategoryIcon(c, 34, false)));
        }
        JLabel title = UI.label("ยังไม่มีรายการค่าใช้จ่าย", Theme.HEADING, 16, Theme.TEXT_SOFT);
        title.setHorizontalAlignment(JLabel.CENTER);
        JLabel hint = UI.label("เพิ่มจากฟอร์มด้านซ้าย หรือลองใช้ข้อมูลตัวอย่างเพื่อดูการทำงาน",
                Font.PLAIN, 13, Theme.MUTED);
        hint.setHorizontalAlignment(JLabel.CENTER);

        ModernButton sample = new ModernButton("ลองใช้ข้อมูลตัวอย่าง", ModernButton.Style.SECONDARY)
                .withPadding(7, 16)
                .withIcon(new Icons.LineIcon(Icons.LineIcon.Type.SPARKLE, 15, Theme.PRIMARY), 8)
                .withFont(Theme.font(Font.BOLD, 13));
        sample.addActionListener(e -> onLoadSample.run());
        JPanel sampleWrap = UI.clear(new FlowLayout(FlowLayout.CENTER, 0, 4));
        sampleWrap.add(sample);

        for (JComponent c : new JComponent[]{icons, title, hint, sampleWrap}) {
            c.setAlignmentX(0.5f);
        }
        content.add(icons);
        content.add(Box.createVerticalStrut(10));
        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(hint);
        content.add(Box.createVerticalStrut(10));
        content.add(sampleWrap);
        empty.add(content);
        return empty;
    }

    // # 6. วาดใหม่: ข้อมูลในตาราง, สลับตาราง/หน้าว่าง, คำอธิบายใต้หัวข้อ
    @Override
    public void refresh() {
        boolean empty = trip.getExpenseCount() == 0;
        tableModel.fireTableDataChanged();
        cards.show(tableArea, empty ? CARD_EMPTY : CARD_TABLE);
        subtitle.setText(empty
                ? "บันทึกค่าใช้จ่ายระหว่างทริปได้ที่นี่"
                : "ทั้งหมด " + trip.getExpenseCount() + " รายการ  ·  เลือกแถวแล้วกด Delete เพื่อลบได้");
        updateDeleteButton();
    }

    // # 7. ลบรายการที่เลือกในตาราง (ถามยืนยันก่อน)
    private void updateDeleteButton() {
        deleteButton.setVisible(trip.getExpenseCount() > 0);
        deleteButton.setEnabled(table.getSelectedRow() >= 0);
    }

    private void deleteSelectedExpense() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.info(this, "กรุณาเลือกรายการที่ต้องการลบในตารางก่อน");
            return;
        }
        Expense expense = tableModel.getExpenseAt(table.convertRowIndexToModel(row));
        if (UI.confirm(this, "ต้องการลบรายการ \"" + expense.getTitle() + "\" ("
                + Money.format(expense.getTotal()) + " บาท) ใช่หรือไม่?")) {
            trip.removeExpense(expense);
            onChange.run();
        }
    }
}
