package com.kyubin.chess.functions.move.king;

import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.functions.move.king.castling.KingSideCastling;
import com.kyubin.chess.functions.move.king.castling.QueenSideCastling;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.kyubin.chess.functions.BasicFunctions.addAll;

public class King {
    public static List<XY> kingMoves(XY king, boolean is_white, boolean no_repeat,ChessGame chessGame) {
        List<XY> moves = new ArrayList<>();
        List<XY> allies;

        if(is_white) {
            allies = addAll(chessGame.white_rook, chessGame.white_knight, chessGame.white_bishop,
                    chessGame.white_queen, chessGame.white_pawn, chessGame.white_king);
        } else {
            allies = addAll(chessGame.black_rook, chessGame.black_knight, chessGame.black_bishop,
                    chessGame.black_queen, chessGame.black_pawn, chessGame.black_king);
        }

        // 모든 방향으로 한 칸씩 이동 가능
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue; // 제자리는 제외
                }
                XY move = new XY(king.x + dx, king.y + dy);

                boolean contains = false;

                for(XY allie : allies){
                    if(allie.x==move.x&&allie.y==move.y){
                        contains=true;
                        break;
                    }
                }

                if (isValidPoint(move) && !contains) {
                    moves.add(move);
                }
            }
        }

        List<XY> validMoves = new ArrayList<>();

        if(!no_repeat){
            if(KingSideCastling.isPossibleKingSideCastling(is_white,chessGame,new Piece(king,new JLabel(),is_white?"white_king":"black_king"))){
                moves.add(new XY(king.x+2,king.y));
            }
            if(QueenSideCastling.isPossibleQueenSideCastling(is_white,chessGame,new Piece(king,new JLabel(),is_white?"white_king":"black_king"))){
                moves.add(new XY(king.x-2,king.y));
            }

            Piece piece=is_white? chessGame.white_king: chessGame.black_king;

            XY xy = king;

            for (XY move : moves) {
                Piece piece2 = null;

                if(piece.xy.equals(xy)){
                    piece.xy=move;
                    piece2=PieceFunctions.removeEnemyWithPiece(piece.xy,is_white,chessGame);
                    xy=move;
                }

                if (Check.checkCount(!is_white,true,chessGame)==0) {
                    validMoves.add(move);
                }
                if(piece2!=null){
                    PieceFunctions.stringToList(piece2.type,chessGame).add(piece2);
                }
            }

            if(piece.xy.equals(xy)){
                piece.xy=king;
            }

            return validMoves;
        } else {
            return moves;
        }
    }

    private static boolean isValidPoint(XY p) {
        return !(p.x <= 0 || p.x > 8 || p.y <= 0 || p.y > 8);
    }
}
