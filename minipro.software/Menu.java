
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Menu extends JFrame implements ActionListener {
       public Menu(){
        setTitle("GAGAR" +" "+ "Dening");
        setSize(1024, 800);
        ImageIcon icon = new ImageIcon("Picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(null);

        JLabel NameGameLabel = new JLabel("By"+" "+"Demon"+" "+"Dev.Team");
        NameGameLabel.setBounds(850, 710, 200, 30);
        add(NameGameLabel);

        
        JButton PlayButton = new JButton("Play");//ปุ่มเล่น
        PlayButton.setBackground(new Color(140, 130, 123));//สีปุ่ม
        PlayButton.setForeground(Color.black);
        PlayButton.setFont(new Font("Monospaced", Font.BOLD, 26));//ฟอนต์ ขนาด
        PlayButton.setBounds(450,400,130,50);
        //การกดปุ่ม play
        PlayButton.addActionListener(e -> {
         java.io.File saveFile = new java.io.File("PlayerName.txt");// ตรวจสอบว่ามีไฟล์ตัวละครหรือยัง
         if (saveFile.exists()) {
                new Gamepanel();// มีตัวละครแล้ว → เล่นต่อ
                dispose();//ปิดหน้า menu
            } else {
                new Nameinput();// ยังไม่มีตัวละคร → ไปตั้งชื่อ
                dispose();//ปิดหน้า menu
            }
        });

        add(PlayButton);

        JButton NewGameButton = new JButton("New"+" "+"Game");//ปุ่มเกมใหม่
        NewGameButton.setBackground(new Color(140, 130, 123));//สีปุ่ม
        NewGameButton.setForeground(Color.black);
        NewGameButton.setFont(new Font("Monospaced", Font.BOLD, 20));//ฟอนต์ ขนาด
        NewGameButton.setBounds(450, 500, 130, 50);
        //การกดปุ่ม New Game
        NewGameButton.addActionListener(e ->{
          java.io.File saveFile = new java.io.File("PlayerName.txt");// หาไฟล์ตัวละครเก่า
          if(saveFile.exists()){// ถ้ามีตัวละครเก่า ให้ลบ
            saveFile.delete();
          }
          new Nameinput();// ไปสร้างตัวละครใหม่
          dispose();//ปิดหน้า menu
        });
        add(NewGameButton);

        JButton EixtButton = new JButton("Eixt");//ปุ่มออก
        EixtButton.setBackground(new Color(140,130,123));//สีปุ่ม
        EixtButton.setForeground(Color.black);
        EixtButton.setFont(new Font("Monospaced", Font.BOLD, 26));//ฟอนต์ ขนาด
        EixtButton.setBounds(450, 600, 130, 50);
        //การกดปุ่ม Eixt
        EixtButton.addActionListener(e -> {
            System.exit(0);// ออกจากโปรแกรม
        });
        add(EixtButton);

        ImageIcon icon2 = new ImageIcon("Picture/Gamename.png");//ชื่อเกมหน้าเมนู
        Image img = icon2.getImage();
        Image scaled = img.getScaledInstance(550, 200, Image.SCALE_SMOOTH);//ทำให้รูปพอดีจอ
        JLabel background1 = new JLabel(new ImageIcon(scaled));
        background1.setBounds(220, 100, 550, 200);
        add(background1);

        ImageIcon blackgroudgame2 = new ImageIcon("Picture/blackgroudgame2.jpg");//พื้นหลัง
        JLabel background = new JLabel(blackgroudgame2);
        background.setBounds(0, 0, 1024, 800);
        add(background);
        setVisible(true);
       }

       @Override
       public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
       }
    }

