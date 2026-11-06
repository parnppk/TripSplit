// ############################################################################
// # ไฟล์     : Theme.java
// # ส่วน     : UI – Design System
// # หน้าที่   : รวมสี ฟอนต์ และค่าตั้งต้นหน้าตาทั้งโปรแกรมไว้ที่เดียว
// # จุดสำคัญ : แก้สี/ฟอนต์ที่ไฟล์นี้ไฟล์เดียว ทั้งโปรแกรมเปลี่ยนตาม
// ############################################################################
package tripsplit.ui;

import tripsplit.model.ExpenseCategory;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** สี ฟอนต์ และค่าตั้งต้นของหน้าตาโปรแกรมทั้งหมด (Design System) */
public final class Theme {

    // # 1. ชุดสี (Design System) — แก้ที่นี่ที่เดียว สีเปลี่ยนทั้งโปรแกรม
    // ---------- พื้นหลัง / เส้นขอบ ----------
    public static final Color BG = new Color(0xF3F6FA);
    public static final Color CARD = Color.WHITE;
    public static final Color SURFACE = new Color(0xF8FAFC);
    public static final Color BORDER = new Color(0xE2E8F0);
    public static final Color BORDER_STRONG = new Color(0xCBD5E1);
    public static final Color DIVIDER = new Color(0xEEF2F6);

    // ---------- ตัวอักษร ----------
    public static final Color TEXT = new Color(0x0F172A);
    public static final Color TEXT_SOFT = new Color(0x334155);
    public static final Color MUTED = new Color(0x64748B);
    public static final Color PLACEHOLDER = new Color(0x94A3B8);

    // ---------- สีหลัก ----------
    public static final Color PRIMARY = new Color(0x0D9488);
    public static final Color PRIMARY_DARK = new Color(0x0F766E);
    public static final Color PRIMARY_SOFT = new Color(0xE3F5F2);
    public static final Color HEADER_START = new Color(0x0B3B4A);
    public static final Color HEADER_END = new Color(0x0E8A80);

    public static final Color ACCENT = new Color(0xEA580C);
    public static final Color ACCENT_SOFT = new Color(0xFFEDD5);
    public static final Color SUCCESS = new Color(0x15803D);
    public static final Color SUCCESS_SOFT = new Color(0xDCFCE7);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color DANGER_SOFT = new Color(0xFEE2E2);
    public static final Color INFO = new Color(0x2563EB);
    public static final Color VIOLET = new Color(0x7C3AED);

    public static final Color ROW_SELECTED = new Color(0xE6F6F3);

    private static final Color[] AVATAR_COLORS = {
            new Color(0x0D9488), new Color(0x2563EB), new Color(0x7C3AED), new Color(0xDB2777),
            new Color(0xEA580C), new Color(0xCA8A04), new Color(0x0891B2), new Color(0x4F46E5),
            new Color(0x059669), new Color(0xE11D48)
    };

    // # 2. รูปแบบตัวอักษรเพิ่มเติม (ใช้คู่กับ Theme.font)
    // ---------- รูปแบบตัวอักษร (ใช้แทน Font.PLAIN / Font.BOLD ได้) ----------
    /** Prompt SemiBold – ตัวหนาพิเศษสำหรับตัวเลข/ปุ่มสำคัญ */
    public static final int SEMIBOLD = 0x100;
    /** Kanit Medium – หัวข้อการ์ด */
    public static final int HEADING = 0x200;
    /** Kanit SemiBold – หัวข้อใหญ่ / ตัวเลขสรุป */
    public static final int DISPLAY = 0x400;

    // # 3. ฟอนต์ที่ฝังในโปรแกรม: Prompt (เนื้อหา) / Kanit (หัวข้อ)
    // ฟอนต์ที่ฝังมากับโปรแกรม (โฟลเดอร์ tripsplit/ui/fonts) – แสดงผลเหมือนกันทุกเครื่อง
    private static final Font BODY_REGULAR = loadFont("Prompt-Regular.ttf");
    private static final Font BODY_MEDIUM = loadFont("Prompt-Medium.ttf");
    private static final Font BODY_SEMIBOLD = loadFont("Prompt-SemiBold.ttf");
    private static final Font HEAD_MEDIUM = loadFont("Kanit-Medium.ttf");
    private static final Font HEAD_SEMIBOLD = loadFont("Kanit-SemiBold.ttf");

    private static final String FALLBACK_FAMILY = resolveFontFamily();

    private Theme() {
    }

    // ===================== สี =====================

    // # 4. สีประจำหมวด / สี avatar ของแต่ละคน
    public static Color categoryColor(ExpenseCategory category) {
        switch (category) {
            case FOOD:      return new Color(0xD97706);
            case TRANSPORT: return new Color(0x2563EB);
            case HOTEL:     return new Color(0x7C3AED);
            case ACTIVITY:  return new Color(0xDB2777);
            default:        return PRIMARY;
        }
    }

    /** สีประจำตัวสมาชิก (คำนวณจากชื่อ จึงได้สีเดิมทุกครั้ง) */
    public static Color avatarColor(String name) {
        int index = Math.floorMod(name.toLowerCase().hashCode(), AVATAR_COLORS.length);
        return AVATAR_COLORS[index];
    }

    // # 5. ตัวช่วยผสมสี: ทำสีอ่อน, ผสมสอง สี, ใส่ความโปร่งใส
    /** ทำสีให้อ่อนลง (ผสมกับสีขาว) ใช้เป็นพื้นหลังของ badge/icon */
    public static Color soft(Color c) {
        return mix(c, Color.WHITE, 0.86);
    }

