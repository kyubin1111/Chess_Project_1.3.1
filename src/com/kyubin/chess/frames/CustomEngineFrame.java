package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.chessengine.engines.custom.CustomEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.kyubin.chess.Main.chessGame;

public class CustomEngineFrame extends JFrame{
    public CustomEngineFrame(int x_size,int y_size){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+50+Main.undo_y_size+Main.flipboard_y_size+50);
        setResizable(true);
        setLayout(null);

        Container c = getContentPane();

        JTextArea fen_label = new JTextArea("Engine File Here");
        fen_label.setSize(10000,15);
        fen_label.setLocation(0,0);

        JButton ok_button = new JButton("OK");
        ok_button.setSize(100,50);
        ok_button.setLocation(0,30);
        ok_button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.custom_engine_file=fen_label.getText();

                CustomEngine.getCustomEngineChoice(!chessGame.white_turn);

                setVisible(false);
            }
        });

        c.add(fen_label);
        c.add(ok_button);

        setVisible(true);
    }
}
