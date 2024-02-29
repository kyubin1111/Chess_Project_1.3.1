package com.kyubin.chess.functions.move.analysis.convert;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.analysis.MoveFake;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.functions.move.check.CheckMate;
import com.kyubin.chess.functions.move.king.castling.KingSideCastling;
import com.kyubin.chess.functions.move.king.castling.QueenSideCastling;
import com.kyubin.chess.functions.move.pawn.promotion.Promotion;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.game.MoveTo;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static com.kyubin.chess.functions.BasicFunctions.removeString;

public class ConvertMove {
    public static int convertingMoveSize=0;

    // To XY to LAN(ex. new XY(4,4) :
    // 8 r n b q k b n r
    // 7 p p p p p p p p
    // 6 - - - - - - - -
    // 5 - - - * - - - -
    // 4 - - - - - - - -
    // 3 - - - - - - - -
    // 2 P P P P P P P P
    // 1 R N B Q K B N R
    //   a b c d e f g h
    //
    // * = d5
    //
    // and print "d5")
    public static String xyToLAN(XY xy){
        int x=xy.x;
        int y=xy.y;

        String stringX="";
        if(x==1) stringX="a";
        if(x==2) stringX="b";
        if(x==3) stringX="c";
        if(x==4) stringX="d";
        if(x==5) stringX="e";
        if(x==6) stringX="f";
        if(x==7) stringX="g";
        if(x==8) stringX="h";

        return stringX+(9-y);
    }

    // To XY to LAN(ex. d5 :
    // 8 r n b q k b n r
    // 7 p p p p p p p p
    // 6 - - - - - - - -
    // 5 - - - * - - - -
    // 4 - - - - - - - -
    // 3 - - - - - - - -
    // 2 P P P P P P P P
    // 1 R N B Q K B N R
    //   a b c d e f g h
    //
    // * = new XY(4,4)
    //
    // and print new XY(4,4)
    public static XY LANToXY(String xy){
        String x=String.valueOf(xy.charAt(0));
        String y=String.valueOf(xy.charAt(1));

        int intX=0;
        if(x.equals("a")) intX=1;
        if(x.equals("b")) intX=2;
        if(x.equals("c")) intX=3;
        if(x.equals("d")) intX=4;
        if(x.equals("e")) intX=5;
        if(x.equals("f")) intX=6;
        if(x.equals("g")) intX=7;
        if(x.equals("h")) intX=8;

        return new XY(intX,9-Integer.parseInt(y));
    }

    public static String convertPreviousMove(String stockfishMove, ChessGame chessGame){
        MoveTo move = chessGame.game.get(chessGame.game.size()-1);

        boolean is_white=!chessGame.white_turn;

        boolean is_checkmate= CheckMate.isCheckmate(is_white,chessGame);
        boolean is_check= Check.checkCount(is_white,false,chessGame)>0&&!is_checkmate;

        XY pre_xy = move.pre_xy;
        XY now_xy = move.now_xy;

        boolean is_ks=move.is_ks;
        boolean is_qs=move.is_qs;

        if(is_ks) return "O-O"+(is_check?"+":"")+(is_checkmate?"#":"");
        if(is_qs) return "O-O-O"+(is_check?"+":"")+(is_checkmate?"#":"");

        boolean is_taking=!move.enemy_type.equals("");

        String object_type=move.type;

        boolean is_en_passant=move.is_ep;

        List<XY> going = move.goingPiece;

        boolean is_equal_x=false;
        boolean is_equal_y=false;

        going.remove(pre_xy);

        for(XY go:going){
            if(go.x==pre_xy.x) is_equal_x=true;
            if(go.y==pre_xy.y) is_equal_y=true;
            if(is_equal_x&&is_equal_y) break;
        }

        boolean is_promotion = object_type.contains("pawn")&&now_xy.y==(object_type.contains("white")?1:8);

        if(object_type.contains("pawn")){
            try {
                return (is_taking||is_en_passant?stockfishMove.charAt(0)+"x":"")+stockfishMove.substring(2,4)
                        +(is_promotion?"="+stockfishMove.toUpperCase().charAt(4):"")+
                        (is_check?"+":"")+(is_checkmate?"#":"");
            } catch (Exception e){
                return "";
            }
        } else if(object_type.contains("knight")){
            return "N"+(going.size()!=0?go(is_equal_x,is_equal_y,stockfishMove):"")
                    +(is_taking?"x":"")+stockfishMove.substring(2,4)+(is_check?"+":"")+(is_checkmate?"#":"");
        } else if(object_type.contains("bishop")){
            return "B"+(going.size()!=0?go(is_equal_x,is_equal_y,stockfishMove):"")+
                    (is_taking?"x":"")+stockfishMove.substring(2,4)+(is_check?"+":"")+(is_checkmate?"#":"");
        } else if(object_type.contains("rook")){
            return "R"+(going.size()!=0?go(is_equal_x,is_equal_y,stockfishMove):"")+
                    (is_taking?"x":"")+stockfishMove.substring(2,4)+(is_check?"+":"")+(is_checkmate?"#":"");
        } else if(object_type.contains("queen")){
            return "Q"+(going.size()!=0?go(is_equal_x,is_equal_y,stockfishMove):"")+
                    (is_taking?"x":"")+stockfishMove.substring(2,4)+(is_check?"+":"")+(is_checkmate?"#":"");
        } else if(object_type.contains("king")){
            return "K"+(going.size()!=0?go(is_equal_x,is_equal_y,stockfishMove):"")+
                    (is_taking?"x":"")+stockfishMove.substring(2,4)+(is_check?"+":"")+(is_checkmate?"#":"");
        } else {
            return "";
        }
    }

