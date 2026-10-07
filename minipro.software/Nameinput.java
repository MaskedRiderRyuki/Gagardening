import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class Nameinput extends JFrame  {

    public  Nameinput() {
       setTitle("GAGAR" +" "+ "Dening");
        setSize(1024, 800);
        ImageIcon icon = new ImageIcon("Picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        

        JTextField inputname = new JTextField();
        inputname.setBounds(350,300,300,50);
        inputname.setFont(new Font("Monospaced", Font.BOLD, 20));
        add(inputname);

        JLabel NameLabel = new JLabel("CARACTER"+" "+"NAME");
        NameLabel.setBounds(400,250,300,50);
        NameLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        add(NameLabel);

        JButton Submit = new JButton("SUBMIT");
        Submit.setBounds(450, 370, 100, 50);
        Submit.setFont(new Font("Monospaced", Font.BOLD, 15));
        //การกดปุ่ม submit
        Submit.addActionListener(e -> {
          String name = inputname.getText();
          if(name.trim().isEmpty()){JOptionPane.showMessageDialog(Submit, "My"+" "+"Character"+" "+"Name");
          return;
          }
          new Gamepanel();
          dispose();
        });
        add(Submit);

        ImageIcon blackgroudgame2 = new ImageIcon("minipro.software/picture/blackgroudgame2.jpg");//พื้นหลัง
        JLabel background = new JLabel(blackgroudgame2);
        background.setBounds(0, 0, 1024, 800);
        add(background);
         setVisible(true);
       }
}

