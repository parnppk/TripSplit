// ############################################################################
// # ไฟล์     : TripSplitForm.java
// # ส่วน     : UI – หน้าต่างหลัก (JFrame)
// # หน้าที่   : ประกอบหน้าจอทั้งหมด และสั่งวาดใหม่เมื่อข้อมูลใน Trip เปลี่ยน
// # จุดสำคัญ : ไม่มี Logic การคำนวณในคลาสนี้เลย (แยก Model กับ UI)
// ############################################################################
package tripsplit.ui;

import tripsplit.SampleData;
import tripsplit.model.Trip;
import tripsplit.ui.components.HeroBanner;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernButton;
import tripsplit.ui.components.TopNavBar;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * หน้าต่างหลักของโปรแกรม (View)
 * หน้าที่: จัดวางหน้าจอ และสั่งให้ส่วนต่าง ๆ วาดใหม่เมื่อข้อมูลใน Trip เปลี่ยน
 * ไม่มี Logic การคำนวณอยู่ในคลาสนี้เลย
 */
public class TripSplitForm extends JFrame {

    // # 1. Model: ทริปปัจจุบัน (ข้อมูลทั้งหมดอยู่ใน Object นี้)
    private final Trip trip = new Trip("ทริปของฉัน");

    // # 2. ส่วนประกอบหลัก: การ์ดสรุป 4 ใบ, การ์ดสมาชิก, ฟอร์ม, ตารางค่าใช้จ่าย, การ์ดสรุปการหารเงิน
    private final StatsPanel statsPanel = new StatsPanel(trip);
    private final MemberPanel memberPanel = new MemberPanel(trip, this::refreshAll);
    private final ExpenseFormPanel expenseFormPanel = new ExpenseFormPanel(trip, this::refreshAll);
    private final ExpenseListPanel expenseListPanel =
            new ExpenseListPanel(trip, this::refreshAll, () -> loadSampleData(false));
    private final SummaryPanel summaryPanel = new SummaryPanel(trip);

    /** ทุกส่วนที่ต้องวาดใหม่เมื่อข้อมูลเปลี่ยน (Polymorphism: เรียก refresh() เหมือนกันหมด) */
    private final List<Refreshable> views =
            List.of(statsPanel, memberPanel, expenseFormPanel, expenseListPanel, summaryPanel);

    // # 3. ชื่อมุมมองของแท็บ + แถบเมนู + แบนเนอร์
    public static final String VIEW_OVERVIEW = "overview";
    public static final String VIEW_EXPENSES = "expenses";
    public static final String VIEW_SUMMARY = "summary";

    private final TopNavBar topNav = new TopNavBar(this::showView);
    private final HeroBanner hero = new HeroBanner(trip, this::editTrip, this::focusAddMember);
    private JComponent body;
    private JScrollPane pageScroll;

    // # 4. Constructor — ประกอบหน้าจอ (แถบเมนู + หน้าเนื้อหา) และตั้งขนาดหน้าต่าง
    public TripSplitForm() {
        super("Trip Split - ระบบจัดทริปเที่ยวและหารค่าใช้จ่าย");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createAppIcon());

