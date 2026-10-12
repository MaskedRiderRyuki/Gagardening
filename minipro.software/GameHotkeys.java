import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.swing.*;
 
/**
 * ผูกปุ่มลัดของเกมเข้ากับ component หลัก (เช่น Gamepanel)
 *   B = เปิดร้านซื้อของ (กด B หรือ Esc อีกครั้งเพื่อปิด)
 *   H = เปิดกระเป๋า     (กด H หรือ Esc อีกครั้งเพื่อปิด)
 * ถ้าเปิดร้าน/กระเป๋าแล้วเกิด error จะขึ้นหน้าต่างบอกสาเหตุ (และพิมพ์ใน Terminal ด้วย)
 */
public final class GameHotkeys {
 
    private GameHotkeys() {}
 
    public static void install(JComponent target, Player player, List<Item> stock) {
        install(target, player, stock, null, () -> true, () -> {});
    }

    /**
     * @param icons        รูปไอเทมในร้าน (ชื่อไอเทม -> รูป) ใส่ null ได้
     * @param canOpenShop  คืน true เมื่อผู้เล่นยืนติดร้านอยู่ (กด B ได้)
     * @param onShopDenied ถูกเรียกเมื่อกด B แต่ยืนไกลร้าน (เช่น โชว์ข้อความเตือน)
     */
    public static void install(JComponent target, Player player, List<Item> stock,
                               Map<String, Image> icons, BooleanSupplier canOpenShop, Runnable onShopDenied) {
        bind(target, KeyEvent.VK_B, "openShop", () -> {
            if (canOpenShop.getAsBoolean()) ShopPanel.showDialog(target, player, stock, icons);
            else onShopDenied.run();
        });
        bind(target, KeyEvent.VK_H, "openBag", () -> PixelInventory.showDialog(target, player));
    }
 
    private static void bind(JComponent target, int key, String name, Runnable action) {
        target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
        target.getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    action.run();
                } catch (Throwable t) {                     
                    t.printStackTrace();
                    JOptionPane.showMessageDialog(target,
                            name + " failed:\n" + t, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
 