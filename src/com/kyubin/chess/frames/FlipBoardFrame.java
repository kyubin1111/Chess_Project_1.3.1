package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.BasicFunctions;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.analysis.FlippingBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.kyubin.chess.Main.*;

public class FlipBoardFrame extends JFrame{
    public FlipBoardFrame(int x_size,int y_size){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+Main.undo_y_size+50);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUndecorated(true);
        getContentPane().setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel();
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        JButton undo_button = new JButton("Flip the Board");
        undo_button.setSize(x_size,y_size);
        undo_button.setLocation(0,0);
        undo_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(PieceFunctions.is_promotion) return;
                FlippingBoard.flipBoard();
                if(!is_flip_board){
                    Main.frame.remove(boardImage);

                    boardImage=BasicFunctions.loadImage(0,0,square_size*8,
                            square_size*8,square_size*8,square_size*8,board);
                } else {
                    Main.frame.remove(boardImage);

                    boardImage=BasicFunctions.loadImage(0,0,square_size*8,
                            square_size*8,square_size*8,square_size*8,flip_board);
                }
            }
        });
        getContentPane().add(undo_button);
        setVisible(true);
    }
}
