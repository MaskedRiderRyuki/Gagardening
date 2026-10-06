import java.awt.*;
import javax.swing.*;

public class Gamepanel extends JFrame {
    // จำนวนแถวและคอลัมน์
    final int ROWS = 15;
    final int COLS = 15;
    // ขนาดแต่ละช่อง
    final int Grass = 40;
    // 0 = หญ้า 1 = ถนน
    int[][] road = new int[ROWS][COLS];
    public Gamepanel() {
        setTitle("GAGAR Dening");
         ImageIcon icon = new ImageIcon("Picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createroad();// กำหนดตำแหน่งถนน
        roadPanel roadPanel = new roadPanel();// สร้างพื้นที่สำหรับวาดถนน
        add(roadPanel);
        pack();// ให้เฟรมปรับขนาดตาม JPanel
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    public void createroad() {
        // ถนนแนวตั้ง
        for (int row = 0; row < 8; row++) {
            road[row][4] = 1;
        }
        // ถนนแนวนอน
        for (int col = 4; col < 12; col++) {
            road[6][col] = 1;
        }
        // ถนนแนวตั้งด้านขวา
        for (int row = 6; row < 12; row++) {
            road[row][9] = 1;
        }
    }
    // JPanel สำหรับวาดถนน
    class roadPanel extends JPanel {
        public roadPanel() {
            setPreferredSize(
                new Dimension(
                    COLS * Grass,
                    ROWS * Grass
                )
            );
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // วาด 15 x 15 ช่อง
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int x = col * Grass;
                    int y = row * Grass;
                    // ถ้าเป็นหญ้า
                    if (road[row][col] == 0) {
                        g.setColor(new Color(120, 200, 140));
                    }
                    // ถ้าเป็นถนน
                    else if (road[row][col] == 1) {
                        g.setColor(new Color(110, 80, 60));
                    }
                    // ระบายสีช่อง
                    g.fillRect(x, y, Grass, Grass);
                    // วาดเส้นช่อง
                    g.setColor(new Color(0, 0, 0, 30));
                    g.drawRect(x,y,Grass,Grass);
                }
            }
        }
    }
}
