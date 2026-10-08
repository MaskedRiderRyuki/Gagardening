import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventory {
    // LinkedHashMap เพื่อให้ลำดับไอเทมคงที่ตอนแสดงผลใน GUI
    private final Map<Item, Integer> items = new LinkedHashMap<>();

    public void addItem(Item item, int amount) {
        requirePositive(amount);
        items.merge(item, amount, Integer::sum);
    }

    /** @return true ถ้าลบสำเร็จ, false ถ้าของไม่พอ */
    public boolean removeItem(Item item, int amount) {
        requirePositive(amount);
        int current = getCount(item);
        if (current < amount) {
            return false;
        }
        if (current == amount) {
            items.remove(item);
        } else {
            items.put(item, current - amount);
        }
        return true;
    }

    public int getCount(Item item) {
        return items.getOrDefault(item, 0);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** จำนวนไอเทมรวมทุกชนิด (ใช้โชว์ตัวเลขข้างไอคอนกระเป๋า) */
    public int getTotalCount() {
        int total = 0;
        for (int n : items.values()) {
            total += n;
        }
        return total;
    }

    /** ให้ GUI อ่านได้อย่างเดียว แก้ไขจากภายนอกไม่ได้ */
    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    private static void requirePositive(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
