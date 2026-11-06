// ############################################################################
// # ไฟล์     : HeroBanner.java
// # ส่วน     : UI – Component ที่ออกแบบเอง
// # หน้าที่   : แบนเนอร์ทริป: ภาพวิวภูเขา + ชื่อทริป วันที่ จำนวนคน รูปสมาชิก
// # จุดสำคัญ : วาดภาพด้วยโค้ด, รองรับรูป hero.jpg
// ############################################################################
package tripsplit.ui.components;

import tripsplit.model.Member;
import tripsplit.model.Trip;
import tripsplit.ui.Theme;
import tripsplit.ui.ThaiDates;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * แบนเนอร์ทริป: ภาพวิวภูเขาในหมอก + ชื่อทริป วันที่ จำนวนคน และรูปสมาชิก
 * <p>
 * ภาพพื้นหลังวาดด้วยโค้ดทั้งหมด (ไม่ต้องใช้ไฟล์รูป) ถ้าต้องการใช้รูปถ่ายจริง
 * ให้วางไฟล์ชื่อ hero.jpg ไว้ที่ src/tripsplit/ui/images/ โปรแกรมจะใช้รูปนั้นแทนอัตโนมัติ
 */
public class HeroBanner extends JComponent {

    // # 1. ค่าคงที่: ความสูงแบนเนอร์, ระยะขอบ, ขนาด avatar
    public static final int BANNER_HEIGHT = 184;
    private static final int PAD_X = 32;
    private static final int AVATAR = 32;
    private static final int MAX_AVATARS = 6;

    // # 2. อ้างอิง Model + callback (แก้ไขทริป / เพิ่มสมาชิก)
    private final Trip trip;
    private final Runnable onEditTrip;
    private final Runnable onAddMember;

    // # 3. ภาพพื้นหลัง (รูป hero.jpg ถ้ามี) + cache กันวาดซ้ำ
    private final Image photo = loadPhoto();
    private BufferedImage cache;

    // # 4. พื้นที่คลิกได้: ชื่อทริป, ปุ่มดินสอ, รูปสมาชิก, ปุ่ม +
    private final Rectangle titleRect = new Rectangle();
    private final Rectangle editRect = new Rectangle();
    private final Rectangle avatarsRect = new Rectangle();
    private final Rectangle plusRect = new Rectangle();
    private boolean hoverEdit;
    private boolean hoverPlus;

    // # 5. Constructor — จัดการเมาส์: คลิก / hover / เปลี่ยนรูปเคอร์เซอร์
    public HeroBanner(Trip trip, Runnable onEditTrip, Runnable onAddMember) {
        this.trip = trip;
        this.onEditTrip = onEditTrip;
        this.onAddMember = onAddMember;
        setToolTipText("");

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (plusRect.contains(e.getPoint())) {
                    onAddMember.run();
                } else if (editRect.contains(e.getPoint()) || titleRect.contains(e.getPoint())) {
                    onEditTrip.run();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean edit = editRect.contains(e.getPoint()) || titleRect.contains(e.getPoint());
                boolean plus = plusRect.contains(e.getPoint());
                if (edit != hoverEdit || plus != hoverPlus) {
                    hoverEdit = edit;
                    hoverPlus = plus;
                    repaint();
                }
                setCursor(Cursor.getPredefinedCursor(edit || plus ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverEdit = false;
                hoverPlus = false;
                repaint();
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, BANNER_HEIGHT);
    }

    // # 6. Tooltip ตามตำแหน่งเมาส์
    @Override
    public String getToolTipText(MouseEvent e) {
        if (plusRect.contains(e.getPoint())) return "เพิ่มสมาชิกในทริป";
        if (editRect.contains(e.getPoint()) || titleRect.contains(e.getPoint())) return "แก้ไขชื่อและวันที่ของทริป";
        if (avatarsRect.contains(e.getPoint()) && trip.getMemberCount() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Member m : trip.getMembers()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(m.getName());
            }
            return sb.toString();
        }
        return null;
    }

    // # 7. วาดแบนเนอร์: พื้นหลัง (จาก cache) แล้ววาดเนื้อหาทับ
    // =====================================================================
    // วาด
    // =====================================================================
    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        if (cache == null || cache.getWidth() != w || cache.getHeight() != h) {
            cache = renderBackground(w, h);
        }
        g.drawImage(cache, 0, 0, null);

        Graphics2D g2 = Theme.aa(g);
        paintContent(g2, w, h);
        g2.dispose();
    }

