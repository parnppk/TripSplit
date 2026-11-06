// ############################################################################
// # ไฟล์     : DatePickerField.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : ช่องเลือกวันที่ คลิกแล้วมีปฏิทินรายเดือน (ปี พ.ศ.)
// # จุดสำคัญ : Inner class (CalendarPanel, DayCell)
// ############################################################################
package tripsplit.ui.components;

import tripsplit.ui.Theme;
import tripsplit.ui.ThaiDates;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/** ช่องเลือกวันที่ (แสดงปี พ.ศ.) คลิกแล้วมีปฏิทินให้เลือก */
public class DatePickerField extends JPanel {

    // # 1. ข้อมูล: วันที่ที่เลือก, วันแรกที่เลือกได้, listener
    private LocalDate value;
    private LocalDate minDate;
    private final JLabel text = new JLabel();
    private final List<Runnable> listeners = new ArrayList<>();

    // # 2. Constructor — ไอคอนปฏิทิน + ข้อความวันที่ คลิกเพื่อเปิดปฏิทิน
    public DatePickerField(LocalDate initial) {
        super(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(new RoundedBorder(10, new Insets(9, 12, 9, 12)));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(new JLabel(new Icons.LineIcon(Icons.LineIcon.Type.CALENDAR, 16, Theme.PRIMARY)), BorderLayout.WEST);
        text.setFont(Theme.font(Font.PLAIN, 14));
        text.setForeground(Theme.TEXT);
        add(text, BorderLayout.CENTER);
        add(new JLabel(new Icons.LineIcon(Icons.LineIcon.Type.CHEVRON_RIGHT, 12, Theme.MUTED)), BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openCalendar();
            }
        });
        setValue(initial);
    }

    // # 3. อ่าน / ตั้งค่าวันที่ (แจ้ง listener ทุกครั้ง)
    public LocalDate getValue() {
        return value;
    }

    public void setValue(LocalDate date) {
        value = date;
        text.setText(ThaiDates.format(date));
        listeners.forEach(Runnable::run);
    }

    // # 4. จำกัดวันแรกที่เลือกได้ + เพิ่ม listener
    /** วันแรกสุดที่เลือกได้ (null = ไม่จำกัด) */
    public void setMinDate(LocalDate min) {
        this.minDate = min;
    }

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    // # 5. เปิดปฏิทินแบบ popup ใต้ช่อง
    private void openCalendar() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(Theme.BORDER_STRONG));
        popup.setBackground(Color.WHITE);
        CalendarPanel calendar = new CalendarPanel(value, minDate, picked -> {
            setValue(picked);
            popup.setVisible(false);
        });
        popup.add(calendar);
        popup.show(this, 0, getHeight() + 4);
    }

    // # 6. ปฏิทินรายเดือน: ‹ เดือน ปี › + ตารางวัน 7 × 6
    // =====================================================================
    // ปฏิทินรายเดือน
    // =====================================================================
    private static class CalendarPanel extends JPanel {
        private YearMonth month;
        private final LocalDate selected;
        private final LocalDate min;
        private final java.util.function.Consumer<LocalDate> onPick;
        private final JLabel header = new JLabel("", SwingConstants.CENTER);
        private final JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));

        CalendarPanel(LocalDate selected, LocalDate min, java.util.function.Consumer<LocalDate> onPick) {
            super(new BorderLayout(0, 8));
            this.selected = selected;
            this.min = min;
            this.onPick = onPick;
            this.month = YearMonth.from(selected);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            ModernButton prev = new ModernButton("", ModernButton.Style.GHOST).withPadding(6, 8);
            prev.setIcon(new Icons.LineIcon(Icons.LineIcon.Type.CHEVRON_LEFT, 14));
            ModernButton next = new ModernButton("", ModernButton.Style.GHOST).withPadding(6, 8);
            next.setIcon(new Icons.LineIcon(Icons.LineIcon.Type.CHEVRON_RIGHT, 14));
            prev.addActionListener(e -> {
                month = month.minusMonths(1);
                rebuild();
            });
            next.addActionListener(e -> {
                month = month.plusMonths(1);
                rebuild();
            });
            header.setFont(Theme.font(Theme.HEADING, 15));
            header.setForeground(Theme.TEXT);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(prev, BorderLayout.WEST);
            top.add(header, BorderLayout.CENTER);
            top.add(next, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            grid.setOpaque(false);
            add(grid, BorderLayout.CENTER);
            rebuild();
        }

        // # 7. วาดวันในเดือนใหม่ (เริ่มสัปดาห์ที่วันจันทร์)
        private void rebuild() {
            header.setText(ThaiDates.MONTHS[month.getMonthValue() - 1] + " " + (month.getYear() + 543));
            grid.removeAll();
            for (String d : ThaiDates.WEEKDAYS) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(Theme.font(Font.BOLD, 12));
                l.setForeground(Theme.MUTED);
                l.setPreferredSize(new Dimension(36, 26));
                grid.add(l);
            }
            LocalDate first = month.atDay(1);
            int offset = first.getDayOfWeek().getValue() - 1; // จันทร์ = 0
            LocalDate start = first.minusDays(offset);
            for (int i = 0; i < 42; i++) {
                LocalDate day = start.plusDays(i);
                grid.add(new DayCell(day, YearMonth.from(day).equals(month)));
            }
            grid.revalidate();
            grid.repaint();
        }

        // # 8. ช่องวันที่ 1 ช่อง (วันนี้ = มีกรอบ, วันที่เลือก = พื้นสีเขียว)
        private class DayCell extends JComponent {
            private final LocalDate day;
            private final boolean inMonth;
            private final boolean enabled;
            private boolean hover;

            DayCell(LocalDate day, boolean inMonth) {
                this.day = day;
                this.inMonth = inMonth;
                this.enabled = min == null || !day.isBefore(min);
                setPreferredSize(new Dimension(36, 32));
                if (enabled) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            onPick.accept(day);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            hover = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            hover = false;
                            repaint();
                        }
                    });
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Theme.aa(g);
                int w = getWidth();
                int h = getHeight();
                boolean isSelected = day.equals(selected);
                boolean isToday = day.equals(LocalDate.now());
                if (isSelected) {
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(2, 1, w - 4, h - 2, 12, 12);
                } else if (hover) {
                    g2.setColor(Theme.PRIMARY_SOFT);
                    g2.fillRoundRect(2, 1, w - 4, h - 2, 12, 12);
                } else if (isToday) {
                    g2.setColor(Theme.PRIMARY);
                    g2.drawRoundRect(2, 1, w - 5, h - 3, 12, 12);
                }
                g2.setFont(Theme.font(isSelected ? Font.BOLD : Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(day.getDayOfMonth());
                g2.setColor(isSelected ? Color.WHITE : !enabled ? Theme.BORDER_STRONG
                        : inMonth ? Theme.TEXT : Theme.PLACEHOLDER);
                g2.drawString(s, (w - fm.stringWidth(s)) / 2, h / 2 + (fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        }
    }
}
