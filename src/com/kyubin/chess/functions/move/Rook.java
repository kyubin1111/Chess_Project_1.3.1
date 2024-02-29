package com.kyubin.chess.functions.move;

import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import java.util.ArrayList;
import java.util.List;

import static com.kyubin.chess.functions.BasicFunctions.addAll;

public class Rook {
    public static List<XY> rookMoves(XY rook,boolean is_white,boolean no_repeat,ChessGame chessGame) {
        List<XY> moves = new ArrayList<>();
        List<XY> allies;
        List<XY> enemy;

        if(is_white) {
            allies = addAll(chessGame.white_rook, chessGame.white_knight, chessGame.white_bishop,
                    chessGame.white_queen, chessGame.white_pawn, chessGame.white_king);
            enemy = addAll(chessGame.black_rook, chessGame.black_knight, chessGame.black_bishop,
                    chessGame.black_queen, chessGame.black_pawn, chessGame.black_king);
        } else {
            allies = addAll(chessGame.black_rook, chessGame.black_knight, chessGame.black_bishop,
                    chessGame.black_queen, chessGame.black_pawn, chessGame.black_king);
            enemy = addAll(chessGame.white_rook, chessGame.white_knight, chessGame.white_bishop,
                    chessGame.white_queen, chessGame.white_pawn, chessGame.white_king);
        }

        // 수직 및 수평 이동 경로 계산
        addMovesInDirection(rook, allies, enemy, moves, 1, 0); // 오른쪽
        addMovesInDirection(rook, allies, enemy, moves, -1, 0); // 왼쪽
        addMovesInDirection(rook, allies, enemy, moves, 0, 1); // 위
        addMovesInDirection(rook, allies, enemy, moves, 0, -1); // 아래

        List<XY> validMoves = new ArrayList<>();

        if(!no_repeat){
            List<Piece> pieces=is_white? chessGame.white_rook: chessGame.black_rook;

            XY xy = rook;

            for (XY move : moves) {
                Piece piece=null;

                for(Piece p:pieces){
                    if(p.xy.equals(xy)){
                        p.xy=move;
                        piece= PieceFunctions.removeEnemyWithPiece(p.xy,is_white,chessGame);
                        xy=move;
                    }
                }
                if (Check.checkCount(!is_white,true,chessGame)==0) {
                    validMoves.add(move);
                }
                if(piece!=null){
                    PieceFunctions.stringToList(piece.type,chessGame).add(piece);
                }
            }

            for(Piece p:pieces){
                if(p.xy.equals(xy)){
                    p.xy=rook;
                }
            }

            return validMoves;
        } else {
            return moves;
        }
    }

    private static void addMovesInDirection(XY rook, List<XY> allies,List<XY> enemy, List<XY> moves, int dx, int dy) {
        int x = rook.x;
        int y = rook.y;

        while (true) {
            x += dx;
            y += dy;

            // 체스판 범위를 벗어나면 중단
            if (x <= 0 || x > 8 || y <= 0 || y > 8) {
                break;
            }

            // 아군 기물이 있는지 확인
            boolean is_piece_this_square=false;

            for(XY piece:allies){
                if(piece.x==x&&piece.y==y){
                    is_piece_this_square=true;
                    break;
                }
            }

            // 적군 기물이 있는지 확인
            for(XY piece:enemy){
                if(piece.x==x&&piece.y==y){
                    is_piece_this_square=true;
                    moves.add(new XY(x, y));
                    break;
                }
            }

            // 아군 또는 적군이 있는 칸이면 while 문 정지
            if(is_piece_this_square){
                break;
            }

            moves.add(new XY(x, y));
        }
    }
}