    private static String convertToLAN(String convertMove,ChessGame chessGame){
        boolean is_white=chessGame.white_turn;

        if(convertMove.equals("O-O")) return is_white?"e1g1":"e8g8";
        if(convertMove.equals("O-O-O")) return is_white?"e1c1":"e8c8";

        String type;

        if(convertMove.charAt(0)=='Q') type=(is_white?"white_":"black_")+"queen";
        else if(convertMove.charAt(0)=='R') type=(is_white?"white_":"black_")+"rook";
        else if(convertMove.charAt(0)=='B') type=(is_white?"white_":"black_")+"bishop";
        else if(convertMove.charAt(0)=='K') type=(is_white?"white_":"black_")+"king";
        else if(convertMove.charAt(0)=='N') type=(is_white?"white_":"black_")+"knight";
        else type=(is_white?"white_":"black_")+"pawn";

        int equalIndex = convertMove.indexOf("="); // '=' 문자의 위치를 찾음

        boolean is_promotion= convertMove.contains("=");
        char promotion_type=' ';
        if(equalIndex!=-1) promotion_type=convertMove.toUpperCase().charAt(equalIndex + 1);

        String s=removeString(convertMove,new ArrayList<>(Arrays.asList("x","#","+","=","N","R","B","K","Q")));

        String now_xy="";

        if(s.length()==4) return s;
        if(s.length()==3) now_xy=s.substring(1,3);
        if(s.length()==2) now_xy=s;

        XY xy = LANToXY(now_xy);

        List<XY> going = PieceFunctions.isPieceGoThere(xy,type,chessGame);

        String convert="";

        if(s.length()==3) convert=String.valueOf(s.charAt(0));

        int intX=0;
        int intY=0;
        try {
            intY=Integer.parseInt(String.valueOf(s.charAt(0)));
        } catch (NumberFormatException ignored){

        }

        if(convert.equals("a")) intX=1;
        if(convert.equals("b")) intX=2;
        if(convert.equals("c")) intX=3;
        if(convert.equals("d")) intX=4;
        if(convert.equals("e")) intX=5;
        if(convert.equals("f")) intX=6;
        if(convert.equals("g")) intX=7;
        if(convert.equals("h")) intX=8;

        XY pre_xy=new XY(9,9);

        for(XY go:going){
            if((intX == 0 || go.x == intX)&&(intY == 0 || go.y == intY)){
                pre_xy=go;
            }
        }

        return xyToLAN(pre_xy)+now_xy+(is_promotion?promotion_type:"");
    }

