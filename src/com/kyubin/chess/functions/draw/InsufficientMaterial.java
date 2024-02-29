package com.kyubin.chess.functions.draw;

import com.kyubin.chess.game.ChessGame;

public class InsufficientMaterial {
    public static boolean isInsufficientMaterial(ChessGame chessGame){
        if(chessGame.white_pawn.size()!=0) return false;
        if(chessGame.black_pawn.size()!=0) return false;
        if(chessGame.white_rook.size()!=0) return false;
        if(chessGame.black_rook.size()!=0) return false;
        if(chessGame.white_queen.size()!=0) return false;
        if(chessGame.black_queen.size()!=0) return false;

        int white_bishop_count=chessGame.white_bishop.size();
        int black_bishop_count=chessGame.black_bishop.size();
        int white_knight_count=chessGame.white_knight.size();
        int black_knight_count=chessGame.black_knight.size();

        if(white_bishop_count>1) return false;
        if(black_bishop_count>1) return false;
        if(white_knight_count>1) return false;
        if(black_knight_count>1) return false;

        if(white_bishop_count==0&&black_bishop_count==1&&white_knight_count==0&&black_knight_count==1) return true;
        if(white_bishop_count==1&&black_bishop_count==0&&white_knight_count==1&&black_knight_count==0) return true;
        if(white_bishop_count==0&&black_bishop_count==1&&white_knight_count==1&&black_knight_count==0) return true;
        if(white_bishop_count==1&&black_bishop_count==0&&white_knight_count==0&&black_knight_count==1) return true;
        return white_bishop_count == 0 && black_bishop_count == 0 && white_knight_count == 0 && black_knight_count == 0;
    }
}