    // # 8. วาดป้าย "ทริปปัจจุบัน", ชื่อทริป, ปุ่มแก้ไข, วันที่, จำนวนคน
    private void paintContent(Graphics2D g2, int w, int h) {
        int x = PAD_X;

        // ---------- ป้าย "ทริปปัจจุบัน" ----------
        Font badgeFont = Theme.font(Font.BOLD, 12);
        g2.setFont(badgeFont);
        FontMetrics fm = g2.getFontMetrics();
        String badge = "ทริปปัจจุบัน";
        int bw = fm.stringWidth(badge) + 22;
        int by = 28;
        g2.setColor(Theme.alpha(Theme.PRIMARY, 235));
        g2.fillRoundRect(x, by, bw, 24, 24, 24);
        g2.setColor(Color.WHITE);
        g2.drawString(badge, x + 11, by + 12 + (fm.getAscent() - fm.getDescent()) / 2);

        // ---------- ชื่อทริป ----------
        Font titleFont = Theme.font(Theme.DISPLAY, 38);
        g2.setFont(titleFont);
        fm = g2.getFontMetrics();
        String title = trip.getName();
        int titleW = fm.stringWidth(title);
        int baseline = by + 24 + 10 + fm.getAscent() - 6;
        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawString(title, x + 1, baseline + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(title, x, baseline);
        titleRect.setBounds(x, baseline - fm.getAscent() + 6, titleW, fm.getAscent());

        // ปุ่มแก้ไข (รูปดินสอ) ต่อท้ายชื่อทริป
        int ex = x + titleW + 14;
        int ey = baseline - fm.getAscent() / 2 - 12 + 4;
        editRect.setBounds(ex, ey, 28, 28);
        g2.setColor(new Color(255, 255, 255, hoverEdit ? 80 : 36));
        g2.fill(new Ellipse2D.Double(ex, ey, 28, 28));
        new Icons.LineIcon(Icons.LineIcon.Type.EDIT, 14, Color.WHITE).paintIcon(this, g2, ex + 7, ey + 7);

        // เส้นประ + เครื่องบินกระดาษ
        paintFlightPath(g2, ex + 44, ey + 14);

        // ---------- วันที่ / จำนวนวัน / จำนวนคน + รูปสมาชิก (บรรทัดเดียวกัน) ----------
        Font subFont = Theme.font(Font.PLAIN, 15);
        g2.setFont(subFont);
        fm = g2.getFontMetrics();
        int rowCenter = baseline + 34;
        int subY = rowCenter + (fm.getAscent() - fm.getDescent()) / 2;
        String sub = ThaiDates.formatRange(trip.getStartDate(), trip.getEndDate())
                + "   •   " + ThaiDates.duration(trip.getDayCount())
                + "   •   " + trip.getMemberCount() + " คน";
        new Icons.LineIcon(Icons.LineIcon.Type.CALENDAR, 16, new Color(255, 255, 255, 220))
                .paintIcon(this, g2, x, rowCenter - 9);
        g2.setColor(new Color(255, 255, 255, 235));
        g2.drawString(sub, x + 24, subY);

        paintAvatars(g2, x + 24 + fm.stringWidth(sub) + 22, rowCenter - AVATAR / 2);
    }

    // # 9. รูปสมาชิกวงกลมซ้อนกัน (สูงสุด 6 คน) + ปุ่ม +
    private void paintAvatars(Graphics2D g2, int x, int y) {
        List<Member> members = trip.getMembers();
        int shown = Math.min(MAX_AVATARS, members.size());
        int step = AVATAR - 8;
        int cx = x;
        for (int i = 0; i < shown; i++) {
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(cx - 2, y - 2, AVATAR + 4, AVATAR + 4));
            new Icons.AvatarIcon(members.get(i).getName(), AVATAR).paintIcon(this, g2, cx, y);
            cx += step;
        }
        if (members.size() > shown) {
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(cx - 2, y - 2, AVATAR + 4, AVATAR + 4));
            g2.setColor(Theme.DIVIDER);
            g2.fill(new Ellipse2D.Double(cx, y, AVATAR, AVATAR));
            g2.setColor(Theme.TEXT_SOFT);
            g2.setFont(Theme.font(Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String more = "+" + (members.size() - shown);
            g2.drawString(more, cx + (AVATAR - fm.stringWidth(more)) / 2,
                    y + AVATAR / 2 + (fm.getAscent() - fm.getDescent()) / 2);
            cx += step;
        }
        avatarsRect.setBounds(x, y, Math.max(0, cx - x + 8), AVATAR);

        // ปุ่ม + เพิ่มสมาชิก
        int px = shown == 0 ? x : cx + 14;
        plusRect.setBounds(px, y, AVATAR, AVATAR);
        g2.setColor(new Color(255, 255, 255, hoverPlus ? 90 : 40));
        g2.fill(new Ellipse2D.Double(px, y, AVATAR, AVATAR));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10f, new float[]{3f, 3f}, 0f));
        g2.draw(new Ellipse2D.Double(px + 0.7, y + 0.7, AVATAR - 1.4, AVATAR - 1.4));
        new Icons.LineIcon(Icons.LineIcon.Type.PLUS, 14, Color.WHITE).paintIcon(this, g2, px + 9, y + 9);