    //Converting LAN to SAN
    public static String convertMove(String convertMove, int size, ChessGame preChessGame,ChessGame realChessGame, String result) {
        ChessGame chessGame;

        if(size==0) {
            chessGame=new ChessGame(realChessGame);
            convertingMoveSize++;
        } else {
            chessGame=preChessGame;
        }

        String[] moves=convertMove.split(" ");

        if(moves.length<=size) {
            convertingMoveSize--;
            return result;
        }

        String move = moves[size];

        XY xy = LANToXY(move.substring(0,2));
        XY now_xy = LANToXY(move.substring(2,4));

        ChessGame chessGame2 = MoveFake.MovePiece(xy
                ,now_xy,chessGame,move.length() >= 5? Promotion.charToPromotionType(move.charAt(4)):
                        PromotionType.NONE);

        if(CheckMate.isCheckmate(preChessGame.white_turn,preChessGame)) return result;

        String add=convertPreviousMove(move, chessGame2);

        if(!toFEN(Main.chessGame).equals(toFEN(realChessGame))){
            return "";
        }
        if(add.equals("")){
            return result;
        }

        return convertMove(convertMove,size+1,chessGame2,realChessGame,
                result+add+" ");
    }

    //Converting SAN to LAN
    public static String convertMoveSanMoves(String convertMove, int size, ChessGame realChessGame, String result) throws LoadFENFailedException {
        ChessGame chessGame = new ChessGame(new JFrame());

        if(size==0) {
            GetFEN.parseFEN(toFEN(realChessGame), new JFrame().getContentPane(), chessGame);

            chessGame.fen_game = new ArrayList<>(realChessGame.fen_game);
            Stack<Integer> full = new Stack<>();
            full.addAll(realChessGame.full_move);
            chessGame.full_move = full;
            Stack<Integer> half = new Stack<>();
            half.addAll(realChessGame.half_move);
            chessGame.full_move = half;

            chessGame.white_turn = realChessGame.white_turn;
        } else {
            chessGame=realChessGame;
        }

        String[] moves=convertMove.split(" ");

        if(moves.length<=size) return result;

        String move = moves[size];

        String add=convertToLAN(move, realChessGame);

        XY xy = LANToXY(add.substring(0,2));
        XY now_xy = LANToXY(add.substring(2,4));

        ChessGame chessGame2 = MoveFake.MovePiece(xy
                ,now_xy,chessGame,move.length() >= 5? Promotion.charToPromotionType(move.charAt(4)):
                        PromotionType.NONE);

        return convertMoveSanMoves(convertMove,size+1,chessGame2,result+" "+add);
    }

    private static String go(boolean is_equal_x, boolean is_equal_y, String stockfishMove){
        if(is_equal_x&&is_equal_y) return stockfishMove.substring(0,2);
        if(is_equal_y) return String.valueOf(stockfishMove.charAt(0));
        if(is_equal_x) return String.valueOf(stockfishMove.charAt(1));
        return String.valueOf(stockfishMove.charAt(0));
    }

    //Get FEN at this chessGame with No HalfMove And full move
    public static String toFENWithNoHalfMove(ChessGame chessGame) {
        StringBuilder fen = new StringBuilder();

        for (int y = 1; y <= 8; y++) {
            int emptyCount = 0;
            for (int x = 1; x <= 8; x++) {
                String white_piece = PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(x,y),true,chessGame);
                String black_piece = PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(x,y),false,chessGame);

                char piece='1';

                if(!white_piece.equals("")&&black_piece.equals("")) {
                    piece=white_piece.toUpperCase().charAt(6);
                    if(white_piece.contains("knight")) piece='N';
                }
                if(white_piece.equals("")&!black_piece.equals("")){
                    piece=black_piece.charAt(6);
                    if(black_piece.contains("knight")) piece='n';
                }

                if (piece == '1') {
                    emptyCount++;
                } else {
                    if (emptyCount != 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                }
            }

            if (emptyCount != 0) {
                fen.append(emptyCount);
            }
            if (y < 8) {
                fen.append('/');
            }
        }

        fen.append(" ").append(chessGame.white_turn?"w":"b");

