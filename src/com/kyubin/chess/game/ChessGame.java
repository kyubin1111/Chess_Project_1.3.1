package com.kyubin.chess.game;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.BasicFunctions;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.analysis.convert.GetFEN;
import com.kyubin.chess.functions.move.analysis.convert.LoadFENFailedException;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.kyubin.chess.Main.*;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.toFEN;

public class ChessGame {
    // 기물 백 리스트 생성
    public List<Piece> white_rook = new ArrayList<>();
    public List<Piece> white_knight = new ArrayList<>();
    public List<Piece> white_bishop = new ArrayList<>();
    public List<Piece> white_queen = new ArrayList<>();
    public List<Piece> white_pawn = new ArrayList<>();
    public Piece white_king;

    // 기물 흑 리스트 생성
    public List<Piece> black_rook = new ArrayList<>();
    public List<Piece> black_knight = new ArrayList<>();
    public List<Piece> black_bishop = new ArrayList<>();
    public List<Piece> black_queen = new ArrayList<>();
    public List<Piece> black_pawn = new ArrayList<>();
    public Piece black_king;

    public Stack<MoveTo> future_move=new Stack<>();

    // 게임 기보 저장
    public List<MoveTo> game = new ArrayList<>();

    // 게임 위치 저장
    public List<String> fen_game = new ArrayList<>();
    public List<String> fen_game_no_half_move = new ArrayList<>();

    // 프레임과 체스 기물 크기 받기
    public JFrame frame;

    // 백 차례인지 흑 차례인지 확인
    public boolean white_turn=true;

    public boolean white_is_possible_kingSideCastling=true;
    public boolean white_is_possible_queenSideCastling=true;
    public boolean black_is_possible_kingSideCastling=true;
    public boolean black_is_possible_queenSideCastling=true;

    public String pgn="";

    public Stack<Integer> half_move=new Stack<>();
    public Stack<Integer> full_move=new Stack<>();

    public void removeAll(){
        List<Piece> wp = BasicFunctions.addAllWithPiece(white_rook,white_knight,white_bishop,white_queen,white_pawn,white_king);
        List<Piece> bp = BasicFunctions.addAllWithPiece(black_rook,black_knight,black_bishop,black_queen,black_pawn,black_king);

        for(Piece p : wp){
            PieceFunctions.removePiece(p.xy,p.type,this);
        }
        for(Piece p : bp){
            PieceFunctions.removePiece(p.xy,p.type,this);
        }
    }

    public String getMoveNumber(){
        return white_turn?(full_move.peek()+"."):"";
    }

    public void reload(){
        for(Piece p : BasicFunctions.addAllWithPiece(white_rook,white_knight,white_bishop,white_queen,white_pawn,white_king)){
            p.label.setLocation(chess_object_size*(p.xy.x-1),chess_object_size*(p.xy.y-1));
        }
        for(Piece p : BasicFunctions.addAllWithPiece(black_rook,black_knight,black_bishop,black_queen,black_pawn,black_king)){
            p.label.setLocation(chess_object_size*(p.xy.x-1),chess_object_size*(p.xy.y-1));
        }
        allUp();
    }

