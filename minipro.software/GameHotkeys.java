import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
 
/**
 * ผูกปุ่มลัดของเกมเข้ากับ component หลัก (เช่น Gamepanel)
 *   B = เปิดร้านซื้อของ (กด B หรือ Esc อีกครั้งเพื่อปิด)
 *   H = เปิดกระเป๋า     (กด H หรือ Esc อีกครั้งเพื่อปิด)
 *
 * วิธีใช้: ใน constructor ของ Gamepanel เขียนบรรทัดเดียว
 *   GameHotkeys.install(getRootPane(), player, stock);
 *
 * ถ้าเปิดร้าน/กระเป๋าแล้วเกิด error จะขึ้นหน้าต่างบอกสาเหตุ (และพิมพ์ใน Terminal ด้วย)
 */
public final class GameHotkeys {
 
    private GameHotkeys() {}
 
    public static void install(JComponent target, Player player, List<Item> stock) {
        bind(target, KeyEvent.VK_B, "openShop", () -> ShopPanel.showDialog(target, player, stock));
        bind(target, KeyEvent.VK_H, "openBag", () -> PixelInventory.showDialog(target, player));
    }
 
    private static void bind(JComponent target, int key, String name, Runnable action) {
        target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
        target.getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    action.run();
                } catch (Throwable t) {                       // รวม Error เช่น "Unresolved compilation problem"
                    t.printStackTrace();
                    JOptionPane.showMessageDialog(target,
                            name + " failed:\n" + t, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
 