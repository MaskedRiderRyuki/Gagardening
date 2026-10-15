import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

public class Gamepanel extends JFrame implements KeyListener {

    // จำนวนแถวและคอลัมน์
    final int ROWS = 15;
    final int COLS = 15;

    // ขนาดแต่ละช่อง
    final int Grass = 40;

    Crop[][] crops = new Crop[ROWS][COLS];

    // วันที่ในเกม
    int currentDay = 1;

    // ระบบพลังงาน
    int maxEnergy = 100;
    int energy = 100;

    // จำว่าช่องไหนถูกขุดแล้ว
    boolean[][] tilled = new boolean[ROWS][COLS];

    // 0 = หญ้า
    // 1 = ถนน
    int[][] road = new int[ROWS][COLS];

    // ตำแหน่งตัวละคร
    int playerX = 40;
    int playerY = 40;

    // รูปตัวละคร
    Image playerImage;

    // รูปบ้าน
    Image[] houseImages = new Image[6];

    // เก็บรูปผักแต่ละชนิด แยกตามระยะเติบโต
    java.util.Map<String, Image[]> plantImages = new java.util.HashMap<>();

    // ผู้เล่น (เงิน + กระเป๋า) และของที่ร้านขาย ใช้กับปุ่ม B (ร้าน) / H (กระเป๋า)
    Player player = new Player(150);

    java.util.List<Item> stock = Arrays.asList(
        new Item("Carrot Seed", 20, 0),
        new Item("Potato Seed", 15, 0),
        new Item("Tomato Seed", 30, 0),
        new Item("Pumpkin Seed", 45, 0),
        new Item("Corn Seed", 25, 0)
    );
    Item selectedSeed = null;

    // ===== ร้านค้า (ตึกอยู่มุมล่างขวา ปลายถนน) =====
    // ต้องยืนติดตึกร้านถึงจะกด B เปิดร้านได้
    final int SHOP_COL = 10;   // คอลัมน์ซ้ายสุดของตึก
    final int SHOP_ROW = 10;  // แถวบนสุดของตึก
    final int SHOP_W = 2;     // กว้าง (ช่อง)
    final int SHOP_H = 2;     // สูง (ช่อง) รูปร้านเป็นสี่เหลี่ยมจัตุรัส เลยใช้ 3x3
    // ===== บ้าน (มุมซ้ายบน) ต้องยืนที่บ้านถึงจะกด N นอนได้ =====
    final int HOUSE_COL = 0;
    final int HOUSE_ROW = 0;
    final int HOUSE_W = 3;
    final int HOUSE_H = 2;

    Image shopImage;          // ถ้ามีไฟล์ picture/shop.png จะใช้รูปนี้ ไม่มีจะวาดร้านให้เอง
    java.util.Map<String, Image> shopIcons = new java.util.HashMap<>();   // รูปสินค้าในร้าน
    String message = "";      // ข้อความเตือนที่แถบ UI
    javax.swing.Timer messageTimer;

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


        // สร้างถนน
        createroad();


        // สร้างพื้นที่สำหรับวาดเกม
        roadPanel roadPanel = new roadPanel();

        add(roadPanel);


        // โหลดรูปตัวละคร
        playerImage = new ImageIcon("minipro.software/picture/character.png").getImage();

        loadPlantImages("Carrot", "Carrot");
        loadPlantImages("Potato", "Potato");
        loadPlantImages("Tomato", "Tomato");
        loadPlantImages("Pumpkin", "Pumpkin");
        loadPlantImages("Corn", "Corn");

        for (int i = 0; i < 6; i++) {
            houseImages[i] = new ImageIcon("minipro.software/picture/house/" + (i + 1) + ".png").getImage();
        }

        // โหลดรูปร้าน (ไม่บังคับ) + รูปสินค้าในร้าน
        java.io.File shopFile = new java.io.File("minipro.software/picture/shop.png");
        if (shopFile.exists()) {
            shopImage = new ImageIcon(shopFile.getPath()).getImage();
        }
        for (String crop : new String[]{"Carrot", "Potato", "Tomato", "Pumpkin", "Corn"}) {
            Image[] imgs = plantImages.get(crop);
            shopIcons.put(crop + " Seed", imgs[0]);   // เมล็ด = รูประยะที่ 1
            shopIcons.put(crop, imgs[3]);             // ผลผลิต = รูประยะที่ 4
        }

