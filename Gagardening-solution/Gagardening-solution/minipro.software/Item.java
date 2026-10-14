import java.util.Objects;

public class Item {
    private final String name;
    private final int buyPrice;   // 0 = ซื้อจากร้านไม่ได้ (เช่น ผลผลิตที่เก็บเกี่ยว)
    private final int sellPrice;  // 0 = ขายให้ร้านไม่ได้

    public Item(String name, int buyPrice, int sellPrice) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }

    public boolean isBuyable() { return buyPrice > 0; }
    public boolean isSellable() { return sellPrice > 0; }

    // เทียบกันด้วยชื่อ ทำให้ new Item("Carrot Seed", ...) สองครั้งถูกนับเป็นไอเทมเดียวกันใน HashMap
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return name.equals(((Item) o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() { return name; }
}
