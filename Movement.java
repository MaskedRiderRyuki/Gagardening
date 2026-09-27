import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Movement extends JFrame implements KeyListener {
    int x =100;
    int y =100;
    int speed =10;
    public Movement() {
        setTitle("Movement WASD");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ลงทะเบียน KeyListener และตั้งค่าให้หน้าต่างโฟกัสการกดปุ่ม
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        setVisible(true);
    }
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 30, 30); // วาดสี่เหลี่ยมแทนตัวละคร
    }
    //  KeyListener เกี่ยวกับกดแป้นพิมพ์ ฉะนั้นในนี้ต้องมีปุ่มอื่นด้วย เช่นเข้าร้านค้ากด F เปิดกระเป๋ากด G 
    // ของเราตรงนี้น่าจะต้องอยู่ใน ui แสดงภาพด้วย เอาส่วนนี้ไปปรับเอานะ
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // ตรวจสอบการกดปุ่ม W, A, S, D (รองรับทั้งตัวพิมพ์เล็กและพิมพ์ใหญ่)
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            y -= speed; // ขึ้นข้างบน
        } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            y += speed; // ลงข้างล่าง
        } else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            x -= speed; // ไปทางซ้าย
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            x += speed; // ไปทางขวา
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    public static void main(String[] args) {
        new Movement();
    }
}
