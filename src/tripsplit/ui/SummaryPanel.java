// ############################################################################
// # ไฟล์     : SummaryPanel.java
// # ส่วน     : UI – การ์ดสรุป
// # หน้าที่   : การ์ดสรุป 3 แท็บ: ใครต้องโอนให้ใคร / ยอดรายคน / สัดส่วนค่าใช้จ่าย
// # จุดสำคัญ : Inheritance (extends TripCard), แสดงผลจาก trip.calculateSettlements() และ getTotalByCategory()
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Money;
import tripsplit.model.Settlement;
import tripsplit.model.Trip;
import tripsplit.ui.components.DonutChart;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.PillLabel;
import tripsplit.ui.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** การ์ดสรุปผล มี 3 แท็บ: ใครต้องโอนให้ใคร / ยอดรายคน / สัดส่วนค่าใช้จ่าย */
public class SummaryPanel extends TripCard {

    // # 1. ชื่อแท็บทั้ง 3
    public static final String TAB_SETTLE = "settle";
    public static final String TAB_BALANCE = "balance";
    public static final String TAB_CHART = "chart";

    // # 2. ส่วนประกอบของการ์ด: แท็บ, รายการโอน, ตารางยอดรายคน, กราฟ
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = UI.clear(cards);
    private final Map<String, ModernButton> tabs = new LinkedHashMap<>();

    private final JPanel settlementList = UI.clear(null);
    private final JLabel settleNote = UI.label(" ", Font.PLAIN, 13, Theme.MUTED);
    private final ModernButton copyButton = new ModernButton("คัดลอกสรุป", ModernButton.Style.SECONDARY)
            .withPadding(7, 14).withFont(Theme.font(Font.BOLD, 13));

    private final BalanceTableModel balanceModel;
    private final DonutChart chart = new DonutChart();

