package com.kyubin.chess.functions.move;

import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import java.util.ArrayList;
import java.util.List;

import static com.kyubin.chess.functions.BasicFunctions.addAll;

public class Bishop {
    public static List<XY> bishopMoves(XY bishop, boolean is_white, boolean no_repeat,ChessGame chessGame) {
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

        // 모든 대각선 방향 이동 경로 계산
        addMovesInDirection(bishop, allies, enemy, moves, 1, 1); // 오른쪽 위
        addMovesInDirection(bishop, allies, enemy, moves, 1, -1); // 오른쪽 아래
        addMovesInDirection(bishop, allies, enemy, moves, -1, 1); // 왼쪽 위
        addMovesInDirection(bishop, allies, enemy, moves, -1, -1); // 왼쪽 아래

        List<XY> validMoves = new ArrayList<>();

        if(!no_repeat){
            List<Piece> pieces=is_white? chessGame.white_bishop: chessGame.black_bishop;

            XY xy = bishop;

            for (XY move : moves) {
                Piece piece = null;

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
                    p.xy=bishop;
                }
            }

            return validMoves;
        } else {
            return moves;
        }
    }

    private static void addMovesInDirection(XY bishop, List<XY> allies,List<XY> enemy, List<XY> moves, int dx, int dy) {
        int x = bishop.x;
        int y = bishop.y;

        while (true) {
            x += dx;
            y += dy;

            // 체스판 범위를 벗어나면 중단
            if (x <= 0 || x > 8 || y <= 0 || y > 8) {
                break;
            }

            // 아군 또는 적군 기물이 있는지 확인
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
