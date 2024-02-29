package com.kyubin.chess.functions;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.move.*;
import com.kyubin.chess.functions.move.analysis.Move;
import com.kyubin.chess.functions.move.king.King;
import com.kyubin.chess.functions.move.pawn.Pawn;
import com.kyubin.chess.functions.move.pawn.promotion.Promotion;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.xy.XYPlus;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.kyubin.chess.Main.*;
import static com.kyubin.chess.Main.chess_object_size;
import static com.kyubin.chess.functions.BasicFunctions.*;

public class PieceFunctions {
    public static boolean grab_object=false;

    public static boolean is_promotion=false;

    static XY xy=new XY(0,0);

    /**보드 출력
     *
     * @param chessGame 보드를 출력할 ChessGame
     */
    public static void printBoard(ChessGame chessGame){
        for(int y=1;y<9;y++){
            for(int x=1;x<9;x++){
                String piece=isPieceInSquareWithString(new XY(x,y),chessGame);
                if(piece.contains("knight")) System.out.print(piece.contains("white")?"N ":"n ");
                else System.out.print(piece.equals("")?"- ":(piece.contains("white")?piece.toUpperCase().charAt(6):piece.charAt(6))+" ");
            }
            System.out.println();
        }
    }

    /** 잡기(Grab) 함수*/
    public static void grab(JLabel object,String object_type,int chess_object_size){
        boolean is_white=object_type.contains("white");
        object_type=PieceFunctions.isPieceInSquareWithString(sortXY(chess_object_size,object.getX(),object.getY()),chessGame);

        if(is_promotion) return;

        // 만약 내 턴이 아니라면 함수 종료
        if(chessGame.white_turn&&!is_white) return;
        if(!chessGame.white_turn&&is_white) return;
        grab_object=!grab_object;
        if(!grab_object) return;
        xy=sortXY(chess_object_size,object.getX(),object.getY());

        Main.frame.getContentPane().setComponentZOrder(object,0);

        String finalObject_type = object_type;
        new Thread(()->{
            while(grab_object){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Main.frame.getContentPane().setComponentZOrder(object,0);

                object.setLocation((int) (MouseInfo.getPointerInfo().getLocation().x-chess_object_size/1.8 - Main.frame.getX())
                        , MouseInfo.getPointerInfo().getLocation().y - chess_object_size - Main.frame.getY()-20);
            }
            // 정렬
            XY xy_now=sortObject(chess_object_size,MouseInfo.getPointerInfo().getLocation().x - Main.frame.getX(),
                    MouseInfo.getPointerInfo().getLocation().y-20 - Main.frame.getY());

            // 갈수 있는 칸인지 체크
            boolean is_en_passant=false;

            boolean isItPossible=false;

            boolean is_king_side_castling=false;
            boolean is_queen_side_castling=false;

            if(finalObject_type.contains("rook")){
                for(XY moves: Rook.rookMoves(xy,is_white,false,chessGame)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(finalObject_type.contains("knight")){
                for(XY moves: Knight.knightMoves(xy,is_white,false,chessGame)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(finalObject_type.contains("bishop")){
                for(XY moves: Bishop.bishopMoves(xy,is_white,false,chessGame)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(finalObject_type.contains("queen")){
                for(XY moves: Queen.queenMoves(xy,is_white,false,chessGame)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(finalObject_type.contains("pawn")){
                for(XYPlus moves: Pawn.pawnMoves(xy,is_white,false,chessGame)){
                    if(moves.xy.x==xy_now.x&&moves.xy.y==xy_now.y){
                        isItPossible=true;

                        if(xy_now.y==(is_white?1:8)){
                            is_promotion=true;
                        }
                        if(moves.is_en_passant){
                            is_en_passant=true;
                        }

                        break;
                    }
                }
            }
            if(finalObject_type.contains("king")){
                for(XY moves: King.kingMoves(xy,is_white,false,chessGame)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        if(xy.x+2==xy_now.x) is_king_side_castling=true;
                        if(xy.x-2==xy_now.x) is_queen_side_castling=true;

                        isItPossible=true;
                        break;
                    }
                }
            }

            if(is_promotion){
                promotion(xy_now,is_white,null,chessGame);

                object.setLocation((!Main.is_flip_board?xy_now.x-1:9-(xy_now.x+1))*chess_object_size,(!Main.is_flip_board?xy_now.y-1:9-(xy_now.y+1))*chess_object_size);

                new Thread(()->{
                    do {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } while (get_promotion_type == null);

                    if(get_promotion_type!=PromotionType.NONE){
                        Move.MovePromotionPiece(is_white,xy,xy_now,get_promotion_type,object,true,chessGame);
                    } else {
                        object.setLocation((!Main.is_flip_board?xy.x-1:9-(xy.x+1))*chess_object_size,(!Main.is_flip_board?xy.y-1:9-(xy.y+1))*chess_object_size);
                    }
                    get_promotion_type=null;
                    is_promotion=false;
                }).start();
            } else {
                if(isItPossible){
                    Move.MovePiece(is_white,xy,xy_now,is_king_side_castling,is_queen_side_castling,is_en_passant, finalObject_type,object,true,chessGame);
                } else {
                    object.setLocation((!Main.is_flip_board?xy.x-1:9-(xy.x+1))*chess_object_size,(!Main.is_flip_board?xy.y-1:9-(xy.y+1))*chess_object_size);
                }
                is_promotion=false;
            }
        }).start();
    }

    public static void addPiece(String object_type,XY xy,ChessGame chessGame){
        boolean white=object_type.contains("white");

        if(object_type.contains("queen")){
            JLabel queen_label;
            if(white){
                //백 퀸 이미지 불러오기
                queen_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                        q_w);

                queen_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(queen_label,"white_queen",chess_object_size);
                    }
                });

                chessGame.white_queen.add(new Piece(xy,queen_label,"white_queen"));
            } else {
                //흑 퀸 이미지 불러오기
                queen_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                        q_b);

                queen_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(queen_label,"black_queen",chess_object_size);
                    }
                });

                chessGame.black_queen.add(new Piece(xy,queen_label,"black_queen"));
            }
            Main.frame.getContentPane().add(queen_label);
            Main.frame.getContentPane().setComponentZOrder(queen_label,0);
        }
        if(object_type.contains("bishop")){
            JLabel bishop_label;
            if(white){
                //백 비숍 이미지 불러오기
                bishop_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.3),
                        b_w);

