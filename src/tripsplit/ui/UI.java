// ############################################################################
// # ไฟล์     : UI.java
// # ส่วน     : UI – ฟังก์ชันช่วย
// # หน้าที่   : สร้าง/ตกแต่ง Component ให้หน้าตาเหมือนกันทั้งโปรแกรม
// # จุดสำคัญ : static helper method
// ############################################################################
package tripsplit.ui;

import tripsplit.model.ExpenseCategory;
import tripsplit.model.Member;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.ModernScrollBarUI;
import tripsplit.ui.components.RoundedBorder;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;

/** ฟังก์ชันช่วยสร้าง/ตกแต่ง Component ให้หน้าตาเหมือนกันทั้งโปรแกรม */
public final class UI {

    private UI() {
    }

    // ===================== Panel / Label =====================

    // # 1. สร้าง Panel โปร่งใส และ Label แบบต่าง ๆ (หัวข้อ, คำอธิบาย, ป้ายช่องกรอก)
    public static JPanel clear(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(style, size));
        l.setForeground(color);
        return l;
    }

    public static JLabel title(String text) {
        return label(text, Theme.HEADING, 17, Theme.TEXT);
    }

    public static JLabel subtitle(String text) {
        return label(text, Font.PLAIN, 13, Theme.MUTED);
    }

    public static JLabel fieldLabel(String text) {
        return label(text, Font.BOLD, 13, Theme.TEXT_SOFT);
    }

    /** ชื่อสมาชิกพร้อมรูป avatar นำหน้า (ใช้ในป้ายชื่อ, รายการโอน, ช่องราคารายคน) */
    public static JLabel memberLabel(Member member, int avatarSize) {
        JLabel l = new JLabel(member.getName(), new Icons.AvatarIcon(member.getName(), avatarSize), JLabel.LEFT);
        l.setFont(Theme.font(Font.BOLD, 14));
        l.setForeground(Theme.TEXT);
        l.setIconTextGap(9);
        l.setToolTipText(member.getName());
        return l;
    }

    // ===================== จัดวางแนวตั้ง (GridBagLayout) =====================

    // # 2. ตัวช่วยจัดวางทีละแถวจากบนลงล่าง (ใช้ร่วมกันทุกฟอร์ม)
    /** เพิ่ม component ต่อท้ายเป็นแถวใหม่ กว้างเต็ม เว้นด้านบนตาม top (panel ต้องใช้ GridBagLayout) */
    public static void addRow(Container panel, Component c, int top) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = panel.getComponentCount();
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(top, 0, 0, 0);
        panel.add(c, gc);
    }

    /** เพิ่มช่องว่างยืดได้ท้ายสุด เพื่อดันทุกแถวขึ้นไปชิดด้านบน */
    public static void pushUp(Container panel) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = panel.getComponentCount();
        gc.weighty = 1;
        panel.add(clear(new BorderLayout()), gc);
    }

    // # 3. ScrollPane แบบเรียบ + ล้อเมาส์ (เลื่อนสุดแล้วส่งต่อให้หน้าหลัก)
    public static JScrollPane scroll(Component view, Color background) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setViewportBorder(null);
        sp.getViewport().setBackground(background);
        sp.setBackground(background);
        ModernScrollBarUI.apply(sp, background);
        // ล้อเมาส์: เลื่อนตัวเองจนสุดแล้ว ส่งต่อให้หน้าหลักเลื่อนต่อ (กันอาการเลื่อนค้าง)
        sp.setWheelScrollingEnabled(false);
        sp.addMouseWheelListener(e -> handleWheel(sp, e));
        return sp;
    }

    private static void handleWheel(JScrollPane sp, MouseWheelEvent e) {
        JScrollBar bar = sp.getVerticalScrollBar();
        double rotation = e.getPreciseWheelRotation();
        if (rotation == 0) return;
        int dir = rotation < 0 ? -1 : 1;
        boolean canScroll = bar.isVisible() && (dir < 0
                ? bar.getValue() > bar.getMinimum()
                : bar.getValue() + bar.getModel().getExtent() < bar.getMaximum());
        if (canScroll) {
            int delta = (int) Math.round(rotation * bar.getUnitIncrement(dir) * Math.max(1, e.getScrollAmount()));
            bar.setValue(bar.getValue() + (delta == 0 ? dir : delta));
        } else {
            Container parent = SwingUtilities.getAncestorOfClass(JScrollPane.class, sp);
            if (parent != null) {
                parent.dispatchEvent(SwingUtilities.convertMouseEvent(sp, e, parent));
            }
        }
    }

    // ===================== Checkbox =====================

    // # 4. Checkbox มุมโค้ง
    public static JCheckBox checkBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        Icons.CheckIcon icon = new Icons.CheckIcon();
        cb.setIcon(icon);
        cb.setSelectedIcon(icon);
        cb.setRolloverIcon(icon);
        cb.setRolloverSelectedIcon(icon);
        cb.setRolloverEnabled(true);
        cb.setIconTextGap(8);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setFont(Theme.font(Font.PLAIN, 14));
        cb.setForeground(Theme.TEXT);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    // ===================== ComboBox =====================

    // # 5. ComboBox ผู้จ่าย (มี avatar นำหน้าชื่อ)
    /** ตกแต่ง ComboBox ของสมาชิก ให้มี avatar นำหน้าชื่อ */
    public static void styleMemberCombo(JComboBox<Member> combo) {
        combo.setUI(new FlatComboUI());
        combo.setBorder(new RoundedBorder(10, new Insets(5, 10, 5, 4)));
        combo.setBackground(Color.WHITE);
        combo.setForeground(Theme.TEXT);
        combo.setFont(Theme.font(Font.PLAIN, 14));
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        combo.setMaximumRowCount(8);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, false);
                setBorder(BorderFactory.createEmptyBorder(5, index < 0 ? 2 : 10, 5, 10));
                setFont(Theme.font(Font.PLAIN, 14));
                if (value instanceof Member) {
                    Member m = (Member) value;
                    setText(m.getName());
                    setIcon(new Icons.AvatarIcon(m.getName(), 22));
                    setIconTextGap(10);
                } else {
                    setText(index < 0 ? "ยังไม่มีสมาชิก" : "");
                    setForeground(Theme.PLACEHOLDER);
                    setIcon(null);
                }
                if (index < 0) {
                    setBackground(Color.WHITE);
                    if (value instanceof Member) setForeground(Theme.TEXT);
                } else {
                    setBackground(isSelected ? Theme.PRIMARY_SOFT : Color.WHITE);
                    setForeground(Theme.TEXT);
                }
                return this;
            }
        });
    }

    // # 6. หน้าตา ComboBox แบบแบน + ลูกศร chevron
    /** UI ของ ComboBox แบบแบน มีลูกศรแบบ chevron */
    private static class FlatComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = Theme.aa(g);
                    g2.setColor(Theme.MUTED);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    Path2D.Double chevron = new Path2D.Double();
                    chevron.moveTo(cx - 5, cy - 2);
                    chevron.lineTo(cx, cy + 3);
                    chevron.lineTo(cx + 5, cy - 2);
                    g2.draw(chevron);
                    g2.dispose();
                }
            };
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox);
            popup.setBorder(BorderFactory.createLineBorder(Theme.BORDER_STRONG));
            return popup;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(Color.WHITE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false);
        }
    }

    // ===================== Table =====================

    // # 7. ตกแต่ง JTable ทั้งโปรแกรม (ความสูงแถว, สี, Renderer)
    public static void styleTable(JTable table) {
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(Theme.ROW_SELECTED);
        table.setSelectionForeground(Theme.TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(Theme.font(Font.PLAIN, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new TableRenderers.HeaderRenderer());
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBackground(Theme.SURFACE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        table.setDefaultRenderer(Object.class, new TableRenderers.TextRenderer());
        table.setDefaultRenderer(String.class, new TableRenderers.TextRenderer());
        table.setDefaultRenderer(Double.class, new TableRenderers.MoneyRenderer());
        table.setDefaultRenderer(Member.class, new TableRenderers.MemberRenderer());
        table.setDefaultRenderer(ExpenseCategory.class, new TableRenderers.CategoryRenderer());
    }

    // ===================== Dialog =====================

    // # 8. กล่องข้อความ JOptionPane: เตือน / แจ้ง / ยืนยัน
    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "ข้อมูลไม่ถูกต้อง", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Trip Split", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        Object[] options = {"ยืนยัน", "ยกเลิก"};
        int result = JOptionPane.showOptionDialog(parent, message, "ยืนยันการทำรายการ",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        return result == 0;
    }

    // # 9. ตัวช่วยจัดความสูงสำหรับ BoxLayout
    /** กำหนดให้ component กว้างเต็มแต่สูงตามค่า preferred (สำหรับ BoxLayout) */
    public static <T extends JComponent> T fixHeight(T c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