    public void allUp(){
        List<Piece> wp = BasicFunctions.addAllWithPiece(white_rook,white_knight,white_bishop,white_queen,white_pawn,white_king);
        List<Piece> bp = BasicFunctions.addAllWithPiece(black_rook,black_knight,black_bishop,black_queen,black_pawn,black_king);

        for(Piece p : wp){
            try {
                // 먼저 컴포넌트가 컨테이너에 추가되었는지 확인
                if (isComponentNotAdded(p.label)) {
                    Main.frame.getContentPane().add(p.label);
                }
                Main.frame.getContentPane().setComponentZOrder(p.label,0);
            } catch (IllegalArgumentException e){
                throw new RuntimeException(e);
            }
        }
        for(Piece p : bp){
            try {
                // 먼저 컴포넌트가 컨테이너에 추가되었는지 확인
                if (isComponentNotAdded(p.label)) {
                    Main.frame.getContentPane().add(p.label);
                }
                Main.frame.getContentPane().setComponentZOrder(p.label,0);
            } catch (IllegalArgumentException e){
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isComponentNotAdded(Component component) {
        Container parent = component.getParent();
        return parent == null || !parent.equals(Main.frame.getContentPane());
    }

    public int getPointChessGame(){
        return
                (white_queen.size()*9+white_pawn.size()+white_knight.size()*3+white_bishop.size()*3
                +white_rook.size()*5)-
                (black_queen.size()*9+black_pawn.size()+black_knight.size()*3+black_bishop.size()*3
                +black_rook.size()*5);
    }

    // 생성자
    public ChessGame(JFrame frame){
        half_move.push(0);
        full_move.push(1);

        this.frame=frame;
    }

    public ChessGame(JFrame frame,String pgn){
        half_move.push(0);
        full_move.push(1);

        this.frame=frame;
        this.pgn=pgn;
    }

    public ChessGame(ChessGame preChessGame){
        try {
            GetFEN.parseFEN(toFEN(preChessGame), new JFrame().getContentPane(), this);
        } catch (LoadFENFailedException e) {
            throw new RuntimeException(e);
        }

        fen_game = new ArrayList<>(preChessGame.fen_game);
        Stack<Integer> full = new Stack<>();
        full.addAll(preChessGame.full_move);
        full_move = full;
        Stack<Integer> half = new Stack<>();
        half.addAll(preChessGame.half_move);
        full_move = half;

        white_turn = preChessGame.white_turn;
    }

    public void setPositionStartPos(Container c){
        Rook(c, true, 1, 8);
        Rook(c, true, 8, 8);
        Rook(c, false, 1, 1);
        Rook(c, false, 8, 1);

        Knight(c, true, 2, 8);
        Knight(c, true, 7, 8);
        Knight(c, false, 2, 1);
        Knight(c, false, 7, 1);

        Bishop(c, true, 3, 8);
        Bishop(c, true, 6, 8);
        Bishop(c, false, 3, 1);
        Bishop(c, false, 6, 1);

        Queen(c, true, 4, 8);
        Queen(c, false, 4, 1);

        Pawn(c, true, 1, 7);
        Pawn(c, true, 2, 7);
        Pawn(c, true, 3, 7);
        Pawn(c, true, 4, 7);
        Pawn(c, true, 5, 7);
        Pawn(c, true, 6, 7);
        Pawn(c, true, 7, 7);
        Pawn(c, true, 8, 7);
        Pawn(c, false, 1, 2);
        Pawn(c, false, 2, 2);
        Pawn(c, false, 3, 2);
        Pawn(c, false, 4, 2);
        Pawn(c, false, 5, 2);
        Pawn(c, false, 6, 2);
        Pawn(c, false, 7, 2);
        Pawn(c, false, 8, 2);

        King(c, true, 5, 8);
        King(c, false, 5, 1);
    }

    // 룩 불러오기
    public void Rook(Container container,boolean white, int x, int y){
        JLabel rook_label;
        if(white){
            //백 룩 이미지 불러오기
            rook_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.5), (int) (chess_object_size / 1.3),
                    r_w);

            rook_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(rook_label,"white_rook",chess_object_size);
                }
            });

