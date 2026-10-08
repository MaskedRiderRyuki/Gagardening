import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PixelInventory {

    // ---------- สี (จาก CSS) ----------
    static final Color PAGE_BG      = new Color(0x6b7c85);
    static final Color WINDOW_BG    = new Color(0x5c3543);
    static final Color OUTLINE      = new Color(0x38212a);
    static final Color BORDER_DARK  = new Color(0x2b1720);
    static final Color SLOT_BG      = new Color(0x92534a);
    static final Color SLOT_LIGHT   = new Color(0xb46b60);
    static final Color SLOT_SHADOW  = new Color(0x6e3a32);
    static final Color SLOT_SELECTED_BG = new Color(0xa66056);
    static final Color YELLOW       = new Color(0xffeb3b);
    static final Color HOTBAR_BG    = new Color(0x42232e);
    static final Color HOTBAR_SHADOW = new Color(0x221017);
    static final Color CHAR_SHADOW  = new Color(0x703c34);

    /** พาเนลที่วาดกรอบ + เงา inset สไตล์พิกเซล */
    static class BevelPanel extends JPanel {
        Color bg, border, topLeft, bottomRight;
        int borderWidth, bevel;

        BevelPanel(Color bg, Color border, int borderWidth, int bevel,
                   Color topLeft, Color bottomRight) {
            this.bg = bg;
            this.border = border;
            this.borderWidth = borderWidth;
            this.bevel = bevel;
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight(), bw = borderWidth;
            int iw = w - 2 * bw, ih = h - 2 * bw;

            g.setColor(border);
            g.fillRect(0, 0, w, h);
            g.setColor(bg);
            g.fillRect(bw, bw, iw, ih);

            if (topLeft != null) {
                g.setColor(topLeft);
                g.fillRect(bw, bw, iw, bevel);
                g.fillRect(bw, bw, bevel, ih);
            }
            if (bottomRight != null) {
                g.setColor(bottomRight);
                g.fillRect(bw, h - bw - bevel, iw, bevel);
                g.fillRect(w - bw - bevel, bw, bevel, ih);
            }
        }
    }

    /** ช่องเก็บของ 44x44 มี hover / selected */
    static class Slot extends BevelPanel {
        static Slot current;          // ช่องที่ถูกเลือกอยู่
        boolean hover, selected;

        Slot() {
            super(SLOT_BG, BORDER_DARK, 3, 3, SLOT_LIGHT, SLOT_SHADOW);
            setPreferredSize(new Dimension(44, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  refresh(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; refresh(); }
                @Override public void mousePressed(MouseEvent e) { select(); }
            });
            refresh();
        }

        void select() {
            if (current != null && current != this) {
                current.selected = false;
                current.refresh();
            }
            selected = true;
            current = this;
            refresh();
        }

        void refresh() {
            bg = selected ? SLOT_SELECTED_BG : SLOT_BG;
            border = selected ? YELLOW : (hover ? Color.WHITE : BORDER_DARK);
            repaint();
        }
    }

    // ---------- ตัวช่วยสร้าง UI ----------
    static JPanel transparent(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        return p;
    }

    static JPanel slotGrid(int rows, int cols, int count) {
        JPanel p = transparent(new GridLayout(rows, cols, 8, 8));
        for (int i = 0; i < count; i++) p.add(new Slot());
        return p;
    }

    static JPanel buildLeftPanel() {
        // คอลัมน์ช่องสวมใส่ซ้าย/ขวา
        JPanel leftCol = slotGrid(2, 1, 2);
        JPanel rightCol = slotGrid(2, 1, 2);

        // กล่องตัวละคร 80x100
        BevelPanel character = new BevelPanel(SLOT_BG, BORDER_DARK, 3, 3, CHAR_SHADOW, SLOT_LIGHT);
        character.setPreferredSize(new Dimension(80, 100));

        // วางแบบ ช่อง | ตัวละคร | ช่อง (จัดกึ่งกลางแนวตั้ง)
        JPanel equip = transparent(new FlowLayout(FlowLayout.CENTER, 8, 0));
        equip.add(leftCol);
        equip.add(character);
        equip.add(rightCol);

        JPanel tools = slotGrid(2, 3, 6);

        JPanel left = transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        left.add(equip, c);
        c.gridy = 1;
        c.insets = new Insets(16, 0, 0, 0);
        left.add(tools, c);
        return left;
    }

    static JPanel buildRightPanel() {
        // ป้าย INVENTORY
        BevelPanel title = new BevelPanel(SLOT_BG, BORDER_DARK, 3, 2, SLOT_LIGHT, null);
        title.setLayout(new GridBagLayout());
        title.setBorder(new EmptyBorder(11, 27, 11, 27)); // 3px border + padding 8x24
        JLabel label = new JLabel("INVENTORY");
        label.setForeground(Color.WHITE);
        label.setFont(pixelFont(12f));
        title.add(label);

        // กริดหลัก 6x4
        JPanel mainGrid = slotGrid(4, 6, 24);

        // แถบ hotbar
        BevelPanel hotbar = new BevelPanel(HOTBAR_BG, BORDER_DARK, 3, 3, HOTBAR_SHADOW, null);
        hotbar.setLayout(new GridLayout(1, 6, 8, 0));
        hotbar.setBorder(new EmptyBorder(9, 9, 9, 9)); // 3px border + padding 6
        for (int i = 0; i < 6; i++) hotbar.add(new Slot());

        JPanel right = transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        right.add(title, c);
        c.gridy = 1;
        c.insets = new Insets(12, 0, 0, 0);
        right.add(mainGrid, c);
        c.gridy = 2;
        right.add(hotbar, c);
        return right;
    }

    static Font pixelFont(float size) {
        // ถ้าติดตั้งฟอนต์ Press Start 2P ไว้ในเครื่องจะใช้ตัวนั้น ไม่งั้นใช้ monospace
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String n : names) {
            if (n.equalsIgnoreCase("Press Start 2P")) return new Font(n, Font.PLAIN, Math.round(size));
        }
        return new Font(Font.MONOSPACED, Font.BOLD, Math.round(size));
    }

    static JPanel buildGameWindow() {
        JPanel window = new JPanel(new GridBagLayout());
        window.setBackground(WINDOW_BG);
        window.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(OUTLINE, 4),                       // outline
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_DARK, 6),           // border
                        new EmptyBorder(20, 20, 20, 20))));       // padding

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;

        c.gridx = 0;
        window.add(buildLeftPanel(), c);
        c.gridx = 1;
        c.insets = new Insets(0, 20, 0, 0);
        window.add(buildRightPanel(), c);
        return window;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pixel Inventory UI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel root = new JPanel(new GridBagLayout());
            root.setBackground(PAGE_BG);
            root.setBorder(new EmptyBorder(40, 40, 40, 40));
            root.add(buildGameWindow());

            // ช่องแรกของกระเป๋าถูกเลือกเริ่มต้น (เหมือน class="slot selected")
            JPanel right = (JPanel) ((JPanel) root.getComponent(0)).getComponent(1);
            JPanel mainGrid = (JPanel) right.getComponent(1);
            ((Slot) mainGrid.getComponent(0)).select();

            frame.setContentPane(root);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
