import java.awt.*;
import java.io.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.*;

public class Nameinput extends JFrame  {

    public  Nameinput() {
       setTitle("GAGAR" +" "+ "Dening");
        setSize(1024, 800);
        ImageIcon icon = new ImageIcon("minipro.software/picture/Piture.jpg");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        JTextField inputname = new JTextField();
        ((AbstractDocument) inputname.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void replace(FilterBypass fb, int offset,int length, String text, AttributeSet attrs)
                throws BadLocationException {
                    String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String newText = oldText.substring(0, offset) 
                    + (text == null ? " " : text)
                    + oldText.substring(offset + length);

            if (newText.matches("[A-Z a-z]{0,10}")) {
                super.replace(fb, offset, length, text, attrs);
            }
         }
        });
        inputname.setBounds(350,300,300,50);
        inputname.setFont(new Font("Monospaced", Font.BOLD, 20));
        add(inputname);

        JLabel NameLabel = new JLabel("CHARACTER"+" "+"NAME");
        NameLabel.setBounds(400,250,300,50);
        NameLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        add(NameLabel);

        JButton Submit = new JButton("SUBMIT");
        Submit.setBounds(450, 370, 100, 50);
        Submit.setFont(new Font("Monospaced", Font.BOLD, 15));
        
        //การกดปุ่ม submit
        Submit.addActionListener(e -> {
        String name = inputname.getText();
        if (name.trim().isEmpty()) {
        JOptionPane.showMessageDialog(Submit, "Input" + " " + "Character" + " " + "Name");
        return;
        }
        try {
            FileWriter writer = new FileWriter("PlayerName.txt");
            writer.write(name);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

