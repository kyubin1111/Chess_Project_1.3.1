package com.kyubin.chess.frames;

import com.kyubin.chess.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GetStringValueFrame extends JFrame{
    public String result=null;
    public boolean stop=false;

    public void getIntValueFrame(int x_size,int y_size,String information){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+50+Main.undo_y_size+Main.flipboard_y_size+50);
        setResizable(true);
        setLayout(null);

        Container c = getContentPane();

        JTextArea fen_label = new JTextArea(information);
        fen_label.setSize(10000,15);
        fen_label.setLocation(0,0);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // 윈도우 리스너 추가
        addWindowListener(new WindowAdapter() {
            // 윈도우가 닫힐 때 호출되는 메소드
            public void windowClosing(WindowEvent e) {
                stop=true;
                setVisible(false);
            }
        });

        JButton ok_button = new JButton("OK");
        ok_button.setSize(100,50);
        ok_button.setLocation(0,30);
        ok_button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result=fen_label.getText();

                setVisible(false);
            }
        });

        c.add(fen_label);
        c.add(ok_button);

        setVisible(true);
    }
}
