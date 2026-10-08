import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * หน้าร้านค้าแบบการ์ด มีแท็บ BUY / SELL, ปุ่มเปลี่ยนหน้า, เงิน และจำนวนของในกระเป๋า
 *
 * เป็น JPanel ธรรมดา เอาไปเสียบใน Gamepanel / JFrame / JDialog ได้
 *  - ShopPanel.showDialog(parent, player, stock) เปิดเป็น popup
 *  - setIcon(item, image) ใส่รูปไอเทมจากโฟลเดอร์ picture (ถ้าไม่ใส่จะวาดตัวอักษรแทน)
 *  - refresh() เรียกเมื่อเงิน/ของเปลี่ยนจากที่อื่น เช่น หลังเก็บเกี่ยว
 */
public class ShopPanel extends JPanel {

    private enum Mode { BUY, SELL }

    private static final int PER_PAGE = 4;

    private static final Color BG_TOP      = new Color(0x4a1216);
    private static final Color BG_BOTTOM   = new Color(0x1f0709);
    private static final Color FRAME       = new Color(0x8a2b1c);
    private static final Color CARD_TOP    = new Color(0xd08236);
    private static final Color CARD_BOTTOM = new Color(0x9a5420);
    private static final Color CARD_EDGE   = new Color(0xf3c06a);
    private static final Color DARK_EDGE   = new Color(0x4a2108);
    private static final Color GOLD_TEXT   = new Color(0xffd54a);
    private static final Color CREAM       = new Color(0xffeccb);

    private final Player player;
    private final List<Item> stock;
    private final Map<String, Image> icons = new HashMap<>();
    private Runnable onClose;

    private Mode mode = Mode.BUY;
    private int page = 0;

    private final Pill bagPill = new Pill(Pill.KIND_BAG);
    private final Pill goldPill = new Pill(Pill.KIND_COIN);
    private final PixelButton buyTab = new PixelButton("BUY");
    private final PixelButton sellTab = new PixelButton("SELL");
    private final PixelButton prevBtn = new PixelButton("<");
    private final PixelButton nextBtn = new PixelButton(">");
    private final CloseButton closeBtn = new CloseButton();
    private final JLabel pageLabel = new JLabel("1 / 1", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JPanel cardRow = transparent(new GridLayout(1, PER_PAGE, 14, 0));

    public ShopPanel(Player player, List<Item> stock) {
        this.player = player;
        this.stock = new ArrayList<>(stock);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));
        setPreferredSize(new Dimension(780, 500));