    public static Color mix(Color a, Color b, double ratioOfB) {
        double r = 1 - ratioOfB;
        return new Color(
                (int) Math.round(a.getRed() * r + b.getRed() * ratioOfB),
                (int) Math.round(a.getGreen() * r + b.getGreen() * ratioOfB),
                (int) Math.round(a.getBlue() * r + b.getBlue() * ratioOfB));
    }

    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    // ===================== ฟอนต์ =====================

    // # 6. อ่านไฟล์ .ttf จากโฟลเดอร์ fonts (ถ้าไม่เจอใช้ฟอนต์ระบบแทน)
    /** โหลดไฟล์ฟอนต์ .ttf จาก resource ภายในโปรแกรม (คืน null ถ้าหาไม่เจอ) */
    private static Font loadFont(String file) {
        try (InputStream in = Theme.class.getResourceAsStream("fonts/" + file)) {
            if (in == null) {
                System.err.println("[Trip Split] ไม่พบฟอนต์ " + file + " ใช้ฟอนต์ของระบบแทน");
                return null;
            }
            Font f = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f;
        } catch (Exception ex) {
            System.err.println("[Trip Split] โหลดฟอนต์ " + file + " ไม่สำเร็จ: " + ex.getMessage());
            return null;
        }
    }

    /** ฟอนต์สำรองของระบบ (กรณีโหลดฟอนต์ที่ฝังไว้ไม่ได้) */
    private static String resolveFontFamily() {
        String[] preferred = {"Leelawadee UI", "Leelawadee", "Noto Sans Thai", "Sarabun",
                "Tahoma", "Thonburi", "Loma", "Garuda"};
        try {
            Set<String> available = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String family : preferred) {
                if (available.contains(family) && new Font(family, Font.PLAIN, 14).canDisplay('ก')) {
                    return family;
                }
            }
        } catch (Exception ignored) {
            // ใช้ค่า default ด้านล่าง
        }
        return Font.DIALOG;
    }

    // # 7. ★ เลือกฟอนต์ตามรูปแบบ — ทุก Component เรียกเมธอดนี้
    /**
     * คืนฟอนต์ตามรูปแบบ
     * <ul>
     *   <li>Font.PLAIN → Prompt Regular (ข้อความทั่วไป)</li>
     *   <li>Font.BOLD  → Prompt Medium (ป้ายกำกับ ปุ่ม ชื่อรายการ)</li>
     *   <li>SEMIBOLD   → Prompt SemiBold (จำนวนเงิน)</li>
     *   <li>HEADING    → Kanit Medium (หัวข้อการ์ด)</li>
     *   <li>DISPLAY    → Kanit SemiBold (หัวข้อใหญ่ ตัวเลขสรุป)</li>
     * </ul>
     */
    public static Font font(int style, float size) {
        Font base;
        int fallbackStyle = Font.BOLD;
        if ((style & DISPLAY) != 0) {
            base = HEAD_SEMIBOLD;
        } else if ((style & HEADING) != 0) {
            base = HEAD_MEDIUM;
        } else if ((style & SEMIBOLD) != 0) {
            base = BODY_SEMIBOLD;
        } else if ((style & Font.BOLD) != 0) {
            base = BODY_MEDIUM;
        } else {
            base = BODY_REGULAR;
            fallbackStyle = Font.PLAIN;
        }
        if (base == null) {
            return new Font(FALLBACK_FAMILY, fallbackStyle, Math.round(size));
        }
        return base.deriveFont(Font.PLAIN, size);
    }

    // ===================== Graphics =====================

    // # 8. เปิด Anti-aliasing ให้เส้นและตัวอักษรคม
    /** คืน Graphics2D ที่เปิด Anti-aliasing แล้ว (ต้อง dispose เองหลังใช้) */
    public static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g2;
    }

    // ===================== ตั้งค่า Look & Feel =====================

    // # 9. ตั้งค่า Look & Feel และฟอนต์/สีเริ่มต้นของ Swing ทั้งระบบ
    public static void applyGlobalDefaults() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ignored) {
            // ใช้ Look & Feel เดิมต่อไป
        }

        FontUIResource base = new FontUIResource(font(Font.PLAIN, 14));
        for (Object key : new ArrayList<>(UIManager.getLookAndFeelDefaults().keySet())) {
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, base);
            }
        }

        UIManager.put("Panel.background", new ColorUIResource(BG));
        UIManager.put("OptionPane.background", new ColorUIResource(Color.WHITE));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(TEXT));
        UIManager.put("OptionPane.messageFont", new FontUIResource(font(Font.PLAIN, 15)));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(font(Font.BOLD, 14)));
        UIManager.put("Button.background", new ColorUIResource(Color.WHITE));
        UIManager.put("Button.focus", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.background", new ColorUIResource(TEXT));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(6, 10, 6, 10));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(PRIMARY_SOFT));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(TEXT));
        UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        UIManager.put("TextField.selectionBackground", new ColorUIResource(new Color(0xBEEBE4)));
        UIManager.put("TextField.selectionForeground", new ColorUIResource(TEXT));
        UIManager.put("TextField.inactiveForeground", new ColorUIResource(MUTED));
        UIManager.put("CheckBox.focus", new ColorUIResource(Color.WHITE));
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(PRIMARY_SOFT));
        UIManager.put("MenuItem.selectionForeground", new ColorUIResource(TEXT));
        UIManager.put("MenuItem.background", new ColorUIResource(Color.WHITE));
        UIManager.put("PopupMenu.background", new ColorUIResource(Color.WHITE));
        UIManager.put("Separator.foreground", new ColorUIResource(DIVIDER));
        UIManager.put("Separator.background", new ColorUIResource(Color.WHITE));
        UIManager.put("PopupMenuSeparator.foreground", new ColorUIResource(DIVIDER));
        UIManager.put("PopupMenuSeparator.background", new ColorUIResource(Color.WHITE));
    }
}
