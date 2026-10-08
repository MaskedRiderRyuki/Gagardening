import java.util.Map;

// Gagardening: เวอร์ชัน console ไว้ทดสอบ logic (ไม่ประกาศ Item/Inventory ซ้ำแล้ว ใช้จากไฟล์ของตัวเอง)
public class FarmingGame {

    public static void main(String[] args) {
        Player player = new Player(100);

        Item carrotSeed = new Item("Carrot Seed", 10, 5);
        Item potato = new Item("Harvested Potato", 0, 25);

        System.out.println("--- Start Game: 100 Gold ---");

        System.out.println("buy 3 seeds      -> " + player.buyItem(carrotSeed, 3));
        showStatus(player);

        System.out.println("--- 3 Days Later (Harvest Time) ---");
        player.getInventory().addItem(potato, 5);
        showStatus(player);

        System.out.println("sell 2 potatoes  -> " + player.sellItem(potato, 2));
        showStatus(player);

        // เคสทดสอบ error
        System.out.println("sell 99 potatoes -> " + player.sellItem(potato, 99));
        System.out.println("buy -5 seeds     -> " + player.buyItem(carrotSeed, -5));
        System.out.println("buy potato       -> " + player.buyItem(potato, 1));
        System.out.println("buy 1000 seeds   -> " + player.buyItem(carrotSeed, 1000));
    }

    private static void showStatus(Player player) {
        System.out.println("Gold: " + player.getGold() + "G");
        if (player.getInventory().isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        for (Map.Entry<Item, Integer> e : player.getInventory().getItems().entrySet()) {
            System.out.println("- " + e.getKey().getName() + " x" + e.getValue());
        }
    }
}
