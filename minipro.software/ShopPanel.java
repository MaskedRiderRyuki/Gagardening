import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * หน้าร้านค้าแบบเรียบง่าย ใช้ปุ่ม/ป้ายของ Swing ธรรมดา ไม่มีการวาดเอง
 *   - แท็บ Buy Seeds (ซื้อเมล็ด) / Sell Produce (ขายผลผลิต)
 *   - แต่ละแถวมีปุ่ม Buy หรือ Sell 1 / Sell All
 *   - ปุ่ม Sell All Produce ขายผลผลิตทั้งหมดในครั้งเดียว (เมล็ดจะไม่ถูกขาย)
 *
 * วิธีใช้:
 *   ShopPanel.showDialog(parent, player, stock);   // เปิดเป็น popup
 *   setIcon(item, image)  ใส่รูปไอเทม (ไม่ใส่ก็ได้)
 *   refresh()             เรียกเมื่อเงิน/ของเปลี่ยนจากที่อื่น เช่น หลังเก็บเกี่ยว
 *   กด Esc หรือ B เพื่อปิดร้าน
 */
public class ShopPanel extends JPanel {

    private static final Color BG = new Color(0x5c3b1e);
    private static final Color ROW_BG = new Color(0xe8c48a);
    private static final Color GOLD = new Color(0xffd54a);

    private final Player player;
    private final List<Item> stock;
    private final Map<String, Image> icons = new HashMap<>();
    private Runnable onClose;
    private boolean buying = true;   // true = แท็บซื้อ, false = แท็บขาย

    private final JLabel goldLabel = label("", 16, GOLD);
    private final JLabel bagLabel = label("", 16, Color.WHITE);
    private final JLabel statusLabel = label(" ", 14, GOLD);
    private final JButton buyTab = new JButton("Buy Seeds");
    private final JButton sellTab = new JButton("Sell Produce");
    private final JButton sellAllBtn = new JButton("Sell All Produce");
    private final JButton closeBtn = new JButton("X");
    private final JPanel list = new JPanel(new GridLayout(0, 1, 0, 6));

