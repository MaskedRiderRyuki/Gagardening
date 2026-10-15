/**
 * เก็บเงิน + กระเป๋า และมีตรรกะซื้อ/ขายอยู่ที่เดียว
 * ทั้งเวอร์ชัน console และ GUI เรียกใช้คลาสนี้ร่วมกัน
 * (ถ้าทีมมีคลาส Player อยู่แล้ว ให้ย้ายเมธอด buyItem/sellItem ไปรวม หรือเปลี่ยนชื่อคลาสนี้)
 */
public class Player {

    public enum TradeResult {
        SUCCESS, INVALID_AMOUNT, NOT_FOR_SALE, NOT_ENOUGH_GOLD, NOT_ENOUGH_ITEMS
    }

    private int gold;
    private final Inventory inventory = new Inventory();

    public Player(int startingGold) {
        this.gold = startingGold;
    }

    public int getGold() { return gold; }
    public Inventory getInventory() { return inventory; }

    public TradeResult buyItem(Item item, int amount) {
        if (amount <= 0) return TradeResult.INVALID_AMOUNT;   // กันจำนวนติดลบแล้วเงินเพิ่ม
        if (!item.isBuyable()) return TradeResult.NOT_FOR_SALE;

        long totalCost = (long) item.getBuyPrice() * amount;
        if (gold < totalCost) return TradeResult.NOT_ENOUGH_GOLD;

        gold -= (int) totalCost;
        inventory.addItem(item, amount);
        return TradeResult.SUCCESS;
    }

    public TradeResult sellItem(Item item, int amount) {
        if (amount <= 0) return TradeResult.INVALID_AMOUNT;
        if (!item.isSellable()) return TradeResult.NOT_FOR_SALE;
        if (!inventory.removeItem(item, amount)) return TradeResult.NOT_ENOUGH_ITEMS;

        gold += item.getSellPrice() * amount;
        return TradeResult.SUCCESS;
    }
}
