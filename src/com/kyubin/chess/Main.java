package com.kyubin.chess;

import com.kyubin.chess.frames.*;
import com.kyubin.chess.frames.chessengine.Engine;
import com.kyubin.chess.frames.chessengine.EngineType;
import com.kyubin.chess.frames.chessengine.engines.KomodoEngine;
import com.kyubin.chess.frames.chessengine.engines.LeelaChessZero;
import com.kyubin.chess.frames.chessengine.engines.custom.CustomEngine;
import com.kyubin.chess.functions.move.analysis.MoveLAN;
import com.kyubin.chess.functions.move.analysis.convert.GetFEN;
import com.kyubin.chess.frames.chessengine.engines.StockFishEngine;
import com.kyubin.chess.functions.BasicFunctions;
import com.kyubin.chess.functions.move.analysis.convert.LoadFENFailedException;
import com.kyubin.chess.game.ChessGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;

import static com.kyubin.chess.frames.chessengine.Engine.runningProcess;

public class Main {
    public static int size=900;
    public static int stockfish_x_size=300;
    public static int stockfish_y_size=400;
    public static int undo_x_size=100;
    public static int undo_y_size=100;
    public static int flipboard_x_size=100;
    public static int flipboard_y_size=100;
    public static boolean engine_mode=false;
    public static int depth=13;
    public static int analysis_chessengine_depth=20;
    public static int multiPV=3;//1~5
    public static boolean analysis_chessengine_is_depth_inf=true;
    public static boolean is_flip_board=false;
    public static boolean is_startPos=true;
    public static String FEN;
    public static EngineType analysis_engine_type=EngineType.STOCKFISH;
    public static JLabel boardImage;

    public static final boolean is_convertMove=true;
    public static final boolean no_set=false;
    public static HeightOverlayChartFrame chart=new HeightOverlayChartFrame();


    //오브젝트 이미지 경로
    public static final String r_w="src/res/r_w.png";
    public static final String n_w="src/res/n_w.png";
    public static final String b_w="src/res/b_w.png";
    public static final String q_w="src/res/q_w.png";
    public static final String k_w="src/res/k_w.png";
    public static final String p_w="src/res/p_w.png";
    public static final String r_b="src/res/r_b.png";
    public static final String n_b="src/res/n_b.png";
    public static final String b_b="src/res/b_b.png";
    public static final String q_b="src/res/q_b.png";
    public static final String k_b="src/res/k_b.png";
    public static final String p_b="src/res/p_b.png";
    public static final String board="src/res/chess_board.png";
    public static final String flip_board="src/res/chess_board.png";
    public static final String cancel="src/res/cancel.png";
    public static final String chess_icon="src/res/chess_icon.png";

    public final static String stockfish_file="uci_chess_engine/stockfish/stockfish.exe";
    public final static String komodo_file="uci_chess_engine/komodo/komodo-14.1-64bit.exe";
    public final static String lc0_file="uci_chess_engine/LC0/lc0.exe";
    public static String custom_engine_file;

    // 한 칸의 크기 계산
    public static int square_size=(int) Math.round(size/8.);

    public static JFrame frame = new JFrame("Chess");

    public static ChessGame chessGame = new ChessGame(frame);

    public static int chess_object_size;

    public static JTextPane pv1;
    public static JTextPane pv2;
    public static JTextPane pv3;
    public static JTextPane pv4;
    public static JTextPane pv5;

    public static long wait=0;

    public static void main(String[] args) throws IOException, InterruptedException, LoadFENFailedException {
        try {
            if(args[0]!=null){
                is_startPos=false;
                FEN=args[0];
            }
        } catch (IndexOutOfBoundsException ignored){
        }

        Chess();
    }

