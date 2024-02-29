package com.kyubin.chess.functions.draw;

import com.kyubin.chess.functions.move.Bishop;
import com.kyubin.chess.functions.move.Knight;
import com.kyubin.chess.functions.move.Queen;
import com.kyubin.chess.functions.move.Rook;
import com.kyubin.chess.functions.move.check.Check;
import com.kyubin.chess.functions.move.king.King;
import com.kyubin.chess.functions.move.pawn.Pawn;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;

public class StaleMate {
    public static boolean isStalemate(boolean is_white,ChessGame chessGame){
        if(!is_white) {
            for (Piece p : chessGame.black_rook) {
                if (!Rook.rookMoves(p.xy, false, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.black_knight) {
                if (!Knight.knightMoves(p.xy, false, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.black_bishop) {
                if (!Bishop.bishopMoves(p.xy, false, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.black_queen) {
                if (!Queen.queenMoves(p.xy, false, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.black_pawn) {
                if (!Pawn.pawnMoves(p.xy, false, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            if (!King.kingMoves(chessGame.black_king.xy, false, false,chessGame).isEmpty()) {
                return false;
            }

            return Check.checkCount(true, false,chessGame) == 0;
        } else {
            for (Piece p : chessGame.white_rook) {
                if (!Rook.rookMoves(p.xy, true, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.white_knight) {
                if (!Knight.knightMoves(p.xy, true, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.white_bishop) {
                if (!Bishop.bishopMoves(p.xy, true, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.white_queen) {
                if (!Queen.queenMoves(p.xy, true, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            for (Piece p : chessGame.white_pawn) {
                if (!Pawn.pawnMoves(p.xy, true, false,chessGame).isEmpty()) {
                    return false;
                }
            }
            if (!King.kingMoves(chessGame.white_king.xy, true, false,chessGame).isEmpty()) {
                return false;
            }

            return Check.checkCount(false, false,chessGame) == 0;
        }
    }
}
