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
            houseImages[i] = new ImageIcon("minipro.software/picture/House/" + (i + 1) + ".png").getImage();
        }

        // ปรับขนาดหน้าต่าง
        pack();

        setLocationRelativeTo(null);

        setResizable(false);

       

        // B = เปิดร้านซื้อของ, H = เปิดกระเป๋า
        GameHotkeys.install(getRootPane(), player, this.stock);

        

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
            g.drawString("P: Plant   N: Next Day", 10, uiY + 63);
            g.drawString("H: Inventory   B: Shop", 10, uiY + 81);

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
            if (playerY - speed >= 0) {
                playerY -= speed;
            }
        }

        // S = ลง
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (playerY + speed <= ROWS * Grass - 40) {
                playerY += speed;
            }
        }

        // A = ซ้าย
        if (e.getKeyCode() == KeyEvent.VK_A) {
            if (playerX - speed >= 0) {
                playerX -= speed;
            }
        }

        // D = ขวา
        if (e.getKeyCode() == KeyEvent.VK_D) {
            if (playerX + speed <= COLS * Grass - 40) {
                playerX += speed;
            }
        }

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
            currentDay++;
            restoreEnergy();
            System.out.println("วันที่ " + currentDay);
        }

        // วาดใหม่
        repaint();
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