    public static void EngineFrame(int pvRepeat,int pvRepeatUse) throws IOException {
        if(pvRepeatUse==0){
            return;
        }
        JFrame jFrame = new JFrame("Engine Choice PV "+(pvRepeat - pvRepeatUse+1));
        jFrame.setSize(stockfish_x_size, stockfish_y_size);
        jFrame.setLocation(size+100+(((pvRepeat - pvRepeatUse+1)-1)*stockfish_x_size),0);
        jFrame.setResizable(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jFrame.setUndecorated(true); // 기본 제목 표시줄 제거
        jFrame.getContentPane().setBackground(Color.WHITE); // 배경색 설정

        // 사용자 정의 제목 표시줄
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel();
        titlePanel.add(titleLabel);

        jFrame.add(titlePanel, BorderLayout.NORTH);

        if(pvRepeatUse==pvRepeat) {
            pv1 = new JTextPane(); // JTextPane 초기화
            pv2 = new JTextPane(); // JTextPane 초기화
            pv3 = new JTextPane(); // JTextPane 초기화
            pv4 = new JTextPane(); // JTextPane 초기화
            pv5 = new JTextPane(); // JTextPane 초기화
        }

        pv1.setEditable(false);
        pv2.setEditable(false);
        pv3.setEditable(false);
        pv4.setEditable(false);
        pv5.setEditable(false);

        if((pvRepeat - pvRepeatUse+1)==1) {
            JScrollPane scrollPane1 = new JScrollPane(pv1); // JScrollPane에 JTextPane 추가
            jFrame.getContentPane().add(scrollPane1); // 프레임에 JScrollPane 추가
        } else if((pvRepeat - pvRepeatUse+1)==2){
            JScrollPane scrollPane2 = new JScrollPane(pv2); // JScrollPane에 JTextPane 추가
            jFrame.getContentPane().add(scrollPane2); // 프레임에 JScrollPane 추가
        } else if((pvRepeat - pvRepeatUse+1)==3){
            JScrollPane scrollPane3 = new JScrollPane(pv3); // JScrollPane에 JTextPane 추가
            jFrame.getContentPane().add(scrollPane3); // 프레임에 JScrollPane 추가
        } else if((pvRepeat - pvRepeatUse+1)==4){
            JScrollPane scrollPane4 = new JScrollPane(pv4); // JScrollPane에 JTextPane 추가
            jFrame.getContentPane().add(scrollPane4); // 프레임에 JScrollPane 추가
        } else if((pvRepeat - pvRepeatUse+1)==5){
            JScrollPane scrollPane5 = new JScrollPane(pv5); // JScrollPane에 JTextPane 추가
            jFrame.getContentPane().add(scrollPane5); // 프레임에 JScrollPane 추가
        }

        jFrame.setVisible(true);

        EngineFrame(pvRepeat,pvRepeatUse-1);

        if(pvRepeatUse==1&&engine_mode) {
            if (analysis_engine_type == EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(false);
            if (analysis_engine_type == EngineType.KOMODO) KomodoEngine.getKomodoChoice(false);
            if (analysis_engine_type == EngineType.LCO) LeelaChessZero.getLC0Choice(false);
            if (analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(false);
        }
    }

    public static void Chess() throws LoadFENFailedException, InterruptedException, IOException {
        Locale.setDefault(new Locale("en", "EN"));

        // 리소스 번들 로드
        ResourceBundle messages = ResourceBundle.getBundle("res.messages");

        chess_object_size=square_size;

        // 프레임 세팅
        frame.setSize(square_size*8+16,square_size*8+36+20);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        ImageIcon icon = new ImageIcon(chess_icon);

        frame.setIconImage(icon.getImage());

        MenuBar menuBar=new MenuBar();

        Menu engine = new Menu(messages.getString("engineMenu"));

        MenuItem stockfish = new MenuItem("Set Engine Stockfish...");
        stockfish.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine_mode=true;
                analysis_engine_type=EngineType.STOCKFISH;
                StockFishEngine.getStockFishChoice(!chessGame.white_turn);
            }
        });

        MenuItem komodo = new MenuItem("Set Engine Komodo...");
        komodo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine_mode=true;
                analysis_engine_type=EngineType.KOMODO;
                KomodoEngine.getKomodoChoice(!chessGame.white_turn);
            }
        });

        MenuItem lc0 = new MenuItem("Set Engine LC0...");
        lc0.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine_mode=true;
                analysis_engine_type=EngineType.LCO;
                LeelaChessZero.getLC0Choice(!chessGame.white_turn);
            }
        });

        MenuItem set_an_depth = new MenuItem("Set Engine Depth...");
        set_an_depth.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GetStringValueFrame depthFrame=new GetStringValueFrame();
                depthFrame.getIntValueFrame(300,300,"Engine Depth Here(Infinity=\"inf\",\"infinity\")");

                new Thread(()->{
                    while(true){
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        if(depthFrame.stop){
                            depthFrame.stop=false;
                            break;
                        }
                        if(depthFrame.result!=null){
                            if(depthFrame.result.equalsIgnoreCase("inf")|| depthFrame.result.equalsIgnoreCase("infinity")) {
                                analysis_chessengine_is_depth_inf=true;
                                break;
                            }
                            analysis_chessengine_is_depth_inf=false;
                            analysis_chessengine_depth=Integer.parseInt(depthFrame.result);
                            break;
                        }
                    }
                }).start();
            }
        });

        MenuItem custom = new MenuItem("Set Engine Custom...");
        custom.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine_mode=true;
                analysis_engine_type=EngineType.CUSTOM;
                new CustomEngineFrame(300,300);
            }
        });

        MenuItem stop_a = new MenuItem("Set Engine Stop...");
        stop_a.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine_mode=false;
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

                pv1.setText("");
                pv2.setText("");
                pv3.setText("");
                pv4.setText("");
                pv5.setText("");
            }
        });

        engine.add(stockfish);
        engine.add(komodo);
        engine.add(lc0);
        engine.add(set_an_depth);
        engine.add(custom);
        engine.add(stop_a);

        Menu file = new Menu(messages.getString("fileMenu"));

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        MenuItem load_FEN = new MenuItem("Load FEN...");
        load_FEN.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoadFENFrame(300,300);
            }
        });

        file.add(exit);
        file.add(load_FEN);

        menuBar.add(engine);
        menuBar.add(file);

        frame.setMenuBar(menuBar);

        if (is_startPos) {
            chessGame.setPositionStartPos(frame.getContentPane());
        } else {
            GetFEN.parseFEN(FEN,frame.getContentPane(),chessGame);
        }

        // 체스 보드 불러오기
        boardImage=BasicFunctions.loadImage(0,0,square_size*8,square_size*8,square_size*8,square_size*8,board);

        new Thread(()->{
            try {
                EngineFrame(multiPV,multiPV);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Score Chart");

            frame.setUndecorated(true);
            frame.getContentPane().setBackground(Color.WHITE);

            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel titleLabel = new JLabel();
            titlePanel.add(titleLabel);

            frame.add(titlePanel, BorderLayout.NORTH);

            frame.add(chart);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            frame.setSize(100, (int) (900/1.5));
            frame.setLocation(size+undo_x_size+200,stockfish_y_size+10);
            frame.setResizable(false);

            chart.setHeight(frame.getHeight());
            chart.setWidth(frame.getWidth());

            chart.setValue(0,false,false);
        });

        new Thread(()->{
            System.out.println("Timer Started.");
            while (true){
                GoBestMoveFrame.time++;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new UndoFrame(undo_x_size,undo_y_size);
        new FlipBoardFrame(flipboard_x_size,flipboard_y_size);
        new GoBestMoveFrame(100,100);
        new RedoFrame(100,100);
    }
}