            white_rook.add(new Piece(new XY(x,y),rook_label,"white_rook"));
        } else {
            //흑 룩 이미지 불러오기
            rook_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.5), (int) (chess_object_size / 1.3),
                    r_b);

            rook_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(rook_label,"black_rook",chess_object_size);
                }
            });

            black_rook.add(new Piece(new XY(x,y),rook_label,"black_rook"));
        }
        Main.frame.getContentPane().setComponentZOrder(rook_label,0);
        container.add(rook_label);
    }

    // 나이트 불러오기
    public void Knight(Container container,boolean white, int x, int y){
        JLabel knight_label;
        if(white){
            //백 나이트 이미지 불러오기
            knight_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    n_w);

            knight_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(knight_label,"white_knight",chess_object_size);
                }
            });

            white_knight.add(new Piece(new XY(x,y),knight_label,"white_knight"));
        } else {
            //흑 나이트 이미지 불러오기
            knight_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    n_b);

            knight_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(knight_label,"black_knight",chess_object_size);
                }
            });

            black_knight.add(new Piece(new XY(x,y),knight_label,"black_knight"));
        }
        Main.frame.getContentPane().setComponentZOrder(knight_label,0);
        container.add(knight_label);
    }

    // 비숍 불러오기
    public void Bishop(Container container,boolean white, int x, int y){
        JLabel bishop_label;
        if(white){
            //백 비숍 이미지 불러오기
            bishop_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.3),
                    b_w);

            bishop_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(bishop_label,"white_bishop",chess_object_size);
                }
            });

            white_bishop.add(new Piece(new XY(x,y),bishop_label,"white_bishop"));
        } else {
            //흑 비숍 이미지 불러오기
            bishop_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.3),
                    b_b);

            bishop_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(bishop_label,"black_bishop",chess_object_size);
                }
            });

            black_bishop.add(new Piece(new XY(x,y),bishop_label,"black_bishop"));
        }
        Main.frame.getContentPane().setComponentZOrder(bishop_label,0);
        container.add(bishop_label);
    }

    // 퀸 불러오기
    public void Queen(Container container,boolean white, int x, int y){
        JLabel queen_label;
        if(white){
            //백 퀸 이미지 불러오기
            queen_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    q_w);

            queen_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(queen_label,"white_queen",chess_object_size);
                }
            });

            white_queen.add(new Piece(new XY(x,y),queen_label,"white_queen"));
        } else {
            //흑 퀸 이미지 불러오기
            queen_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    q_b);

            queen_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(queen_label,"black_queen",chess_object_size);
                }
            });

            black_queen.add(new Piece(new XY(x,y),queen_label,"black_queen"));
        }
        Main.frame.getContentPane().setComponentZOrder(queen_label,0);
        container.add(queen_label);
    }

    // 폰 불러오기
    public void Pawn(Container container,boolean white, int x, int y){
        JLabel pawn_label;
        if(white){
            //백 폰 이미지 불러오기
            pawn_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.6), (int) (chess_object_size / 1.4),
                    p_w);

            pawn_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(pawn_label,"white_pawn",chess_object_size);
                }
            });

            white_pawn.add(new Piece(new XY(x,y),pawn_label,"white_pawn"));
        } else {
            //흑 폰 이미지 불러오기
            pawn_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.6), (int) (chess_object_size / 1.4),
                    p_b);

            pawn_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(pawn_label,"black_pawn",chess_object_size);
                }
            });

            black_pawn.add(new Piece(new XY(x,y),pawn_label,"black_pawn"));
        }
        Main.frame.getContentPane().setComponentZOrder(pawn_label,0);
        container.add(pawn_label);
    }

    // 킹 불러오기
    public void King(Container container,boolean white, int x, int y){
        JLabel king_label;
        if(white){
            //백 폰 이미지 불러오기
            king_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    k_w);

            king_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(king_label,"white_king",chess_object_size);
                }
            });

            white_king=new Piece(new XY(x,y),king_label,"white_king");
        } else {
            //흑 폰 이미지 불러오기
            king_label = BasicFunctions.loadImage(chess_object_size * (x - 1), chess_object_size * (y - 1),
                    chess_object_size, chess_object_size,
                    (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                    k_b);

            king_label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PieceFunctions.grab(king_label,"black_king",chess_object_size);
                }
            });

            black_king=new Piece(new XY(x,y),king_label,"black_king");
        }
        Main.frame.getContentPane().setComponentZOrder(king_label,0);
        container.add(king_label);
    }
}
