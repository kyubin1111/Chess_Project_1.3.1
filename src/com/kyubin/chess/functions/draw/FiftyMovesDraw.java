package com.kyubin.chess.functions.draw;

import com.kyubin.chess.game.ChessGame;

public class FiftyMovesDraw {
    public static boolean isFiftyMovesDraw(ChessGame chessGame){
        return chessGame.half_move.peek() == 100;
    }
}
