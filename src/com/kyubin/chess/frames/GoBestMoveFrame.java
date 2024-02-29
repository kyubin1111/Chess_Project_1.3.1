package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.chessengine.Engine;
import com.kyubin.chess.functions.move.analysis.MoveLAN;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class GoBestMoveFrame extends JFrame{
    public static boolean is_depth=false;
    public static boolean is_possible=true;
    public static long time=0;
    public static long timeMax=0;

    public GoBestMoveFrame(int x_size,int y_size){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+Main.flipboard_y_size+y_size+50);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUndecorated(true);
        getContentPane().setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel();
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        JButton go_bestMove_button = new JButton("Go BestMove");
        go_bestMove_button.setSize(x_size,y_size);
        go_bestMove_button.setLocation(0,0);
        go_bestMove_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(is_depth){
                    if(is_possible&&time>timeMax&& !Objects.equals(Engine.bestMove, "")){
                        is_possible=false;
                        String engine_move=Engine.getEngineChoiceString(Main.analysis_engine_type,15);

                        Engine.bestMove="";
                        MoveLAN.MovePiece(engine_move,true);
                        is_possible=true;
                    }
                } else {
                    if(is_possible&&time>timeMax&& !Objects.equals(Engine.bestMove, "")) {
                        is_possible=false;
                        if (!Objects.equals(Engine.bestMove, "")) MoveLAN.MovePiece(Engine.bestMove, true);

                        Engine.bestMove="";
                        is_possible=true;
                    }
                }
            }
        });
        getContentPane().add(go_bestMove_button);
        setVisible(true);
    }
}
