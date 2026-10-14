import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Gamepanel extends JFrame implements KeyListener {
    // จำนวนแถวและคอลัมน์
    final int ROWS = 15;
    final int COLS = 15;
    // ขนาดแต่ละช่อง
    final int Grass = 40;

    // ตำแหน่งตัวละคร
    int playerX = 40;
    int playerY = 40;
    // จำตำแหน่งล่าสุด
    int lastX = playerX;
    int lastY = playerY;
    // รูปตัวละคร
    Image playerImage;

    public Gamepanel() {
        setTitle("GAGAR Dening");
        ImageIcon icon = new ImageIcon("minipro.software/picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);
        addKeyListener(this);
        // ป้องกัน Tab แย่ง Focus
        setFocusTraversalKeysEnabled(false);

        BoxPanel boxPanel = new BoxPanel();// สร้างพื้นที่สำหรับวาดพื้นเกม
        add(boxPanel);

        // โหลดรูปตัวละคร
        playerImage = new ImageIcon(
            "minipro.software/picture/character.png"
        ).getImage();

        pack();// ให้เฟรมปรับขนาดตาม JPanel
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    // JPanel สำหรับวาดพื้นเกม
    class BoxPanel extends JPanel {
        public BoxPanel() {
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
            // วาด 15*15 ช่อง
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    int x = col * Grass;
                    int y = row * Grass;
                    g.setColor(new Color(120, 200, 140));
                    // ระบายสีช่อง
                    g.fillRect(x, y, Grass, Grass);
                    // วาดเส้นช่อง
                    g.setColor(new Color(0, 0, 0, 30));
                    g.drawRect(x,y,Grass,Grass);
                }
            }
            g.drawImage(playerImage,playerX,playerY,40,40,
                this
            );
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        // จำตำแหน่งก่อนเดิน
            lastX = playerX;
            lastY = playerY;
            // W = ขึ้น
            if (e.getKeyCode() == KeyEvent.VK_W) {
                if ( playerY - 40 >= 0) {
                    playerY -= 40;
                }
            }
            // S = ลง
            if (e.getKeyCode() == KeyEvent.VK_S) {
                if (playerY  + 40 <= ROWS * Grass - 40) {
                    playerY += 40;
                }
            }

            // A = ซ้าย
            if (e.getKeyCode() == KeyEvent.VK_A) {
                if (playerX - 40 >= 0) {
                    playerX -= 40;
                }
            }

            // D = ขวา
            if (e.getKeyCode() == KeyEvent.VK_D) {
                if (playerX + 40 <= COLS * Grass - 40) {
                     playerX += 40;
                }
            }
        
            // วาดใหม่
            repaint();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
