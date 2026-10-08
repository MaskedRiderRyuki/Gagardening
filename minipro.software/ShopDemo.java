import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/** เปิดหน้าร้านขึ้นมาทดสอบ (ใช้แทน FarmingGUI เดิม) */
public class ShopDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Player player = new Player(150);

            // ของที่ร้านขาย (ราคาซื้อ, ราคารับซื้อคืน)
            Item carrotSeed  = new Item("Carrot Seed", 20, 10);
            Item potatoSeed  = new Item("Potato Seed", 15, 7);
            Item tomatoSeed  = new Item("Tomato Seed", 30, 15);
            Item pumpkinSeed = new Item("Pumpkin Seed", 45, 22);
            Item cornSeed    = new Item("Corn Seed", 25, 12);
            List<Item> stock = Arrays.asList(carrotSeed, potatoSeed, tomatoSeed, pumpkinSeed, cornSeed);

            // ผลผลิต: ซื้อไม่ได้ (ราคาซื้อ 0) ขายได้อย่างเดียว
            Item potato = new Item("Harvested Potato", 0, 25);
            Item carrot = new Item("Harvested Carrot", 0, 30);
            player.getInventory().addItem(potato, 5);
            player.getInventory().addItem(carrot, 3);

            JFrame frame = new JFrame("Gagardening - Shop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ShopPanel shop = new ShopPanel(player, stock);
            shop.setOnClose(frame::dispose);

            frame.setContentPane(shop);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
