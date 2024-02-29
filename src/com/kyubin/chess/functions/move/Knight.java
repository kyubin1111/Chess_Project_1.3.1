package com.kyubin.chess.functions.move;

import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import java.util.ArrayList;
import java.util.List;

import static com.kyubin.chess.functions.BasicFunctions.addAll;

public class Knight {
    public static List<XY> knightMoves(XY knight, boolean is_white, boolean no_repeat,ChessGame chessGame) {
        List<XY> allies;

        if(is_white) {
            allies = addAll(chessGame.white_rook, chessGame.white_knight, chessGame.white_bishop,
                    chessGame.white_queen, chessGame.white_pawn, chessGame.white_king);
        } else {
            allies = addAll(chessGame.black_rook, chessGame.black_knight, chessGame.black_bishop,
                    chessGame.black_queen, chessGame.black_pawn, chessGame.black_king);
        }

        List<XY> moves = new ArrayList<>();
        int[][] directions = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] d : directions) {
            int x = knight.x + d[0];
            int y = knight.y + d[1];

            // 체스판 범위를 벗어나거나 아군 기물이 있는지 확인
            if (!(x <= 0 || x > 8 || y <= 0 || y > 8)) {
                // 아군 기물이 있는지 확인
                boolean is_piece_this_square=false;

                for(XY piece:allies){
                    if(piece.x==x&&piece.y==y){
                        is_piece_this_square=true;
                        break;
                    }
                }

                if(!is_piece_this_square) moves.add(new XY(x, y));
            }
        }

        List<XY> validMoves = new ArrayList<>();

        if(!no_repeat){
            List<Piece> pieces=is_white? chessGame.white_knight: chessGame.black_knight;

            XY xy = knight;

            for (XY move : moves) {
                Piece piece = null;

                for(Piece p:pieces){
                    if(p.xy.equals(xy)){
                        p.xy=move;
                        piece=PieceFunctions.removeEnemyWithPiece(p.xy,is_white,chessGame);
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
                    p.xy=knight;
                }
            }

            return validMoves;
        } else {
            return moves;
        }
    }
}
