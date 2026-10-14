import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class HowPlay extends JFrame implements ActionListener{
    public HowPlay() {
        setTitle("GAGAR" +" "+ "Dening");
        setSize(250, 250);
        ImageIcon icon = new ImageIcon("minipro.software/picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel HowPlaysettingLabel = new JLabel(" W "+" A "+" S "+" D "+"   "+"ใช้ในการเคลื่อนที่");
        styleLabel(HowPlaysettingLabel);
        HowPlaysettingLabel.setBounds(20,20 ,250 ,20 );
        add(HowPlaysettingLabel);

        JLabel HowPlaysettingLabel2 = new JLabel(" E "+ " พรวนดิน/เก็บผลผลิต ");
        styleLabel(HowPlaysettingLabel2);
        HowPlaysettingLabel2.setBounds(30,40 ,250 ,20 );
        add(HowPlaysettingLabel2);

        JLabel HowPlaysettingLabel3 = new JLabel(" P  " + " ปลูก ");
        styleLabel(HowPlaysettingLabel3);
        HowPlaysettingLabel3.setBounds(30,60 ,250 ,20 );
        add(HowPlaysettingLabel3);

        JLabel HowPlaysettingLabel4 = new JLabel(" N  " + " นอนและไปวันถัดไป/ฟื้นพลัง");
        styleLabel(HowPlaysettingLabel4);
        HowPlaysettingLabel4.setBounds(30,80 ,250 ,20 );
        add(HowPlaysettingLabel4);

        JLabel HowPlaysettingLabel5 = new JLabel(" H  " + " ช่องเก็บของ ");
        styleLabel(HowPlaysettingLabel5);
        HowPlaysettingLabel5.setBounds(30,100 ,250 ,20 );
        add(HowPlaysettingLabel5);

        JLabel HowPlaysettingLabel6 = new JLabel(" B  " + " ร้านค้า(ต้องอยู่ใกล้)");
        styleLabel(HowPlaysettingLabel6);
        HowPlaysettingLabel6.setBounds(30,120 ,250 ,20 );
        add(HowPlaysettingLabel6);

        JLabel HowPlaysettingLabel7 = new JLabel("1-5  " + " เลือกชนิดเมล็ดที่จะปลูก ");
        styleLabel(HowPlaysettingLabel7);
        HowPlaysettingLabel7.setBounds(30,140 ,250 ,20 );
        add(HowPlaysettingLabel7);

        JButton returnButton = new JButton("Return");
        styleButton(returnButton);
        returnButton.setBounds(70, 165, 100,35);
        returnButton.addActionListener(e ->{
            new Menu();
            dispose();
        });
        add(returnButton);

        ImageIcon Settingbg = new ImageIcon("minipro.software/picture/grassbg.jpg");//พื้นหลัง
        JLabel Settingbg1 = new JLabel(Settingbg);
        Settingbg1.setBounds(0, 0, 250, 250);
        add(Settingbg1);
    
        setVisible(true);
    }
     private  void styleButton(JButton button) {
       button.setBackground(new Color(70, 95, 75));
       button.setForeground(Color.black);
       button.setFont(new Font("Monospaced", Font.BOLD, 15));
       button.setFocusPainted(false);
       button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
       button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       button.setOpaque(true);

       Color hoverColor = new Color(95, 125, 95);
       Color normalColor = new Color(70, 95, 75);

       button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(hoverColor);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(normalColor);
        }
      });
    }
    private void styleLabel(JLabel Label){
        Label.setFont(new Font("Tahoma", Font.PLAIN, 14));
        Label.setForeground(Color.black);
        }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    };
    
}