    public ShopPanel(Player player, List<Item> stock) {
        this.player = player;
        this.stock = new ArrayList<>(stock);

        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setPreferredSize(new Dimension(560, 420));

        // ส่วนบน: กระเป๋า / ชื่อร้าน / เงิน + ปิด และแถวแท็บ
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(goldLabel);
        right.add(closeBtn);
        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.add(bagLabel, BorderLayout.WEST);
        info.add(label("SHOP", 24, Color.WHITE), BorderLayout.CENTER);
        info.add(right, BorderLayout.EAST);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tabs.setOpaque(false);
        tabs.add(buyTab);
        tabs.add(sellTab);
        tabs.add(sellAllBtn);

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 8));
        top.setOpaque(false);
        top.add(info);
        top.add(tabs);
        add(top, BorderLayout.NORTH);

        // ส่วนกลาง: รายการสินค้า (เลื่อนได้ถ้าเยอะ)
        list.setOpaque(false);
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(BG);
        holder.add(list, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x3a2410), 2));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ส่วนล่าง: ข้อความสถานะ
        add(statusLabel, BorderLayout.SOUTH);

        buyTab.addActionListener(e -> setBuying(true));
        sellTab.addActionListener(e -> setBuying(false));
        sellAllBtn.addActionListener(e -> handleSellAll());
        closeBtn.addActionListener(e -> close());
        closeBtn.setVisible(false);

        // กด Esc หรือ B เพื่อปิดร้าน
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeShop");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "closeShop");
        getActionMap().put("closeShop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { close(); }
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

    /** อัปเดตเงิน จำนวนของ และวาดรายการใหม่ทั้งหมด */
    public void refresh() {
        goldLabel.setText("Gold: " + player.getGold() + "G");
        bagLabel.setText("Bag: " + player.getInventory().getTotalCount());
        buyTab.setEnabled(!buying);        // แท็บที่เปิดอยู่จะกดไม่ได้ (เป็นสีเทา)
        sellTab.setEnabled(buying);

        int produceValue = 0;
        list.removeAll();
        for (Item item : currentItems()) {
            list.add(buildRow(item));
            if (!buying && !item.isBuyable()) produceValue += item.getSellPrice() * player.getInventory().getCount(item);
        }
        sellAllBtn.setVisible(!buying);
        sellAllBtn.setText("Sell All Produce (+" + produceValue + "G)");
        sellAllBtn.setEnabled(produceValue > 0);

        list.revalidate();
        list.repaint();
    }

    // ---------------------------------------------------------------- logic

    private void close() {
        if (onClose != null) onClose.run();
    }

    private void setBuying(boolean buying) {
        this.buying = buying;
        statusLabel.setText(" ");
        refresh();
    }

    /** ไอเทมที่แสดงในแท็บปัจจุบัน: แท็บซื้อ = ของที่ร้านขาย, แท็บขาย = ของในกระเป๋าที่ขายได้ */
    private List<Item> currentItems() {
        List<Item> items = new ArrayList<>();
        if (buying) {
            for (Item item : stock) if (item.isBuyable()) items.add(item);
        } else {
            for (Item item : player.getInventory().getItems().keySet()) if (item.isSellable()) items.add(item);
        }
        return items;
    }

    private void handleBuy(Item item) {
        statusLabel.setText(switch (player.buyItem(item, 1)) {
            case SUCCESS -> "Bought 1 " + item.getName() + " (-" + item.getBuyPrice() + "G)";
            case NOT_ENOUGH_GOLD -> "Not enough gold! Need " + (item.getBuyPrice() - player.getGold()) + " more.";
            default -> "Can't buy " + item.getName() + ".";
        });
        refresh();
    }

    private void handleSell(Item item, int amount) {
        statusLabel.setText(switch (player.sellItem(item, amount)) {
            case SUCCESS -> "Sold " + amount + " " + item.getName() + " (+" + item.getSellPrice() * amount + "G)";
            case NOT_ENOUGH_ITEMS -> "You don't have enough " + item.getName() + ".";
            default -> "Can't sell " + item.getName() + ".";
        });
        refresh();
    }

    /** ขายผลผลิตทั้งหมด (ของที่ซื้อจากร้านไม่ได้ เช่น ผักที่เก็บเกี่ยว) เมล็ดจะไม่ถูกขาย */
    private void handleSellAll() {
        int total = 0;
        for (Item item : currentItems()) {
            int amount = player.getInventory().getCount(item);
            if (!item.isBuyable() && player.sellItem(item, amount) == Player.TradeResult.SUCCESS) {
                total += item.getSellPrice() * amount;
            }
        }
        statusLabel.setText(total > 0 ? "Sold all produce (+" + total + "G)" : "No produce to sell.");
        refresh();
    }

    // ---------------------------------------------------------------- UI helpers

    /** สร้างแถวสินค้า 1 แถว: ชื่อ(+รูป) | ราคา/จำนวนที่มี | ปุ่ม */
    private JPanel buildRow(Item item) {
        int owned = player.getInventory().getCount(item);
        int price = buying ? item.getBuyPrice() : item.getSellPrice();

        Image img = icons.get(item.getName());
        Icon icon = img == null ? null : new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_FAST));
        JLabel name = new JLabel(item.getName(), icon, SwingConstants.LEFT);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 16f));
        name.setPreferredSize(new Dimension(190, 44));

        JLabel detail = new JLabel(price + "G   (" + (buying ? "owned " : "in bag ") + owned + ")");
        detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setOpaque(false);
        if (buying) {
            buttons.add(button("Buy", e -> handleBuy(item)));
        } else {
            buttons.add(button("Sell 1", e -> handleSell(item, 1)));
            buttons.add(button("Sell All", e -> handleSell(item, owned)));
        }

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(ROW_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3a2410), 2),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        row.add(name, BorderLayout.WEST);
        row.add(detail, BorderLayout.CENTER);
        row.add(buttons, BorderLayout.EAST);
        return row;
    }

    private static JButton button(String text, ActionListener action) {
        JButton b = new JButton(text);
        b.addActionListener(action);
        return b;
    }

    private static JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, (float) size));
        l.setForeground(color);
        return l;
    }
}