        body = buildBody();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(buildTopNav(), BorderLayout.NORTH);
        root.add(buildPage(), BorderLayout.CENTER);
        setContentPane(root);

        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1360, screen.width - 20), Math.min(860, screen.height - 10));
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);
        // จอโน้ตบุ๊กขนาดเล็ก (หรือตั้งค่า Scale 125%+) ให้เปิดเต็มจอ เพื่อให้เห็นข้อมูลครบ
        if (screen.width < 1440 || screen.height < 900) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        refreshAll();
    }

    // # 5. ไอคอนโปรแกรมบน Taskbar
    /** ไอคอนโปรแกรมบน Taskbar (วาดเครื่องบินกระดาษบนพื้นไล่สี) */
    private static Image createAppIcon() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = Theme.aa(img.getGraphics());
        g2.setPaint(new GradientPaint(0, 0, Theme.HEADER_START, s, s, Theme.HEADER_END));
        g2.fillRoundRect(0, 0, s, s, 28, 28);
        new Icons.LogoIcon(s).paintIcon(null, g2, 0, 0);
        g2.dispose();
        return img;
    }

    // =====================================================================
    // แถบเมนูด้านบน
    // =====================================================================

    // # 6. แถบเมนูด้านบน: แท็บ + ปุ่ม "ทริปของฉัน" + ปุ่ม "สร้างทริป"
    private JComponent buildTopNav() {
        topNav.addTab(VIEW_OVERVIEW, "ภาพรวม", new Icons.LineIcon(Icons.LineIcon.Type.HOME, 17));
        topNav.addTab(VIEW_EXPENSES, "รายการค่าใช้จ่าย", new Icons.LineIcon(Icons.LineIcon.Type.LIST, 17));
        topNav.addTab(VIEW_SUMMARY, "สรุปการหารเงิน", new Icons.LineIcon(Icons.LineIcon.Type.PIE, 17));

        ModernButton myTrip = new ModernButton("ทริปของฉัน", ModernButton.Style.SECONDARY)
                .withPadding(9, 16)
                .withIcon(new Icons.LineIcon(Icons.LineIcon.Type.BRIEFCASE, 16), 9)
                .withFont(Theme.font(Font.BOLD, 14));
        myTrip.addActionListener(e -> showTripMenu(myTrip));

        ModernButton create = new ModernButton("สร้างทริป", ModernButton.Style.PRIMARY)
                .withPadding(9, 18)
                .withIcon(new Icons.LineIcon(Icons.LineIcon.Type.PLUS, 15, Color.WHITE), 9)
                .withFont(Theme.font(Font.BOLD, 14));
        create.addActionListener(e -> createTrip());

        topNav.addAction(myTrip);
        topNav.addAction(create);
        return topNav;
    }

    // # 7. เมนูของปุ่ม "ทริปของฉัน"
    private void showTripMenu(JComponent anchor) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)));
        menu.setBackground(Color.WHITE);

        JLabel header = UI.label(trip.getName(), Theme.HEADING, 14, Theme.TEXT);
        header.setBorder(BorderFactory.createEmptyBorder(6, 16, 0, 16));
        JLabel sub = UI.label(ThaiDates.formatRange(trip.getStartDate(), trip.getEndDate())
                + "  ·  " + trip.getMemberCount() + " คน", Font.PLAIN, 12, Theme.MUTED);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        menu.add(header);
        menu.add(sub);
        menu.addSeparator();
        menu.add(menuItem("แก้ไขชื่อและวันที่ทริป", Icons.LineIcon.Type.EDIT, this::editTrip));
        menu.add(menuItem("โหลดข้อมูลตัวอย่าง", Icons.LineIcon.Type.SPARKLE, () -> loadSampleData(true)));
        menu.add(menuItem("ไปที่สรุปการหารเงิน", Icons.LineIcon.Type.PIE, () -> showView(VIEW_SUMMARY)));
        menu.show(anchor, anchor.getWidth() - menu.getPreferredSize().width, anchor.getHeight() + 6);
    }

    private static JMenuItem menuItem(String text, Icons.LineIcon.Type icon, Runnable action) {
        JMenuItem item = new JMenuItem(text, new Icons.LineIcon(icon, 16, Theme.PRIMARY));
        item.setFont(Theme.font(Font.PLAIN, 14));
        item.setForeground(Theme.TEXT);
        item.setBackground(Color.WHITE);
        item.setIconTextGap(12);
        item.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 28));
        item.addActionListener(e -> action.run());
        return item;
    }

    // =====================================================================
    // หน้าเนื้อหา (แบนเนอร์ + เนื้อหาหลัก) เลื่อนขึ้นลงได้ทั้งหน้า
    // =====================================================================

    // # 8. หน้าเนื้อหา (แบนเนอร์ + เนื้อหาหลัก) ที่เลื่อนขึ้นลงได้
    private JComponent buildPage() {
        PagePanel page = new PagePanel(HeroBanner.BANNER_HEIGHT + 760);
        page.setBackground(Theme.BG);
        page.add(hero, BorderLayout.NORTH);
        page.add(body, BorderLayout.CENTER);
        pageScroll = UI.scroll(page, Theme.BG);
        pageScroll.getVerticalScrollBar().setUnitIncrement(24);
        return pageScroll;
    }

    // # 9. Panel ที่กว้างเต็มหน้าต่าง มีความสูงขั้นต่ำ ถ้าจอเตี้ยกว่าจะเลื่อนได้
    /** Panel ที่กว้างเท่าหน้าต่างเสมอ และสูงอย่างน้อย minHeight (เกินแล้วจะเลื่อนได้) */
    private static class PagePanel extends JPanel implements Scrollable {
        private final int minHeight;

        PagePanel(int minHeight) {
            super(new BorderLayout());
            this.minHeight = minHeight;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, minHeight);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, visibleRect.height - 60);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport && getParent().getHeight() > minHeight;
        }
    }

    // # 10. สลับมุมมองตามแท็บ: ภาพรวม / รายการค่าใช้จ่าย / สรุปการหารเงิน
    private void showView(String view) {
        topNav.setActive(view);
        expenseListPanel.setVisible(!VIEW_SUMMARY.equals(view));
        summaryPanel.setVisible(!VIEW_EXPENSES.equals(view));
        body.revalidate();
        body.repaint();
        SwingUtilities.invokeLater(() -> {
            // ภาพรวม = เลื่อนขึ้นบนสุด / แท็บอื่น = ซ่อนแบนเนอร์ เพื่อให้เห็นตาราง/สรุปเต็ม ๆ
            int top = VIEW_OVERVIEW.equals(view) ? 0 : hero.getHeight();
            pageScroll.getVerticalScrollBar().setValue(top);
        });
    }

    // # 11. เนื้อหาหลัก: ซ้าย = สมาชิก + ฟอร์ม / ขวา = การ์ดสรุป + ตาราง + สรุป
    private JComponent buildBody() {
        JPanel content = UI.clear(new BorderLayout(16, 0));
        content.setBorder(BorderFactory.createEmptyBorder(18, 16, 16, 16));
        content.add(buildLeftColumn(), BorderLayout.WEST);
        content.add(buildRightColumn(), BorderLayout.CENTER);
        return content;
    }

    /** คอลัมน์ซ้าย: การ์ดสมาชิก + ฟอร์มเพิ่มค่าใช้จ่าย (เลื่อนแยกได้) */
    private JComponent buildLeftColumn() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(Theme.BG);
        left.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 6));
        UI.addRow(left, memberPanel, 0);
        UI.addRow(left, expenseFormPanel, 14);
        UI.pushUp(left);

        JScrollPane leftScroll = UI.scroll(left, Theme.BG);
        leftScroll.setPreferredSize(new Dimension(392, 100));
        return leftScroll;
    }

    /** คอลัมน์ขวา: การ์ดสรุป 4 ใบ + ตารางค่าใช้จ่าย (52%) + สรุปการหารเงิน (48%) */
    private JComponent buildRightColumn() {
        JPanel main = UI.clear(new GridBagLayout());
        GridBagConstraints mc = new GridBagConstraints();
        mc.gridx = 0;
        mc.weightx = 1;
        mc.fill = GridBagConstraints.BOTH;
        mc.gridy = 0;
        main.add(statsPanel, mc);

        mc.insets = new Insets(14, 0, 0, 0);
        mc.gridy = 1;
        mc.weighty = 0.52;
        expenseListPanel.setPreferredSize(new Dimension(100, 100));
        main.add(expenseListPanel, mc);

        mc.gridy = 2;
        mc.weighty = 0.48;
        summaryPanel.setPreferredSize(new Dimension(100, 100));
        main.add(summaryPanel, mc);
        return main;
    }

    // =====================================================================
    // การทำงาน
    // =====================================================================

    // # 12. ★ วาดทุกส่วนใหม่จากข้อมูลใน Trip (เรียกทุกครั้งที่ข้อมูลเปลี่ยน)
    /** วาดทุกส่วนใหม่จากข้อมูลล่าสุดใน Trip (เรียกทุกครั้งที่ข้อมูลเปลี่ยน) */
    public final void refreshAll() {
        hero.repaint();
        for (Refreshable view : views) {
            view.refresh(); // Polymorphism: แต่ละส่วนวาดตัวเองคนละแบบ
        }
        revalidate();
        repaint();
    }

    // # 13. แก้ไขทริป / สร้างทริปใหม่ (เปิด TripDialog)
    /** แก้ไขชื่อและวันที่ของทริปปัจจุบัน */
    private void editTrip() {
        TripDialog.Result r = TripDialog.showEdit(this, trip);
        if (r == null) return;
        trip.setName(r.name);
        trip.setDates(r.start, r.end);
        refreshAll();
    }

    /** สร้างทริปใหม่ (ล้างข้อมูลเดิม) */
    private void createTrip() {
        TripDialog.Result r = TripDialog.showCreate(this);
        if (r == null) return;
        if (trip.hasData() && !UI.confirm(this,
                "ข้อมูลของทริป \"" + trip.getName() + "\" จะถูกล้างทั้งหมด\nต้องการสร้างทริปใหม่ใช่หรือไม่?")) {
            return;
        }
        trip.clear();
        trip.setName(r.name);
        trip.setDates(r.start, r.end);
        showView(VIEW_OVERVIEW);
        refreshAll();
        focusAddMember();
    }

    // # 14. ปุ่ม + บนแบนเนอร์ → ไปที่ช่องเพิ่มสมาชิก
    private void focusAddMember() {
        memberPanel.focusNameField();
    }

    // # 15. โหลดข้อมูลตัวอย่าง
    /** โหลดข้อมูลตัวอย่าง (askFirst = ถามยืนยันก่อน ถ้ามีข้อมูลอยู่แล้ว) */
    public void loadSampleData(boolean askFirst) {
        if (askFirst && trip.hasData() && !UI.confirm(this,
                "ข้อมูลปัจจุบันจะถูกแทนที่ด้วยข้อมูลตัวอย่าง\nต้องการดำเนินการต่อหรือไม่?")) {
            return;
        }
        SampleData.load(trip);
        refreshAll();
        showView(VIEW_OVERVIEW);
    }

    // # 16. Getter (ใช้ตอนทดสอบ/จับภาพหน้าจอ)
    public SummaryPanel getSummaryPanel() {
        return summaryPanel;
    }
}