        if (shown == 0) {
            g2.setFont(Theme.font(Font.PLAIN, 13));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawString("เพิ่มเพื่อนร่วมทริป", px + AVATAR + 10, y + AVATAR / 2 + (fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    // # 10. เส้นประโค้ง + เครื่องบินกระดาษ
    /** เส้นประโค้งพร้อมเครื่องบินกระดาษที่ปลายเส้น */
    private void paintFlightPath(Graphics2D g2, int x0, int y0) {
        double x1 = x0 + 120;
        double y1 = y0 - 26;
        CubicCurve2D.Double curve = new CubicCurve2D.Double(
                x0, y0 + 12, x0 + 40, y0 + 34, x0 + 70, y0 - 20, x1 - 14, y1 + 6);
        g2.setColor(new Color(255, 255, 255, 190));
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10f, new float[]{5f, 6f}, 0f));
        g2.draw(curve);

        AffineTransform old = g2.getTransform();
        g2.translate(x1 - 12, y1 - 6);
        g2.rotate(Math.toRadians(-8));
        TopNavBar.paintPlane(g2, 0, -10, 24, new Color(0xD8F3EF), Color.WHITE);
        g2.setTransform(old);
    }

    // # 11. โหลดรูป images/hero.jpg (ถ้าไม่มีใช้ภาพวาด)
    // =====================================================================
    // ภาพพื้นหลัง
    // =====================================================================
    private static Image loadPhoto() {
        for (String name : new String[]{"images/hero.jpg", "images/hero.png"}) {
            try {
                URL url = Theme.class.getResource(name);
                if (url != null) {
                    return ImageIO.read(url);
                }
            } catch (Exception ignored) {
                // ใช้ภาพวาดแทน
            }
        }
        return null;
    }

    // # 12. สร้างภาพพื้นหลัง + ไล่สีเข้มด้านซ้ายให้อ่านตัวหนังสือง่าย
    private BufferedImage renderBackground(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        if (photo != null) {
            // ปรับรูปให้เต็มพื้นที่ (cover) โดยไม่บิดสัดส่วน
            double scale = Math.max((double) w / photo.getWidth(null), (double) h / photo.getHeight(null));
            int pw = (int) Math.ceil(photo.getWidth(null) * scale);
            int ph = (int) Math.ceil(photo.getHeight(null) * scale);
            g2.drawImage(photo, (w - pw) / 2, (h - ph) / 2, pw, ph, null);
        } else {
            paintLandscape(g2, w, h);
        }

        // ไล่สีเข้มด้านซ้าย ให้ตัวหนังสืออ่านง่าย
        g2.setPaint(new LinearGradientPaint(0, 0, (float) (w * 0.66), 0,
                new float[]{0f, 0.45f, 1f},
                new Color[]{new Color(8, 40, 48, 225), new Color(8, 40, 48, 150), new Color(8, 40, 48, 0)}));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, h * 0.55f, new Color(0, 0, 0, 0), 0, h, new Color(0, 20, 24, 70)));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return img;
    }

    // # 13. วาดวิว: ท้องฟ้า → แสงอาทิตย์ → ภูเขา 5 ชั้น → ต้นสน → หมอก → วัด
    /** วาดวิวภูเขาซ้อนชั้นในสายหมอกยามเช้า พร้อมเจดีย์และต้นสน */
    private static void paintLandscape(Graphics2D g2, int w, int h) {
        // ---------- ท้องฟ้า ----------
        g2.setPaint(new LinearGradientPaint(0, 0, 0, h * 0.75f,
                new float[]{0f, 0.55f, 1f},
                new Color[]{new Color(0x6E9EAA), new Color(0xC9DDD9), new Color(0xF4E7CF)}));
        g2.fillRect(0, 0, w, h);

        // ---------- แสงพระอาทิตย์ ----------
        float sunX = w * 0.70f;
        float sunY = h * 0.42f;
        float sunR = Math.max(h * 0.9f, 1f);
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(sunX, sunY), sunR,
                new float[]{0f, 0.18f, 1f},
                new Color[]{new Color(255, 248, 225, 235), new Color(255, 236, 200, 120), new Color(255, 236, 200, 0)}));
        g2.fillRect(0, 0, w, h);

        // ---------- ภูเขา 5 ชั้น (ไกล → ใกล้) ----------
        Color[] colors = {new Color(0xAEC6C8), new Color(0x8EAEB1), new Color(0x668A8D),
                new Color(0x3F6363), new Color(0x1E3B39)};
        double[] base = {0.46, 0.56, 0.66, 0.78, 0.92};
        double[] amp = {0.14, 0.13, 0.12, 0.10, 0.09};
        Random rnd = new Random(20261016L);

        for (int layer = 0; layer < colors.length; layer++) {
            double p1 = rnd.nextDouble() * 6.28, p2 = rnd.nextDouble() * 6.28, p3 = rnd.nextDouble() * 6.28;
            double f1 = 2.2 + layer * 0.6, f2 = 5.0 + layer * 1.3, f3 = 13.0 + layer * 2.0;
            double[] ridge = new double[w + 1];
            for (int x = 0; x <= w; x++) {
                double t = (double) x / w;
                double n = 0.55 * Math.sin(t * f1 * Math.PI + p1)
                        + 0.30 * Math.sin(t * f2 * Math.PI + p2)
                        + 0.15 * Math.sin(t * f3 * Math.PI + p3);
                double y = h * base[layer] - h * amp[layer] * n;
                // ภูเขาด้านขวาสูงขึ้น (ด้านหลังวัด)
                y -= h * 0.10 * Math.exp(-Math.pow((t - 0.80) / 0.16, 2)) * (layer < 3 ? 1 : 0.4);
                if (layer == 4) {
                    // เนินที่ตั้งเจดีย์
                    y -= h * 0.16 * Math.exp(-Math.pow((t - 0.87) / 0.07, 2));
                }
                ridge[x] = y;
            }
            Path2D.Double mountain = new Path2D.Double();
            mountain.moveTo(0, h);
            for (int x = 0; x <= w; x++) {
                mountain.lineTo(x, ridge[x]);
            }
            mountain.lineTo(w, h);
            mountain.closePath();
            Color top = colors[layer];
            Color bottom = Theme.mix(top, new Color(0xE9EFEC), layer < 4 ? 0.35 : 0.08);
            g2.setPaint(new GradientPaint(0, (float) (h * (base[layer] - amp[layer])), top,
                    0, (float) (h * base[layer] + h * 0.15), bottom));
            g2.fill(mountain);

            // ต้นสนบนสันเขาชั้นใกล้
            if (layer >= 3) {
                g2.setColor(Theme.mix(top, Color.BLACK, layer == 4 ? 0.25 : 0.12));
                int x = rnd.nextInt(10);
                while (x < w) {
                    double t = (double) x / w;
                    boolean nearTemple = layer == 4 && t > 0.80 && t < 0.95;
                    if (!nearTemple) {
                        double treeH = (layer == 4 ? 12 : 7) + Math.pow(rnd.nextDouble(), 1.6) * (layer == 4 ? 26 : 10);
                        paintPine(g2, x, ridge[Math.min(w, x)] + 3, treeH);
                        if (rnd.nextDouble() < 0.35) {
                            // กลุ่มต้นไม้ติดกัน ดูเป็นธรรมชาติขึ้น
                            paintPine(g2, x + 5 + rnd.nextInt(4), ridge[Math.min(w, x + 6)] + 3, treeH * 0.7);
                        }
                    }
                    x += (layer == 4 ? 10 : 14) + rnd.nextInt(layer == 4 ? 34 : 40);
                }
            }

            // หมอกในหุบเขา
            if (layer < 4) {
                float my = (float) (h * (base[layer] + 0.02));
                float mh = (float) (h * 0.16);
                g2.setPaint(new LinearGradientPaint(0, my - mh, 0, my + mh,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(255, 255, 255, 0), new Color(250, 252, 250, layer == 3 ? 150 : 120),
                                new Color(255, 255, 255, 0)}));
                g2.fillRect(0, (int) (my - mh), w, (int) (mh * 2));
            }

            if (layer == 4) {
                paintTemple(g2, w * 0.87, ridge[(int) (w * 0.87)] + 4, h, top);
            }
        }
    }

    // # 14. วาดต้นสน 1 ต้น
    private static void paintPine(Graphics2D g2, double x, double groundY, double height) {
        double wdt = height * 0.42;
        Path2D.Double tree = new Path2D.Double();
        for (int i = 0; i < 3; i++) {
            double top = groundY - height + i * height * 0.22;
            double bottom = top + height * 0.45;
            double half = wdt * (0.55 + i * 0.25) / 2;
            tree.moveTo(x, top);
            tree.lineTo(x + half, bottom);
            tree.lineTo(x - half, bottom);
            tree.closePath();
        }
        g2.fill(tree);
        g2.fill(new java.awt.geom.Rectangle2D.Double(x - 1, groundY - height * 0.2, 2, height * 0.2));
    }

    // # 15. วาดเจดีย์ทรงล้านนา + เจดีย์องค์เล็ก + หลังคาวิหาร
    /** เจดีย์ทรงล้านนา + หลังคาวิหาร (ภาพเงา) */
    private static void paintTemple(Graphics2D g2, double cx, double groundY, int h, Color ground) {
        Color dark = Theme.mix(ground, Color.BLACK, 0.30);
        g2.setColor(dark);
        double s = h * 0.62; // ความสูงเจดีย์

        // ฐานซ้อนชั้น
        double baseW = s * 0.42;
        for (int i = 0; i < 3; i++) {
            double bw = baseW * (1 - i * 0.18);
            double bh = s * 0.07;
            g2.fill(new java.awt.geom.Rectangle2D.Double(cx - bw / 2, groundY - bh * (i + 1), bw, bh + 1));
        }
        double y = groundY - s * 0.21;
        // องค์ระฆัง
        Path2D.Double bell = new Path2D.Double();
        bell.moveTo(cx - s * 0.13, y);
        bell.curveTo(cx - s * 0.13, y - s * 0.16, cx - s * 0.05, y - s * 0.22, cx, y - s * 0.24);
        bell.curveTo(cx + s * 0.05, y - s * 0.22, cx + s * 0.13, y - s * 0.16, cx + s * 0.13, y);
        bell.closePath();
        g2.fill(bell);
        y -= s * 0.24;
        // บัลลังก์ + ปล้องไฉน
        g2.fill(new java.awt.geom.Rectangle2D.Double(cx - s * 0.045, y - s * 0.05, s * 0.09, s * 0.05));
        y -= s * 0.05;
        Path2D.Double spire = new Path2D.Double();
        spire.moveTo(cx - s * 0.035, y);
        spire.lineTo(cx, y - s * 0.30);
        spire.lineTo(cx + s * 0.035, y);
        spire.closePath();
        g2.fill(spire);
        // ยอดฉัตรสีทอง
        g2.setColor(new Color(0xE9C46A));
        g2.fill(new Ellipse2D.Double(cx - 2, y - s * 0.30 - 2, 4, 4));

        // เจดีย์องค์เล็กด้านซ้าย
        g2.setColor(dark);
        double sx = cx - s * 0.36;
        double small = s * 0.45;
        g2.fill(new java.awt.geom.Rectangle2D.Double(sx - small * 0.16, groundY - small * 0.12, small * 0.32, small * 0.12 + 1));
        Path2D.Double small1 = new Path2D.Double();
        small1.moveTo(sx - small * 0.11, groundY - small * 0.12);
        small1.curveTo(sx - small * 0.11, groundY - small * 0.30, sx, groundY - small * 0.36, sx, groundY - small * 0.36);
        small1.curveTo(sx, groundY - small * 0.36, sx + small * 0.11, groundY - small * 0.30, sx + small * 0.11, groundY - small * 0.12);
        small1.closePath();
        g2.fill(small1);
        Path2D.Double small2 = new Path2D.Double();
        small2.moveTo(sx - small * 0.03, groundY - small * 0.34);
        small2.lineTo(sx, groundY - small * 0.75);
        small2.lineTo(sx + small * 0.03, groundY - small * 0.34);
        small2.closePath();
        g2.fill(small2);

        // หลังคาวิหารซ้อนชั้นด้านขวา
        double rx = cx + s * 0.30;
        double roofW = s * 0.42;
        double ry = groundY - s * 0.10;
        g2.fill(new java.awt.geom.Rectangle2D.Double(rx - roofW * 0.38, ry, roofW * 0.76, s * 0.10 + 1));
        for (int i = 0; i < 3; i++) {
            double rw = roofW * (1 - i * 0.22);
            double rh = s * 0.11;
            double top = ry - rh * (i + 1) + i * rh * 0.25;
            Path2D.Double roof = new Path2D.Double();
            roof.moveTo(rx - rw / 2, top + rh);
            roof.lineTo(rx - rw * 0.12, top);
            roof.lineTo(rx + rw * 0.12, top);
            roof.lineTo(rx + rw / 2, top + rh);
            roof.closePath();
            g2.fill(roof);
        }
    }
}
