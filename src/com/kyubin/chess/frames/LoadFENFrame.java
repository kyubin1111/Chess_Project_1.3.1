package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.chessengine.EngineType;
import com.kyubin.chess.frames.chessengine.engines.KomodoEngine;
import com.kyubin.chess.frames.chessengine.engines.LeelaChessZero;
import com.kyubin.chess.frames.chessengine.engines.StockFishEngine;
import com.kyubin.chess.frames.chessengine.engines.custom.CustomEngine;
import com.kyubin.chess.functions.move.analysis.convert.GetFEN;
import com.kyubin.chess.functions.move.analysis.convert.LoadFENFailedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static com.kyubin.chess.Main.*;
import static com.kyubin.chess.frames.chessengine.Engine.runningProcess;

public class LoadFENFrame extends JFrame{
    public LoadFENFrame(int x_size,int y_size){
        setSize(x_size,y_size);
        setLocation(1000, Main.stockfish_y_size+50+Main.undo_y_size+Main.flipboard_y_size+50);
        setResizable(true);
        setLayout(null);

        Container c = getContentPane();

        JTextArea fen_label = new JTextArea("FEN Here");
        fen_label.setSize(10000,15);
        fen_label.setLocation(0,0);

        JButton ok_button = new JButton("OK");
        ok_button.setSize(100,50);
        ok_button.setLocation(0,30);
        ok_button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chessGame.removeAll();

                try {
                    GetFEN.parseFEN(fen_label.getText(),frame.getContentPane(),chessGame);
                } catch (RuntimeException | LoadFENFailedException ex) {
                    throw new RuntimeException(ex);
                }
                chessGame.allUp();
                Main.is_startPos=false;

                FEN=fen_label.getText();

                Main.engine_mode=false;
                ProcessBuilder processBuilder = new ProcessBuilder(stockfish_file);
                Process stockfishProcess;
                try {
                    stockfishProcess = processBuilder.start();
                } catch (IOException q) {
                    throw new RuntimeException(q);
                }

                // 현재 실행 중인 프로세스를 runningProcess에 저장
                Process existingProcess = runningProcess.getAndSet(stockfishProcess);
                if (existingProcess != null) {
                    existingProcess.destroyForcibly();
                    try {
                        existingProcess.waitFor();
                    } catch (InterruptedException q) {
                        Thread.currentThread().interrupt();
                    }
                }

                if(Main.engine_mode) {
                    new Thread(()->{
                        if(Main.analysis_engine_type== EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(!chessGame.white_turn);
                        if(Main.analysis_engine_type==EngineType.KOMODO) KomodoEngine.getKomodoChoice(!chessGame.white_turn);
                        if(Main.analysis_engine_type==EngineType.LCO) LeelaChessZero.getLC0Choice(!chessGame.white_turn);
                        if(Main.analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(!chessGame.white_turn);
                    }).start();
                }

                setVisible(false);
            }
        });

        c.add(fen_label);
        c.add(ok_button);

        setVisible(true);
    }
}
