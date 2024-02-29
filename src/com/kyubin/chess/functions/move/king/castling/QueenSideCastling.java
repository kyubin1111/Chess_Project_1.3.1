package com.kyubin.chess.functions.move.king.castling;

import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.game.MoveTo;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

public class QueenSideCastling {
    public static boolean isPossibleQueenSideCastling(boolean is_white,ChessGame chessGame, Piece king){
        if(is_white&&!chessGame.white_is_possible_queenSideCastling) return false;
        if(!is_white&&!chessGame.black_is_possible_queenSideCastling) return false;

        if(is_white) chessGame.white_king=king;
        else chessGame.black_king=king;

        for(MoveTo move : chessGame.game){
            if(is_white?move.now_xy.x==1&&move.now_xy.y==8:move.now_xy.x==1&&move.now_xy.y==1){
                return false;
            }
            if(move.type.contains(is_white?"white_king":"black_king")){
                return false;
            }
            if(move.type.contains(is_white?"white_rook":"black_rook")&&is_white?move.pre_xy.x==1&&move.pre_xy.y==8:move.pre_xy.x==1&&move.pre_xy.y==1){
                return false;
            }
        }
        if(is_white){
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.white_king.xy.x-1,
                    chessGame.white_king.xy.y),chessGame)) return false;
        } else {
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.black_king.xy.x-1,
                    chessGame.black_king.xy.y),chessGame)) return false;
        }
        if(is_white){
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.white_king.xy.x-2,
                    chessGame.white_king.xy.y),chessGame)) return false;
        } else {
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.black_king.xy.x-2,
                    chessGame.black_king.xy.y),chessGame)) return false;
        }
        if(is_white){
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.white_king.xy.x-3,
                    chessGame.white_king.xy.y),chessGame)) return false;
        } else {
            if(PieceFunctions.isPieceInSquare(new XY(chessGame.black_king.xy.x-3,
                    chessGame.black_king.xy.y),chessGame)) return false;
        }
        if(is_white){
            if(!PieceFunctions.isPieceGoThere(new XY(4,8),false,chessGame).isEmpty()) return false;
            return PieceFunctions.isPieceGoThere(new XY(3, 8), false, chessGame).isEmpty();
        } else {
            if(!PieceFunctions.isPieceGoThere(new XY(4,1),true,chessGame).isEmpty()) return false;
            return PieceFunctions.isPieceGoThere(new XY(3, 1), true, chessGame).isEmpty();
        }
    }
}