        // ปรับขนาดหน้าต่าง
        pack();

        setLocationRelativeTo(null);

        setResizable(false);

       

        // B = เปิดร้านซื้อของ, H = เปิดกระเป๋า
        GameHotkeys.install(getRootPane(), player, this.stock, shopIcons,
                this::isNearShop,
                () -> showMessage("Go stand next to the SHOP to buy! (B)"));

        

        setVisible(true);

        // ให้หน้าต่างรับปุ่มทันที
        requestFocusInWindow();
    }

    // โหลดภาพพืช 4 ระยะ
    private void loadPlantImages(String name, String folder) {
        Image[] images = new Image[4];

        for (int i = 0; i < 4; i++) {
           String path = "minipro.software/picture/Plants/"+ folder + "/" + (i + 1) + ".png";

            images[i] = new ImageIcon(path).getImage();
        }

        plantImages.put(name, images);
    }

    // ช่องที่เป็นตัวตึกร้าน (เดินทะลุไม่ได้)
    private boolean isShopTile(int x, int y) {
        int col = x / Grass;
        int row = y / Grass;
        return col >= SHOP_COL && col < SHOP_COL + SHOP_W
            && row >= SHOP_ROW && row < SHOP_ROW + SHOP_H;
    }

    // ยืนติดร้านหรือยัง (ช่องที่ติดตึก รวมแนวทแยง)
    private boolean isNearShop() {
        int col = playerX / Grass;
        int row = playerY / Grass;
        return col >= SHOP_COL - 1 && col <= SHOP_COL + SHOP_W
            && row >= SHOP_ROW - 1 && row <= SHOP_ROW + SHOP_H;
    }

    // ยืนที่บ้านหรือยัง (ในตัวบ้านหรือช่องที่ติดบ้าน)
    private boolean isNearHouse() {
        int col = playerX / Grass;
        int row = playerY / Grass;
        return col <= HOUSE_COL + HOUSE_W && row <= HOUSE_ROW + HOUSE_H;
    }

    // โชว์ข้อความที่แถบ UI 2 วินาที
    private void showMessage(String text) {
        message = text;
        if (messageTimer != null) messageTimer.stop();
        messageTimer = new javax.swing.Timer(2000, e -> { message = ""; repaint(); });
        messageTimer.setRepeats(false);
        messageTimer.start();
        repaint();
    }

    // วาดตึกร้านค้า
    private void drawShop(Graphics g, Component obs) {
        int x = SHOP_COL * Grass;
        int y = SHOP_ROW * Grass;
        int w = SHOP_W * Grass;
        int h = SHOP_H * Grass;

        if (shopImage != null) {
            g.drawImage(shopImage, x, y, w, h, obs);
            return;
        }

        // ไม่มีรูป -> วาดร้านง่าย ๆ
        g.setColor(new Color(196, 148, 92));          // ตัวร้าน
        g.fillRect(x, y + 24, w, h - 24);
        g.setColor(new Color(178, 58, 58));           // หลังคา
        g.fillRect(x - 4, y, w + 8, 28);
        g.setColor(Color.WHITE);
        for (int i = 0; i < w + 8; i += 24) {         // ลายหลังคา
            g.fillRect(x - 4 + i, y, 12, 28);
        }
        g.setColor(new Color(90, 60, 30));            // ประตู
        g.fillRect(x + w / 2 - 14, y + h - 36, 28, 36);
        g.setColor(new Color(60, 40, 20));            // ป้าย
        g.fillRect(x + 8, y + 34, 44, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("SHOP", x + 12, y + 47);
        g.setColor(Color.BLACK);
        g.drawRect(x, y + 24, w, h - 24);
    }

    // ตรวจสอบและใช้พลังงาน
    private boolean useEnergy(int amount) {
        if (amount < 0 || energy < amount) {
            System.out.println("พลังงานไม่พอ! กด N เพื่อพักผ่อน");
            return false;
        }

        energy -= amount;
        return true;
    }

    

    // ฟื้นฟูพลังงานเต็ม
    private void restoreEnergy() {
        energy = maxEnergy;
        System.out.println("ฟื้นฟูพลังงานเต็มแล้ว!");
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


    // JPanel สำหรับวาดเกม
    class roadPanel extends JPanel {

        public roadPanel() {
            setPreferredSize(new Dimension(COLS * Grass,ROWS * Grass + 100));
        }


        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);


            // วาด 15 x 15 ช่อง
            for (int row = 0; row < ROWS; row++) {

                for (int col = 0; col < COLS; col++) {

                    int x = col * Grass;
                    int y = row * Grass;

                    // =========================
                    // ช่องที่ถูกขุดแล้ว
                    // =========================

                    if (tilled[row][col]) {
                        // ดิน
                        g.setColor(new Color(139, 93, 59) );
                    }

                    // =========================
                    // ช่องหญ้า
                    // =========================

                    else if (road[row][col] == 0) {
                        // สีเขียว
                        g.setColor(new Color(116, 195, 101));
                    }

                    // =========================
                    // ช่องถนน
                    // =========================

                    else if (road[row][col] == 1) {
                        // สีน้ำตาล
                        g.setColor(new Color(110, 80, 60));
                    }

                    // วาดช่อง
                    g.fillRect(x,y,Grass,Grass);

                    // เส้นแบ่งช่อง
                    g.setColor(
                        new Color(0, 0, 0, 0)
                    );
                    // ===== วาดบ้านมุมซ้ายบน =====
                    int houseCol = 0;
                    int houseRow = 0;

                    for (int i = 0; i < 6; i++) {
                        int col2 = i % 3;
                        int row2 = i / 3;

                        g.drawImage(houseImages[i],(houseCol + col2) * Grass,(houseRow + row2) * Grass,Grass,Grass,this);
                    }
            
                    Crop crop = crops[row][col];

                    if (crop != null) {
                        String name = crop.getName();
                        Image[] images = plantImages.get(name);

                        if (images != null) {
                            int stage = crop.getStage(currentDay);

                            // ป้องกัน index เกินขอบเขต
                            stage = Math.max(1, Math.min(stage, images.length));

                            Image image = images[stage - 1];

                            g.drawImage(image,x, y, Grass, Grass,this);
                        }
                    }


                    g.drawRect(x,y,Grass,Grass);
                }
            }

            // ========================
            // วาดตัวละคร
            // ========================
            drawShop(g, this);
            g.drawImage(playerImage,playerX,playerY,40,40,this);

            // ===== แถบ UI ด้านล่าง =====
            int uiY = ROWS * Grass;

            // พื้นหลังแถบ UI
            g.setColor(new Color(235, 235, 220));
            g.fillRect(0, uiY, COLS * Grass, 100);

            // เส้นแบ่งพื้นที่เล่นกับ UI
            g.setColor(Color.DARK_GRAY);
            g.drawLine(0, uiY, COLS * Grass, uiY);

            // แสดงวัน
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Day: " + currentDay, 10, uiY + 22);

            
            // ===== หลอดพลังงานสีเหลือง =====
            int barX = 410;
            int barY = uiY + 8;
            int barWidth = 180;
            int barHeight = 16;

            // พื้นหลังหลอดสีเทา
            g.setColor(Color.GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // พลังงานที่เหลือ สีเหลือง
            g.setColor(Color.YELLOW);
            g.fillRect(barX, barY,barWidth * energy / maxEnergy, barHeight);

            // กรอบหลอด
            g.setColor(Color.BLACK);
            g.drawRect(barX, barY, barWidth, barHeight);

            // ตัวเลขพลังงาน
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Energy: " + energy + " / " + maxEnergy,barX + 85, barY + 32);


            // คำแนะนำปุ่มควบคุม
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("WASD: Move   E: Dig / Harvest", 10, uiY + 45);
            g.drawString("P: Plant   N: Sleep (at house)", 10, uiY + 63);
            g.drawString("H: Inventory   B: Shop (stand next to it)", 10, uiY + 81);

            // ข้อความเตือน
            if (!message.isEmpty()) {
                g.setColor(new Color(180, 30, 30));
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(message, 300, uiY + 75);
            }

        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {

        // ระยะที่เดินต่อ 1 ครั้ง
        int speed = 40;

        // W = ขึ้น
        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (playerY - speed >= 0 && !isShopTile(playerX, playerY - speed)) {
                playerY -= speed;
            }
        }

        // S = ลง
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (playerY + speed <= ROWS * Grass - 40 && !isShopTile(playerX, playerY + speed)) {
                playerY += speed;
            }
        }

        // A = ซ้าย
        if (e.getKeyCode() == KeyEvent.VK_A) {
            if (playerX - speed >= 0 && !isShopTile(playerX - speed, playerY)) {
                playerX -= speed;
            }
        }

        // D = ขวา
        if (e.getKeyCode() == KeyEvent.VK_D) {
            if (playerX + speed <= COLS * Grass - 40 && !isShopTile(playerX + speed, playerY)) {
                playerX += speed;
            }
        }
        /*// Q = รดน้ำผักในช่องที่ตัวละครยืนอยู่
        if (e.getKeyCode() == KeyEvent.VK_Q) {
           int playerCol = playerX / Grass;
           int playerRow = playerY / Grass;
           Crop crop = crops[playerRow][playerCol];
           if (crop == null) {
        System.out.println("ช่องนี้ไม่มีผักให้รดน้ำ!");
        } else {
        crop.water(currentDay);
        System.out.println("รดน้ำ " + crop.getName() + " แล้ว!");
        }
        }*/

        // เลือกเมล็ดที่จะปลูก
        if (e.getKeyCode() == KeyEvent.VK_1) {
            selectedSeed = stock.get(0);
            System.out.println("เลือกแครอท");
        }
        else if (e.getKeyCode() == KeyEvent.VK_2) {
            selectedSeed = stock.get(1);
            System.out.println("เลือกมันฝรั่ง");
        }
        else if (e.getKeyCode() == KeyEvent.VK_3) {
            selectedSeed = stock.get(2);
            System.out.println("เลือกมะเขือเทศ");
        }
        else if (e.getKeyCode() == KeyEvent.VK_4) {
            selectedSeed = stock.get(3);
            System.out.println("เลือกฟักทอง");
        }
        else if (e.getKeyCode() == KeyEvent.VK_5) {
            selectedSeed = stock.get(4);
            System.out.println("เลือกข้าวโพด");
        }
        
        // P = ปลูกผักตามเมล็ดที่เลือก
        if (e.getKeyCode() == KeyEvent.VK_P) {
            int playerCol = playerX / Grass;
            int playerRow = playerY / Grass;

            if (road[playerRow][playerCol] != 0) {
                System.out.println("ปลูกบนถนนไม่ได้!");
            } else if (!tilled[playerRow][playerCol]) {
                System.out.println("ต้องขุดดินก่อน!");
            } else if (crops[playerRow][playerCol] != null) {
                System.out.println("ช่องนี้มีผักอยู่แล้ว!");
            } else if (selectedSeed == null) {
                System.out.println("กด 1-5 เพื่อเลือกเมล็ดก่อน!");
            } else if (player.getInventory().getCount(selectedSeed) <= 0) {
                System.out.println("เมล็ดไม่พอ! ไปซื้อที่ร้านก่อน");
            } else {
                String cropName;
                int growDays;

                switch (selectedSeed.getName()) {
                    case "Carrot Seed":
                        cropName = "Carrot";
                        growDays = 3;
                        break;
                    case "Potato Seed":
                        cropName = "Potato";
                        growDays = 4;
                        break;
                    case "Tomato Seed":
                        cropName = "Tomato";
                        growDays = 5;
                        break;
                    case "Pumpkin Seed":
                        cropName = "Pumpkin";
                        growDays = 6;
                        break;
                    case "Corn Seed":
                        cropName = "Corn";
                        growDays = 4;
                        break;
                    default:
                        System.out.println("ไม่รู้จักเมล็ดชนิดนี้");
                        return;
                }

                if (energy < 3) {
                    System.out.println("พลังงานไม่พอ! กด N เพื่อพักผ่อน");
                } else if (player.getInventory().removeItem(selectedSeed, 1)) {
                    useEnergy(3);

                    crops[playerRow][playerCol] = new Crop(cropName, currentDay, growDays);

                    System.out.println("ปลูก " + cropName + " แล้ว!");
                }

                repaint();
            }
        }
        // =========================
        // E = เก็บเกี่ยว หรือขุดดิน
        // =========================
        if (e.getKeyCode() == KeyEvent.VK_E) {

            int playerCol = playerX / Grass;
            int playerRow = playerY / Grass;

            if (playerRow >= 0 && playerRow < ROWS
                    && playerCol >= 0 && playerCol < COLS) {

                // ถ้าเป็นถนน ทำอะไรไม่ได้
                if (road[playerRow][playerCol] != 0) {
                    System.out.println("ตรงนี้ทำการเกษตรไม่ได้!");
                }

                // ถ้ามีพืชอยู่ ให้ลองเก็บเกี่ยวก่อน
                else if (crops[playerRow][playerCol] != null) {

                    Crop crop = crops[playerRow][playerCol];

                    // ตรวจสอบว่าพืชโตเต็มที่หรือยัง
                    if (crop.isReadyToHarvest(currentDay)) {
                         if (energy < 2) {
                            System.out.println("พลังงานไม่พอ! กด N เพื่อพักผ่อน");
                        } else {
                            useEnergy(2);
                        
                        String cropName = crop.getName();

                        // กำหนดราคาขายผลผลิต
                        int sellPrice;

                        switch (cropName) {
                            case "Carrot": sellPrice = 10; break;
                            case "Potato": sellPrice = 12; break;
                            case "Tomato": sellPrice = 15; break;
                            case "Pumpkin": sellPrice = 22; break;
                            case "Corn": sellPrice = 12; break;
                            default: sellPrice = 5;
                        }

                        // เพิ่มผลผลิตเข้า Inventory
                        Item produce = new Item(cropName, 0, sellPrice);
                        player.getInventory().addItem(produce, 1);

                        // นำพืชออกจากแปลง
                        crops[playerRow][playerCol] = null;

                        System.out.println("เก็บเกี่ยว " + cropName + " สำเร็จ!");
                        System.out.println("นำผลผลิตเข้ากระเป๋าแล้ว");

                        repaint();

                        }
                    } else {
                        System.out.println("ผักยังโตไม่เต็มที่!");
                    }
                }

                // ถ้าไม่มีพืชและยังไม่ได้ขุด ให้ขุดดิน
                else if (!tilled[playerRow][playerCol]) {
                    if (useEnergy(5)){
                    tilled[playerRow][playerCol] = true;
                    System.out.println("ขุดดินแล้ว! กด P เพื่อปลูกผัก");
                    }
                }

                // ถ้าขุดดินแล้วแต่ไม่มีพืช
                else {
                    System.out.println("ช่องนี้ขุดแล้ว สามารถกด P เพื่อปลูกผักได้");
                }
            }
        }

        // N = นอน
        if (e.getKeyCode() == KeyEvent.VK_N) {
            if (!isNearHouse()) {
                showMessage("Go to your HOUSE to sleep! (N)");
            } else {
                currentDay++;
                restoreEnergy();
                System.out.println("วันที่ " + currentDay);
            }
        }

        // วาดใหม่
        repaint();
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
// ป้องกันการเก็บเกี่ยวเมื่อกระเป๋าเต็ม
/*Item produce = new Item(cropName, 0, sellPrice);

if (!player.getInventory().canAddItem(produce, 1)) {
    System.out.println("กระเป๋าเต็ม! ขายของบางส่วนก่อน");
} else if (energy < 2) {
    System.out.println("พลังงานไม่พอ! กด N เพื่อพักผ่อน");
} else {
    useEnergy(2);
    player.getInventory().addItem(produce, 1);

    crops[playerRow][playerCol] = null;

    System.out.println("เก็บเกี่ยว " + cropName + " สำเร็จ!");
    System.out.println("นำผลผลิตเข้ากระเป๋าแล้ว");

    repaint();
}
 */