        add(buildHeader(), BorderLayout.NORTH);
        add(cardRow, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        buyTab.addActionListener(e -> setMode(Mode.BUY));
        sellTab.addActionListener(e -> setMode(Mode.SELL));
        prevBtn.addActionListener(e -> { page--; refresh(); });
        nextBtn.addActionListener(e -> { page++; refresh(); });
        closeBtn.addActionListener(e -> { if (onClose != null) onClose.run(); });
        closeBtn.setVisible(false);

        // กด Esc เพื่อปิดร้าน
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeShop");
        getActionMap().put("closeShop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClose != null) onClose.run();
            }
        });

        refresh();
    }

    // ---------------------------------------------------------------- public API

    /** ตั้งการทำงานเมื่อกดปุ่มปิด (ปุ่ม X จะโผล่ก็ต่อเมื่อตั้งค่านี้) */
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
        closeBtn.setVisible(onClose != null);
    }

    public void setIcon(Item item, Image image) {
        icons.put(item.getName(), image);
        refresh();
    }

    /** เปิดร้านเป็น popup แบบ modal */
    public static void showDialog(Component parent, Player player, List<Item> stock) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Shop", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        ShopPanel panel = new ShopPanel(player, stock);
        panel.setOnClose(dialog::dispose);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /** อัปเดตเงิน จำนวนของ และวาดการ์ดใหม่ทั้งหมด */
    public void refresh() {
        List<Row> rows = currentRows();
        int pages = Math.max(1, (rows.size() + PER_PAGE - 1) / PER_PAGE);
        page = Math.max(0, Math.min(page, pages - 1));

        goldPill.setText(String.valueOf(player.getGold()));
        bagPill.setText(String.valueOf(player.getInventory().getTotalCount()));
        buyTab.setActive(mode == Mode.BUY);
        sellTab.setActive(mode == Mode.SELL);
        pageLabel.setText((page + 1) + " / " + pages);
        prevBtn.setEnabled(page > 0);
        nextBtn.setEnabled(page < pages - 1);

        cardRow.removeAll();
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE; i++) {
            int idx = start + i;
            if (idx < rows.size()) {
                cardRow.add(new Card(rows.get(idx)));
            } else if (rows.isEmpty() && i == 0) {
                cardRow.add(new Placeholder(mode == Mode.BUY
                        ? "Nothing for sale" : "Nothing to sell"));
            } else {
                cardRow.add(new Placeholder(null));
            }
        }
        cardRow.revalidate();
        cardRow.repaint();
        repaint();
    }

    // ---------------------------------------------------------------- logic

    private void setMode(Mode newMode) {
        if (mode == newMode) return;
        mode = newMode;
        page = 0;
        statusLabel.setText(" ");
        refresh();
    }

    private List<Row> currentRows() {
        List<Row> rows = new ArrayList<>();
        Inventory inv = player.getInventory();
        if (mode == Mode.BUY) {
            for (Item item : stock) {
                if (item.isBuyable()) rows.add(new Row(item, inv.getCount(item)));
            }
        } else {
            for (Map.Entry<Item, Integer> e : inv.getItems().entrySet()) {
                if (e.getKey().isSellable()) rows.add(new Row(e.getKey(), e.getValue()));
            }
        }
        return rows;
    }

    private void handleBuy(Item item) {
        Player.TradeResult result = player.buyItem(item, 1);
        switch (result) {
            case SUCCESS:
                statusLabel.setText("Bought 1 " + item.getName() + "  (-" + item.getBuyPrice() + "G)");
                break;
            case NOT_ENOUGH_GOLD:
                statusLabel.setText("Not enough gold! Need "
                        + (item.getBuyPrice() - player.getGold()) + " more.");
                break;
            default:
                statusLabel.setText("Can't buy " + item.getName() + ".");
        }
        refresh();
    }

    private void handleSell(Item item, boolean all) {
        int amount = all ? player.getInventory().getCount(item) : 1;
        Player.TradeResult result = player.sellItem(item, amount);
        switch (result) {
            case SUCCESS:
                statusLabel.setText("Sold " + amount + " " + item.getName()
                        + "  (+" + (item.getSellPrice() * amount) + "G)");
                break;
            case NOT_ENOUGH_ITEMS:
                statusLabel.setText("You don't have any " + item.getName() + ".");
                break;
            default:
                statusLabel.setText("Can't sell " + item.getName() + ".");
        }
        refresh();
    }

    // ---------------------------------------------------------------- layout

    private JComponent buildHeader() {
        JPanel header = transparent(new BorderLayout());

        JPanel left = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.add(bagPill);
        left.setPreferredSize(new Dimension(200, 36));

        JPanel right = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(goldPill);
        right.add(closeBtn);
        right.setPreferredSize(new Dimension(200, 36));

        buyTab.setPreferredSize(new Dimension(96, 30));
        sellTab.setPreferredSize(new Dimension(96, 30));
        JPanel tabs = transparent(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tabs.add(buyTab);
        tabs.add(sellTab);

        JPanel center = transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        center.add(new Sign("SHOP"), c);
        c.gridy = 1;
        c.insets = new Insets(10, 0, 0, 0);
        center.add(tabs, c);

        header.add(left, BorderLayout.WEST);
        header.add(center, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildFooter() {
        JPanel footer = transparent(new BorderLayout(10, 0));

        prevBtn.setPreferredSize(new Dimension(46, 34));
        nextBtn.setPreferredSize(new Dimension(46, 34));

        pageLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        pageLabel.setForeground(CREAM);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        statusLabel.setForeground(GOLD_TEXT);

        JPanel mid = transparent(new GridLayout(2, 1, 0, 2));
        mid.add(statusLabel);
        mid.add(pageLabel);

        footer.add(prevBtn, BorderLayout.WEST);
        footer.add(mid, BorderLayout.CENTER);
        footer.add(nextBtn, BorderLayout.EAST);
        return footer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        antialias(g2);
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(FRAME);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
        g2.dispose();
    }

    // ---------------------------------------------------------------- helpers

    private static JPanel transparent(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        return p;
    }

    private static void antialias(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static void drawCentered(Graphics2D g, String s, int x, int y, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(s)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, tx, ty);
    }

    private static void drawCoin(Graphics2D g, int x, int y, int d) {
        g.setColor(new Color(0x9a6a00));
        g.fillOval(x, y, d, d);
        g.setPaint(new GradientPaint(x, y, new Color(0xffe27a), x, y + d, new Color(0xf0b400)));
        g.fillOval(x + 1, y + 1, d - 2, d - 2);
        g.setColor(new Color(0xc98f00));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x + d / 4, y + d / 4, d / 2, d / 2);
    }

    // ---------------------------------------------------------------- data row

    private static class Row {
        final Item item;
        final int owned;

        Row(Item item, int owned) {
            this.item = item;
            this.owned = owned;
        }
    }

    // ---------------------------------------------------------------- item card

    private class Card extends JPanel {
        Card(Row row) {
            Item item = row.item;
            setOpaque(false);
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));

            add(new Ribbon(item.getName()), BorderLayout.NORTH);

            JPanel mid = transparent(new BorderLayout(0, 2));
            mid.add(new IconView(item.getName(), icons.get(item.getName())), BorderLayout.CENTER);
            if (mode == Mode.BUY) {
                mid.add(new PriceLine(item.getBuyPrice(), "Owned x" + row.owned), BorderLayout.SOUTH);
            } else {
                mid.add(new PriceLine(item.getSellPrice(), "In bag x" + row.owned), BorderLayout.SOUTH);
            }
            add(mid, BorderLayout.CENTER);

            if (mode == Mode.BUY) {
                PixelButton buy = new PixelButton("BUY");
                buy.setPreferredSize(new Dimension(100, 34));
                buy.addActionListener(e -> handleBuy(item));
                add(buy, BorderLayout.SOUTH);
            } else {
                PixelButton one = new PixelButton("SELL 1");
                PixelButton all = new PixelButton("SELL ALL");
                one.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                all.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                one.setPreferredSize(new Dimension(70, 34));
                all.setPreferredSize(new Dimension(70, 34));
                one.addActionListener(e -> handleSell(item, false));
                all.addActionListener(e -> handleSell(item, true));
                JPanel buttons = transparent(new GridLayout(1, 2, 6, 0));
                buttons.add(one);
                buttons.add(all);
                add(buttons, BorderLayout.SOUTH);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            g2.setColor(DARK_EDGE);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 18, 18);
            g2.setPaint(new GradientPaint(0, 0, CARD_TOP, 0, h, CARD_BOTTOM));
            g2.fillRoundRect(5, 5, w - 10, h - 10, 14, 14);
            g2.setColor(CARD_EDGE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(5, 5, w - 11, h - 11, 14, 14);
            g2.dispose();
        }
    }

    /** ช่องว่างเวลาไอเทมไม่ครบหน้า (หรือไม่มีของให้แสดง) */
    private static class Placeholder extends JComponent {
        private final String message;

        Placeholder(String message) {
            this.message = message;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(5, 5, w - 10, h - 10, 14, 14);
            g2.setColor(new Color(0xf3, 0xc0, 0x6a, 90));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{6f, 6f}, 0f));
            g2.drawRoundRect(5, 5, w - 11, h - 11, 14, 14);
            if (message != null) {
                g2.setColor(CREAM);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                drawCentered(g2, message, 0, 0, w, h);
            }
            g2.dispose();
        }
    }

    /** ป้ายชื่อไอเทมบนหัวการ์ด */
    private static class Ribbon extends JComponent {
        private final String text;

        Ribbon(String text) {
            this.text = text;
            setPreferredSize(new Dimension(100, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            g2.setColor(DARK_EDGE);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.setColor(new Color(0xf0b060));
            g2.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);

            Font f = new Font(Font.SANS_SERIF, Font.BOLD, 13);
            for (int size = 13; size >= 9; size--) {
                f = new Font(Font.SANS_SERIF, Font.BOLD, size);
                if (g2.getFontMetrics(f).stringWidth(text) <= w - 16) break;
            }
            g2.setFont(f);
            g2.setColor(DARK_EDGE);
            drawCentered(g2, text, 0, 0, w, h);
            g2.dispose();
        }
    }

    /** รูปไอเทม ถ้าไม่มีรูปจะวาดสี่เหลี่ยมสี + ตัวอักษรแรกของชื่อ */
    private static class IconView extends JComponent {
        private final String name;
        private final Image image;

        IconView(String name, Image image) {
            this.name = name;
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int s = Math.min(getWidth(), getHeight()) - 10;
            if (s > 0) {
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                if (image != null) {
                    g2.drawImage(image, x, y, s, s, null);
                } else {
                    float hue = ((name.hashCode() & 0x7fffffff) % 360) / 360f;
                    g2.setColor(new Color(0, 0, 0, 70));
                    g2.fillRoundRect(x + 2, y + 4, s, s, 20, 20);
                    g2.setColor(Color.getHSBColor(hue, 0.55f, 0.85f));
                    g2.fillRoundRect(x, y, s, s, 20, 20);
                    g2.setColor(new Color(255, 255, 255, 70));
                    g2.fillRoundRect(x + 4, y + 4, s - 8, s / 3, 14, 14);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, s / 2)));
                    drawCentered(g2, name.substring(0, 1).toUpperCase(), x, y, s, s);
                }
            }
            g2.dispose();
        }
    }

    /** บรรทัดราคา (เหรียญ + ตัวเลข) และข้อความจำนวนที่มี */
    private static class PriceLine extends JComponent {
        private final int price;
        private final String info;

        PriceLine(int price, String info) {
            this.price = price;
            this.info = info;
            setPreferredSize(new Dimension(100, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth();

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            String p = String.valueOf(price);
            int d = 16;
            int tw = g2.getFontMetrics().stringWidth(p);
            int x0 = (w - (d + 6 + tw)) / 2;
            drawCoin(g2, x0, 4, d);
            g2.setColor(DARK_EDGE);
            drawCentered(g2, p, x0 + d + 6 + 1, 5, tw, 20);
            g2.setColor(GOLD_TEXT);
            drawCentered(g2, p, x0 + d + 6, 4, tw, 20);

            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(CREAM);
            drawCentered(g2, info, 0, 24, w, 18);
            g2.dispose();
        }
    }

    // ---------------------------------------------------------------- header parts

    /** แคปซูลโชว์เงิน / จำนวนของในกระเป๋า */
    private static class Pill extends JComponent {
        static final int KIND_BAG = 0;
        static final int KIND_COIN = 1;

        private final int kind;
        private String text = "0";

        Pill(int kind) {
            this.kind = kind;
            setPreferredSize(new Dimension(120, 34));
        }

        void setText(String text) {
            this.text = text;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(0x1a0507));
            g2.fillRoundRect(0, 0, w, h, h, h);
            g2.setColor(CARD_EDGE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, h, h);

            int d = h - 10;
            if (kind == KIND_COIN) {
                drawCoin(g2, 7, 5, d);
            } else {
                // กระเป๋าเป้
                g2.setColor(DARK_EDGE);
                g2.fillRoundRect(7 + d / 4, 4, d / 2, 9, 6, 6);
                g2.setColor(new Color(0xc0392b));
                g2.fillRoundRect(7, 8, d, d - 3, 8, 8);
                g2.setColor(new Color(0x7a1f14));
                g2.fillRoundRect(7 + d / 4, 8 + d / 2, d / 2, d / 4, 4, 4);
            }

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            g2.setColor(kind == KIND_COIN ? GOLD_TEXT : Color.WHITE);
            int tw = g2.getFontMetrics().stringWidth(text);
            drawCentered(g2, text, w - 16 - tw, 0, tw, h);
            g2.dispose();
        }
    }

    /** ป้ายไม้ SHOP */
    private static class Sign extends JComponent {
        private final String text;

        Sign(String text) {
            this.text = text;
            setPreferredSize(new Dimension(200, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();

            g2.setColor(new Color(0xc9a66b));
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(w / 4, 0, w / 4, 8);
            g2.drawLine(3 * w / 4, 0, 3 * w / 4, 8);

            g2.setColor(DARK_EDGE);
            g2.fillRoundRect(0, 6, w, h - 6, 12, 12);
            g2.setPaint(new GradientPaint(0, 6, new Color(0xe29a47), 0, h, new Color(0xb06a25)));
            g2.fillRoundRect(3, 9, w - 6, h - 12, 10, 10);
            g2.setColor(DARK_EDGE);
            g2.fillOval(10, 22, 6, 6);
            g2.fillOval(w - 16, 22, 6, 6);

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            g2.setColor(DARK_EDGE);
            drawCentered(g2, text, 0, 3, w, h);
            g2.setColor(new Color(0xfff1d0));
            drawCentered(g2, text, 0, 1, w, h);
            g2.dispose();
        }
    }

    // ---------------------------------------------------------------- buttons

    private static class PixelButton extends JButton {
        private boolean active;

        PixelButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            ButtonModel m = getModel();

            Color top, bottom;
            if (!isEnabled()) {
                top = new Color(0x8d8d8d);
                bottom = new Color(0x5c5c5c);
            } else if (active) {
                top = new Color(0xffdc55);
                bottom = new Color(0xe39a12);
            } else if (m.isPressed()) {
                top = new Color(0xd9441c);
                bottom = new Color(0xb02f10);
            } else if (m.isRollover()) {
                top = new Color(0xff9a52);
                bottom = new Color(0xea5a24);
            } else {
                top = new Color(0xff7d3d);
                bottom = new Color(0xd9441c);
            }

            g2.setColor(new Color(0x5e1708));
            g2.fillRoundRect(0, 0, w, h, 18, 18);
            g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
            g2.fillRoundRect(2, 2, w - 4, h - 5, 14, 14);

            g2.setFont(getFont());
            g2.setColor(new Color(0, 0, 0, 90));
            drawCentered(g2, getText(), 0, 1, w, h - 2);
            g2.setColor(!isEnabled() ? new Color(0xdddddd) : (active ? DARK_EDGE : Color.WHITE));
            drawCentered(g2, getText(), 0, 0, w, h - 2);
            g2.dispose();
        }
    }

    private static class CloseButton extends JButton {
        CloseButton() {
            setPreferredSize(new Dimension(34, 34));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setToolTipText("Close (Esc)");
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(0x5e1708));
            g2.fillOval(0, 0, w, h);
            g2.setColor(getModel().isRollover() ? new Color(0xff6a4a) : new Color(0xe0472b));
            g2.fillOval(3, 3, w - 6, h - 6);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(11, 11, w - 11, h - 11);
            g2.drawLine(w - 11, 11, 11, h - 11);
            g2.dispose();
        }
    }
}