        fen.append(" ")
                .append(KingSideCastling.isPossibleKingSideCastling(true,chessGame,chessGame.white_king) ? "K" : "")
                .append(QueenSideCastling.isPossibleQueenSideCastling(true,chessGame,chessGame.white_king) ? "Q" : "")
                .append(KingSideCastling.isPossibleKingSideCastling(false,chessGame,chessGame.black_king) ? "k" : "")
                .append(QueenSideCastling.isPossibleQueenSideCastling(false,chessGame,chessGame.black_king) ? "q" : "");

        if(
                !KingSideCastling.isPossibleKingSideCastling(true,chessGame,chessGame.white_king)&&
                !QueenSideCastling.isPossibleQueenSideCastling(true,chessGame,chessGame.white_king)&&
                !KingSideCastling.isPossibleKingSideCastling(false,chessGame,chessGame.black_king)&&
                        !QueenSideCastling.isPossibleQueenSideCastling(false,chessGame,chessGame.black_king)) fen.append("-");

        return fen.toString();
    }

    //Get FEN at this chessGame
    public static String toFEN(ChessGame chessGame) {
        StringBuilder fen = new StringBuilder();

        for (int y = 1; y <= 8; y++) {
            int emptyCount = 0;
            for (int x = 1; x <= 8; x++) {
                String white_piece = PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(x,y),true,chessGame);
                String black_piece = PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(x,y),false,chessGame);

                char piece='1';

                if(!white_piece.equals("")&&black_piece.equals("")) {
                    piece=white_piece.toUpperCase().charAt(6);
                    if(white_piece.contains("knight")) piece='N';
                }
                if(white_piece.equals("")&!black_piece.equals("")){
                    piece=black_piece.charAt(6);
                    if(black_piece.contains("knight")) piece='n';
                }

                if (piece == '1') {
                    emptyCount++;
                } else {
                    if (emptyCount != 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                }
            }

            if (emptyCount != 0) {
                fen.append(emptyCount);
            }
            if (y < 8) {
                fen.append('/');
            }
        }

        fen.append(" ").append(chessGame.white_turn?"w":"b");

        fen.append(" ")
                .append(KingSideCastling.isPossibleKingSideCastling(true,chessGame,chessGame.white_king) ? "K" : "")
                .append(QueenSideCastling.isPossibleQueenSideCastling(true,chessGame,chessGame.white_king) ? "Q" : "")
                .append(KingSideCastling.isPossibleKingSideCastling(false,chessGame,chessGame.black_king) ? "k" : "")
                .append(QueenSideCastling.isPossibleQueenSideCastling(false,chessGame,chessGame.black_king) ? "q" : "");

        if(
                !KingSideCastling.isPossibleKingSideCastling(true,chessGame,chessGame.white_king)&&
                        !QueenSideCastling.isPossibleQueenSideCastling(true,chessGame,chessGame.white_king)&&
                        !KingSideCastling.isPossibleKingSideCastling(false,chessGame,chessGame.black_king)&&
                        !QueenSideCastling.isPossibleQueenSideCastling(false,chessGame,chessGame.black_king)) fen.append("-");

        boolean pawn_moved=false;

        if(!(chessGame.game.size()==0)){
            if(chessGame.game.get(chessGame.game.size()-1).type.contains("pawn")){
                MoveTo moveTo = chessGame.game.get(chessGame.game.size()-1);
                if(chessGame.white_turn){
                    if(moveTo.pre_xy.y-moveTo.now_xy.y==-2){
                        fen.append(" ").append(xyToLAN(new XY(moveTo.now_xy.x,moveTo.now_xy.y-1)));
                        pawn_moved=true;
                    }
                } else {
                    if(moveTo.pre_xy.y-moveTo.now_xy.y==2){
                        fen.append(" ").append(xyToLAN(new XY(moveTo.now_xy.x,moveTo.now_xy.y+1)));
                        pawn_moved=true;
                    }
                }
            }
        }

        if(!pawn_moved){
            fen.append(" -");
        }

        fen.append(" ").append(chessGame.half_move.peek());
        fen.append(" ").append(chessGame.full_move.peek());

        return fen.toString();
    }
}