                bishop_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(bishop_label,"white_bishop",chess_object_size);
                    }
                });

                chessGame.white_bishop.add(new Piece(xy,bishop_label,"white_bishop"));
            } else {
                //흑 비숍 이미지 불러오기
                bishop_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.3),
                        b_b);

                bishop_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(bishop_label,"black_bishop",chess_object_size);
                    }
                });

                chessGame.black_bishop.add(new Piece(xy,bishop_label,"black_bishop"));
            }
            Main.frame.getContentPane().add(bishop_label);
            Main.frame.getContentPane().setComponentZOrder(bishop_label,0);
        }
        if(object_type.contains("knight")){
            JLabel knight_label;
            if(white){
                //백 나이트 이미지 불러오기
                knight_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                        n_w);

                knight_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(knight_label,"white_knight",chess_object_size);
                    }
                });

                chessGame.white_knight.add(new Piece(xy,knight_label,"white_knight"));
            } else {
                //흑 나이트 이미지 불러오기
                knight_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.4), (int) (chess_object_size / 1.4),
                        n_b);

                knight_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(knight_label,"black_knight",chess_object_size);
                    }
                });

                chessGame.black_knight.add(new Piece(xy,knight_label,"black_knight"));
            }
            Main.frame.getContentPane().add(knight_label);
            Main.frame.getContentPane().setComponentZOrder(knight_label,0);
        }
        if(object_type.contains("rook")){
            JLabel rook_label;
            if(white){
                //백 룩 이미지 불러오기
                rook_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.5), (int) (chess_object_size / 1.3),
                        r_w);

                rook_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(rook_label,"white_rook",chess_object_size);
                    }
                });

                chessGame.white_rook.add(new Piece(xy,rook_label,"white_rook"));
            } else {
                //흑 룩 이미지 불러오기
                rook_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.5), (int) (chess_object_size / 1.3),
                        r_b);

                rook_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(rook_label,"black_rook",chess_object_size);
                    }
                });

                chessGame.black_rook.add(new Piece(xy,rook_label,"black_rook"));
            }
            Main.frame.getContentPane().add(rook_label);
            Main.frame.getContentPane().setComponentZOrder(rook_label,0);
        }
        if(object_type.contains("pawn")){
            JLabel pawn_label;
            if(white){
                //백 폰 이미지 불러오기
                pawn_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.6), (int) (chess_object_size / 1.4),
                        p_w);

                pawn_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(pawn_label,"white_pawn",chess_object_size);
                    }
                });

                chessGame.white_pawn.add(new Piece(xy,pawn_label,"white_pawn"));
            } else {
                //흑 폰 이미지 불러오기
                pawn_label = BasicFunctions.loadImage(chess_object_size * (Main.is_flip_board?9-(xy.x+1):(xy.x-1)),
                        chess_object_size * (Main.is_flip_board?9-(xy.y+1):(xy.y-1)),
                        chess_object_size, chess_object_size,
                        (int) (chess_object_size / 1.6), (int) (chess_object_size / 1.4),
                        p_b);

                pawn_label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PieceFunctions.grab(pawn_label,"black_pawn",chess_object_size);
                    }
                });

                chessGame.black_pawn.add(new Piece(xy,pawn_label,"black_pawn"));
            }
            Main.frame.getContentPane().add(pawn_label);
            Main.frame.getContentPane().setComponentZOrder(pawn_label,0);
        }
    }

    /**프로모션 선택했을때 바뀌는 변수(별로 안씀)*/
    static PromotionType get_promotion_type=null;

    /**
     * 프로모션 프레임을 뛰우고 기물을 생성함(프로모션)
     *
     * @param now_xy 프로모션 프레임 그리고 어디에 기물을 놓을지 XY 형태로 입력
     * @param is_white 기물 백 흑 구별
     * @param promotionType 프로모션 기물 종류 | PromotionType.NONE이라면 선택 화면 출력 그 다음 사용자가 선택하면 정해짐
     * @param chessGame 추가할 프로모션 기물의 ChessGame
     */
    public static void promotion(XY now_xy, boolean is_white, PromotionType promotionType, ChessGame chessGame){
        new Thread(() -> {
            if(promotionType==null){
                Promotion.getPromotionType(now_xy,is_white);
            } else {
                Promotion.promotionType=promotionType;
            }

            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (Promotion.promotionType != null) {
                    if(Promotion.promotionType!=PromotionType.NONE){
                        removeEnemy(now_xy,!is_white,chessGame);
                    }
                    if(Promotion.promotionType==PromotionType.QUEEN){
                        addPiece(is_white ? "white_queen":"black_queen",now_xy,chessGame);
                    }
                    if(Promotion.promotionType==PromotionType.KNIGHT){
                        addPiece(is_white ? "white_knight":"black_knight",now_xy,chessGame);
                    }
                    if(Promotion.promotionType==PromotionType.ROOK){
                        addPiece(is_white ? "white_rook":"black_rook",now_xy,chessGame);
                    }
                    if(Promotion.promotionType==PromotionType.BISHOP){
                        addPiece(is_white ? "white_bishop":"black_bishop",now_xy,chessGame);
                    }

                    Main.frame.repaint();

                    get_promotion_type=Promotion.promotionType;

                    Promotion.promotionType=null;
                    break;
                }
            }
        }).start();
    }

    /**기물 제거하기
     *
     * @param xy 기물을 제거할 위치
     * @param object_type 기물의 종류
     * @param chessGame 기물을 제거할 ChessGame
     */
    public static void removePiece(XY xy, String object_type,ChessGame chessGame){
        if(object_type.equals("white_rook")){
            for(Piece piece: chessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_rook.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("black_rook")){
            for(Piece piece: chessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_rook.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("white_knight")){
            for(Piece piece: chessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_knight.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("black_knight")){
            for(Piece piece: chessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_knight.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("white_bishop")){
            for(Piece piece: chessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_bishop.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("black_bishop")){
            for(Piece piece: chessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_bishop.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("white_queen")){
            for(Piece piece: chessGame.white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_queen.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("black_queen")){
            for(Piece piece: chessGame.black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_queen.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("white_pawn")){
            for(Piece piece: chessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_pawn.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("black_pawn")){
            for(Piece piece: chessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_pawn.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    break;
                }
            }
        }
        if(object_type.equals("white_king")){
            chessGame.white_king=new Piece(new XY(-5,-5),chessGame.white_king.label,"black_king");
            chessGame.white_king.label.setLocation(-10000,-10000);
        }
        if(object_type.equals("black_king")){
            chessGame.black_king=new Piece(new XY(-5,-5),chessGame.black_king.label,"black_king");
            chessGame.black_king.label.setLocation(-10000,-10000);
        }
    }

    public static List<Piece> stringToList(String object_type,ChessGame chessGame){
        if(object_type.equals("white_rook")) return chessGame.white_rook;
        if(object_type.equals("black_rook")) return chessGame.black_rook;
        if(object_type.equals("white_knight")) return chessGame.white_knight;
        if(object_type.equals("black_knight")) return chessGame.black_knight;
        if(object_type.equals("white_bishop")) return chessGame.white_bishop;
        if(object_type.equals("black_bishop")) return chessGame.black_bishop;
        if(object_type.equals("white_queen")) return chessGame.white_queen;
        if(object_type.equals("black_queen")) return chessGame.black_queen;
        if(object_type.equals("white_pawn")) return chessGame.white_pawn;
        if(object_type.equals("black_pawn")) return chessGame.black_pawn;

        return new ArrayList<>();
    }

    public static List<XY> isPieceGoThere(XY xy, String object_type, ChessGame chessGame){
        boolean is_white=object_type.contains("white");

        List<XY> result=new ArrayList<>();

        if(object_type.equals("white_rook")){
            for(Piece piece: chessGame.white_rook){
                if(Rook.rookMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("black_rook")){
            for(Piece piece: chessGame.black_rook){
                if(Rook.rookMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("white_knight")){
            for(Piece piece: chessGame.white_knight){
                if(Knight.knightMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("black_knight")){
            for(Piece piece: chessGame.black_knight){
                if(Knight.knightMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("white_bishop")){
            for(Piece piece: chessGame.white_bishop){
                if(Bishop.bishopMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("black_bishop")){
            for(Piece piece: chessGame.black_bishop){
                if(Bishop.bishopMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("white_queen")){
            for(Piece piece: chessGame.white_queen){
                if(Queen.queenMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("black_queen")){
            for(Piece piece: chessGame.black_queen){
                if(Queen.queenMoves(piece.xy,is_white,false,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("white_pawn")){
            for(Piece piece: chessGame.white_pawn){
                if(Pawn.pawnMoves(piece.xy,is_white,false,chessGame).contains(new XYPlus(xy,false))){
                    result.add(piece.xy);
                }
            }
        }
        if(object_type.equals("black_pawn")){
            for(Piece piece: chessGame.black_pawn){
                if(Pawn.pawnMoves(piece.xy,is_white,false,chessGame).contains(new XYPlus(xy,false))){
                    result.add(piece.xy);
                }
            }
        }

        return result;
    }

    public static List<XY> isPieceGoThere(XY xy,boolean is_white, ChessGame chessGame){
        List<XY> result=new ArrayList<>();

        if(is_white){
            for(Piece piece: chessGame.white_rook){
                if(Rook.rookMoves(piece.xy,true,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.white_knight){
                if(Knight.knightMoves(piece.xy,true,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.white_bishop){
                if(Bishop.bishopMoves(piece.xy,true,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.white_queen){
                if(Queen.queenMoves(piece.xy,true,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.white_pawn){
                if(Pawn.pawnMoves(piece.xy,true,true,chessGame).contains(new XYPlus(xy,false))){
                    result.add(piece.xy);
                }
            }
            if(King.kingMoves(chessGame.white_king.xy,true,true,chessGame).contains(xy)){
                result.add(chessGame.white_king.xy);
            }
        } else {
            for(Piece piece: chessGame.black_rook){
                if(Rook.rookMoves(piece.xy,false,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.black_knight){
                if(Knight.knightMoves(piece.xy,false,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.black_bishop){
                if(Bishop.bishopMoves(piece.xy,false,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.black_queen){
                if(Queen.queenMoves(piece.xy,false,true,chessGame).contains(xy)){
                    result.add(piece.xy);
                }
            }
            for(Piece piece: chessGame.black_pawn){
                if(Pawn.pawnMoves(piece.xy,false,true,chessGame).contains(new XYPlus(xy,false))){
                    result.add(piece.xy);
                }
            }
            if(King.kingMoves(chessGame.black_king.xy,false,true,chessGame).contains(xy)){
                result.add(chessGame.black_king.xy);
            }
        }

        return result;
    }

    public static JLabel getObject(XY xy,ChessGame chessGame){
        for(Piece piece: chessGame.black_rook){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.black_knight){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.black_bishop){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.black_queen){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.black_pawn){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.white_rook){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.white_knight){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.white_bishop){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.white_queen){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        for(Piece piece: chessGame.white_pawn){
            if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                return piece.label;
            }
        }
        if(chessGame.white_king.xy.equals(xy)){
            return chessGame.white_king.label;
        }
        if(chessGame.black_king.xy.equals(xy)){
            return chessGame.black_king.label;
        }

        return new JLabel();
    }

    public static String removeEnemy(XY xy, boolean is_white, ChessGame chessGame){
        if(is_white){
            for(Piece piece: chessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_rook.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "black_rook";
                }
            }
            for(Piece piece: chessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_knight.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "black_knight";
                }
            }
            for(Piece piece: chessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_bishop.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "black_bishop";
                }
            }
            for(Piece piece: chessGame.black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_queen.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "black_queen";
                }
            }
            for(Piece piece: chessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_pawn.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "black_pawn";
                }
            }
        } else {
            for(Piece piece: chessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_rook.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "white_rook";
                }
            }
            for(Piece piece: chessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_knight.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "white_knight";
                }
            }
            for(Piece piece: chessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_bishop.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "white_bishop";
                }
            }
            for(Piece piece: chessGame.white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_queen.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "white_queen";
                }
            }
            for(Piece piece: chessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_pawn.remove(piece);
                    piece.label.setLocation(-10000,-10000);
                    return "white_pawn";
                }
            }
        }

        return "";
    }

    public static Piece removeEnemyWithPiece(XY xy, boolean is_white,ChessGame chessGame){
        if(is_white){
            for(Piece piece: chessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_rook.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_knight.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_bishop.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_queen.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_pawn.remove(piece);
                    return piece;
                }
            }
        } else {
            for(Piece piece: chessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_rook.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_knight.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_bishop.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_queen.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: chessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_pawn.remove(piece);
                    return piece;
                }
            }
        }

        return null;
    }

    public static boolean isPieceInSquare(XY xy,ChessGame chessGame){
        for(Piece piece:chessGame.white_rook){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.black_rook){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.white_knight){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.black_knight){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.white_bishop){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.black_bishop){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.white_queen){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.black_queen){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.white_pawn){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:chessGame.black_pawn){
            if(piece==null) continue;
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        if(chessGame.white_king!=null&&chessGame.white_king.xy.equals(xy)){
            return true;
        }
        return chessGame.white_king!=null&&chessGame.black_king.xy.equals(xy);
    }


    public static String isPieceInSquareWithStringWithWhite(XY xy,boolean is_white,ChessGame chessGame){
        if (is_white) {
            for(Piece piece:chessGame.white_bishop){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "white_bishop";
                }
            }
            for(Piece piece:chessGame.white_rook){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "white_rook";
                }
            }
            for(Piece piece:chessGame.white_knight){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "white_knight";
                }
            }
            for(Piece piece:chessGame.white_queen){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "white_queen";
                }
            }
            for(Piece piece:chessGame.white_pawn){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "white_pawn";
                }
            }
            if(chessGame.white_king!=null) if(chessGame.white_king.xy.equals(xy)){
                return "white_king";
            }
        } else {
            for(Piece piece:chessGame.black_bishop){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "black_bishop";
                }
            }
            for(Piece piece:chessGame.black_rook){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "black_rook";
                }
            }
            for(Piece piece:chessGame.black_knight){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "black_knight";
                }
            }
            for(Piece piece:chessGame.black_queen){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "black_queen";
                }
            }
            for(Piece piece:chessGame.black_pawn){
                if(piece==null) continue;
                if(piece.xy.equals(xy)){
                    return "black_pawn";
                }
            }
            if(chessGame.black_king!=null) if(chessGame.black_king.xy.equals(xy)){
                return "black_king";
            }
        }
        return "";
    }

    /**
     * 만약 기물이 있다면 이 기물의 종류를 출력해주는 간단한 함수
     *
     * @param xy 기물을 확인할 위치
     * @param chessGame 기물이 있을 ChessGame
     * @return 없다면 ""을 출력 아니면 기물의 종류 출력
     */
    public static String isPieceInSquareWithString(XY xy,ChessGame chessGame){
        for(Piece piece:chessGame.white_bishop){
            if(piece.xy.equals(xy)){
                return "white_bishop";
            }
        }
        for(Piece piece:chessGame.white_rook){
            if(piece.xy.equals(xy)){
                return "white_rook";
            }
        }
        for(Piece piece:chessGame.white_knight){
            if(piece.xy.equals(xy)){
                return "white_knight";
            }
        }
        if(chessGame.white_king.xy.equals(xy)){
            return "white_king";
        }
        if(chessGame.black_king.xy.equals(xy)){
            return "black_king";
        }
        for(Piece piece:chessGame.white_queen){
            if(piece.xy.equals(xy)){
                return "white_queen";
            }
        }
        for(Piece piece:chessGame.white_pawn){
            if(piece.xy.equals(xy)){
                return "white_pawn";
            }
        }
        for(Piece piece:chessGame.black_bishop){
            if(piece.xy.equals(xy)){
                return "black_bishop";
            }
        }
        for(Piece piece:chessGame.black_rook){
            if(piece.xy.equals(xy)){
                return "black_rook";
            }
        }
        for(Piece piece:chessGame.black_knight){
            if(piece.xy.equals(xy)){
                return "black_knight";
            }
        }
        for(Piece piece:chessGame.black_queen){
            if(piece.xy.equals(xy)){
                return "black_queen";
            }
        }
        for(Piece piece:chessGame.black_pawn){
            if(piece.xy.equals(xy)){
                return "black_pawn";
            }
        }
        return "";
    }
}
