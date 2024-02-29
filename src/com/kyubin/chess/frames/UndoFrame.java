package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.analysis.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.kyubin.chess.Main.chessGame;

public class UndoFrame extends JFrame {
    public UndoFrame(int x_size,int y_size){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+50);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUndecorated(true);
        getContentPane().setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel();
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        JButton undo_button = new JButton("undo");
        undo_button.setSize(x_size,y_size);
        undo_button.setLocation(0,0);
        undo_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(chessGame.game.size()!=0){
                    if(PieceFunctions.is_promotion) return;
                    Move.UndoPiece(true,chessGame);
                }
            }
        });
        getContentPane().add(undo_button);
        setVisible(true);
    }
}
