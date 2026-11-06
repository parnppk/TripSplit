// ############################################################################
// # ไฟล์     : TableRenderers.java
// # ส่วน     : UI – หน้าตาเซลล์ตาราง
// # หน้าที่   : กำหนดว่าแต่ละช่องในตารางแสดงผลอย่างไร (สี ไอคอน ตัวหนา ป้าย)
// # จุดสำคัญ : Inheritance (renderer ลูกสืบทอดจาก TextRenderer)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.ExpenseCategory;
import tripsplit.model.Member;
import tripsplit.model.Money;
import tripsplit.ui.components.Icons;
import tripsplit.ui.components.PillLabel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;

/** ตัวแสดงผลเซลล์ในตาราง (Renderer) แบบต่าง ๆ */
public final class TableRenderers {

    private TableRenderers() {
    }

    // # 1. เส้นคั่นแถว + ระยะขอบเซลล์ (ใช้ร่วมกัน)
    private static javax.swing.border.Border cellBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER),
                BorderFactory.createEmptyBorder(0, 14, 0, 14));
    }

    // # 2. หัวตาราง
    /** หัวตาราง */
    public static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(Theme.font(Font.BOLD, 13));
            setForeground(Theme.MUTED);
            setBackground(Theme.SURFACE);
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14)));
            Class<?> type = table.getColumnClass(column);
            setHorizontalAlignment(type == Double.class ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return this;
        }
    }

    // # 3. เซลล์ข้อความพื้นฐาน — เป็นคลาสแม่ของ renderer ด้านล่าง (Inheritance)
    /** เซลล์ข้อความทั่วไป (เป็นคลาสแม่ของ renderer อื่น) */
    public static class TextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setBorder(cellBorder());
            setBackground(isSelected ? Theme.ROW_SELECTED : Color.WHITE);
            setForeground(Theme.TEXT);
            setFont(Theme.font(Font.PLAIN, 14));
            setIcon(null);
            setIconTextGap(10);
            setHorizontalAlignment(SwingConstants.LEFT);
            setToolTipText(value == null ? null : value.toString());
            return this;
        }
    }

    // # 4. ข้อความสีเทา / ตัวหนา
    /** ข้อความสีเทา (รายละเอียด) */
    public static class MutedRenderer extends TextRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setForeground(Theme.MUTED);
            setFont(Theme.font(Font.PLAIN, 13));
            return this;
        }
    }

    /** ตัวหนา (ชื่อรายการ) */
    public static class BoldRenderer extends TextRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(Theme.font(Font.BOLD, 14));
            return this;
        }
    }

    // # 5. จำนวนเงิน ชิดขวา รูปแบบ 1,234.00
    /** จำนวนเงิน ชิดขวา */
    public static class MoneyRenderer extends TextRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setFont(Theme.font(Theme.SEMIBOLD, 14));
            if (value instanceof Double) {
                setText(Money.format((Double) value));
            }
            return this;
        }
    }

    // # 6. หมวดหมู่: ไอคอน + ชื่อหมวด
    /** หมวดหมู่ (ไอคอน + ชื่อ) */
    public static class CategoryRenderer extends TextRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof ExpenseCategory) {
                ExpenseCategory c = (ExpenseCategory) value;
                setIcon(new Icons.CategoryIcon(c, 28, false));
                setText(c.getDisplayName());
                setForeground(Theme.TEXT_SOFT);
            }
            return this;
        }
    }

    // # 7. สมาชิก: avatar + ชื่อ
    /** สมาชิก (avatar + ชื่อ) */
    public static class MemberRenderer extends TextRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Member) {
                Member m = (Member) value;
                setIcon(new Icons.AvatarIcon(m.getName(), 26));
                setText(m.getName());
                setFont(Theme.font(Font.BOLD, 14));
            }
            return this;
        }
    }

    // # 8. ป้ายสถานะ: ได้คืน (เขียว) / จ่ายเพิ่ม (แดง) / เคลียร์แล้ว (เทา)
    /** สถานะยอดสุทธิ: ได้เงินคืน / ต้องจ่ายเพิ่ม / เคลียร์แล้ว */
    public static class BalanceStatusRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final PillLabel pill = new PillLabel("", Theme.SUCCESS_SOFT, Theme.SUCCESS);

        public BalanceStatusRenderer() {
            pill.setFont(Theme.font(Theme.SEMIBOLD, 13));
            java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
            gc.weightx = 1;
            gc.anchor = java.awt.GridBagConstraints.EAST;
            panel.add(pill, gc);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            double balance = value instanceof Double ? (Double) value : 0;
            if (balance > 0.005) {
                pill.setText("ได้คืน +" + Money.format(balance));
                pill.setColors(Theme.SUCCESS_SOFT, Theme.SUCCESS);
            } else if (balance < -0.005) {
                pill.setText("จ่ายเพิ่ม " + Money.format(-balance));
                pill.setColors(Theme.DANGER_SOFT, Theme.DANGER);
            } else {
                pill.setText("เคลียร์แล้ว");
                pill.setColors(Theme.DIVIDER, Theme.MUTED);
            }
            panel.setBackground(isSelected ? Theme.ROW_SELECTED : Color.WHITE);
            panel.setBorder(cellBorder());
            return panel;
        }
    }

    // # 9. ข้อความว่างกลางพื้นที่
    /** ข้อความว่างเปล่าในตาราง */
    public static JLabel emptyLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(Theme.MUTED);
        return l;
    }
}