    // # 3. Constructor — สร้างหัวการ์ด และเนื้อหาทั้ง 3 แท็บ
    public SummaryPanel(Trip trip) {
        super(trip, 16, 18);
        this.balanceModel = new BalanceTableModel(trip);
        setLayout(new BorderLayout(0, 12));

        // ---------- หัวการ์ด + แท็บ ----------
        JPanel header = UI.clear(new BorderLayout());
        header.add(UI.title("สรุปการหารเงิน"), BorderLayout.WEST);
        JPanel tabBar = UI.clear(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        addTab(tabBar, TAB_SETTLE, "ใครต้องโอนให้ใคร");
        addTab(tabBar, TAB_BALANCE, "ยอดรายคน");
        addTab(tabBar, TAB_CHART, "สัดส่วนค่าใช้จ่าย");
        header.add(tabBar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------- แท็บ 1: ใครต้องโอนให้ใคร ----------
        settlementList.setLayout(new BoxLayout(settlementList, BoxLayout.Y_AXIS));
        JPanel settleView = UI.clear(new BorderLayout(0, 10));
        JScrollPane settleScroll = UI.scroll(settlementList, Color.WHITE);
        settleView.add(settleScroll, BorderLayout.CENTER);
        JPanel settleFooter = UI.clear(new BorderLayout());
        settleFooter.add(settleNote, BorderLayout.WEST);
        settleFooter.add(copyButton, BorderLayout.EAST);
        copyButton.setToolTipText("คัดลอกข้อความสรุป ไปวางในแชทกลุ่มได้เลย");
        copyButton.addActionListener(e -> copySummary());
        settleView.add(settleFooter, BorderLayout.SOUTH);
        cardPanel.add(settleView, TAB_SETTLE);

        // ---------- แท็บ 2: ยอดรายคน ----------
        JTable balanceTable = new JTable(balanceModel);
        UI.styleTable(balanceTable);
        balanceTable.getColumnModel().getColumn(3).setCellRenderer(new TableRenderers.BalanceStatusRenderer());
        balanceTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        balanceTable.getColumnModel().getColumn(3).setPreferredWidth(190);
        JScrollPane balanceScroll = UI.scroll(balanceTable, Color.WHITE);
        balanceScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        cardPanel.add(balanceScroll, TAB_BALANCE);

        // ---------- แท็บ 3: กราฟ ----------
        cardPanel.add(chart, TAB_CHART);

        add(cardPanel, BorderLayout.CENTER);
        showTab(TAB_SETTLE);
    }

    // # 4. สร้างปุ่มแท็บ
    private void addTab(JPanel bar, String key, String text) {
        ModernButton b = new ModernButton(text, ModernButton.Style.TAB)
                .withPadding(6, 12).withFont(Theme.font(Font.BOLD, 13));
        b.addActionListener(e -> showTab(key));
        tabs.put(key, b);
        bar.add(b);
    }

    // # 5. สลับแท็บ (CardLayout) + เปลี่ยนสีปุ่มแท็บ
    public void showTab(String key) {
        cards.show(cardPanel, key);
        tabs.forEach((k, b) -> b.setStyle(k.equals(key) ? ModernButton.Style.TAB_ACTIVE : ModernButton.Style.TAB));
    }

    // # 6. คำนวณใหม่และวาดทุกแท็บ
    /** คำนวณใหม่และวาดทุกแท็บ */
    @Override
    public void refresh() {
        refreshSettlements();
        balanceModel.fireTableDataChanged();
        chart.setData(trip.getTotalByCategory());
    }

    // # 7. แท็บ 1: รายการโอนเงิน (เรียก trip.calculateSettlements())
    private void refreshSettlements() {
        settlementList.removeAll();
        List<Settlement> settlements = trip.calculateSettlements();

        if (trip.getExpenseCount() == 0) {
            settlementList.add(emptyState("ยังไม่มีข้อมูลสำหรับคำนวณ",
                    "เพิ่มสมาชิกและค่าใช้จ่าย แล้วระบบจะบอกว่าใครต้องโอนให้ใคร"));
            settleNote.setText(" ");
            copyButton.setEnabled(false);
        } else if (settlements.isEmpty()) {
            settlementList.add(emptyState("เคลียร์ครบทุกคนแล้ว",
                    "ทุกคนจ่ายพอดีกับส่วนของตัวเอง ไม่ต้องโอนเงินเพิ่ม"));
            settleNote.setText("ไม่ต้องโอนเงิน");
            copyButton.setEnabled(true);
        } else {
            for (int i = 0; i < settlements.size(); i++) {
                if (i > 0) settlementList.add(Box.createVerticalStrut(8));
                settlementList.add(settlementRow(i + 1, settlements.get(i)));
            }
            settlementList.add(Box.createVerticalGlue());
            settleNote.setText("โอนทั้งหมด " + settlements.size() + " ครั้ง ก็เคลียร์ครบทุกคน");
            copyButton.setEnabled(true);
        }
        settlementList.revalidate();
        settlementList.repaint();
    }

    // # 8. แถวโอนเงิน 1 รายการ: ใคร → โอนให้ → ใคร → เท่าไร
    private JPanel settlementRow(int number, Settlement s) {
        RoundedPanel row = new RoundedPanel(12, Theme.SURFACE, Theme.BORDER, false);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));

        JPanel left = UI.clear(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel num = UI.label(number + ".", Font.BOLD, 13, Theme.PLACEHOLDER);
        num.setPreferredSize(new Dimension(24, 30));
        left.add(num);

        left.add(UI.memberLabel(s.getFrom(), 30));

        JLabel arrow = new JLabel("โอนให้", new Icons.ArrowIcon(Theme.PRIMARY, 26), JLabel.LEFT);
        arrow.setFont(Theme.font(Font.PLAIN, 13));
        arrow.setForeground(Theme.MUTED);
        arrow.setHorizontalTextPosition(JLabel.LEFT);
        arrow.setIconTextGap(8);
        arrow.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        left.add(arrow);

        left.add(UI.memberLabel(s.getTo(), 30));

        PillLabel amount = new PillLabel(Money.format(s.getAmount()) + " บาท", Theme.ACCENT_SOFT,
                Theme.mix(Theme.ACCENT, Color.BLACK, 0.15));
        amount.setFont(Theme.font(Theme.SEMIBOLD, 14));
        amount.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        JPanel right = UI.clear(new GridBagLayout());
        right.add(amount);

        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return UI.fixHeight(row);
    }

    // # 9. ข้อความตอนยังไม่มีข้อมูล
    private JPanel emptyState(String title, String hint) {
        JPanel p = UI.clear(new GridLayout(2, 1, 0, 4));
        p.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        JLabel t = UI.label(title, Theme.HEADING, 16, Theme.TEXT_SOFT);
        t.setHorizontalAlignment(JLabel.CENTER);
        JLabel h = UI.label(hint, Font.PLAIN, 13, Theme.MUTED);
        h.setHorizontalAlignment(JLabel.CENTER);
        p.add(t);
        p.add(h);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    // # 10. คัดลอกข้อความสรุปไป Clipboard
    private void copySummary() {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(trip.buildSummaryText()), null);
        UI.info(this, "คัดลอกสรุปเรียบร้อย\nนำไปวางในแชทกลุ่มได้เลย (Ctrl + V)");
    }
}
