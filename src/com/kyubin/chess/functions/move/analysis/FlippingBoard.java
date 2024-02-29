package com.kyubin.chess.functions.move.analysis;

import com.kyubin.chess.Main;
import com.kyubin.chess.object.Piece;

import static com.kyubin.chess.Main.chessGame;
import static com.kyubin.chess.Main.chess_object_size;

public class FlippingBoard {
    public static void flipBoard(){
        for(Piece p : chessGame.white_pawn) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.black_pawn) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.white_rook) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.black_rook) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.white_bishop) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.black_bishop) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.white_knight) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.black_knight) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.white_queen) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);
        for(Piece p : chessGame.black_queen) p.label.setLocation((7-p.label.getX()/chess_object_size)*chess_object_size,(7-p.label.getY()/chess_object_size)*chess_object_size);

        chessGame.white_king.label.setLocation((7-chessGame.white_king.label.getX()/chess_object_size)*chess_object_size
                ,(7-chessGame.white_king.label.getY()/chess_object_size)*chess_object_size);
        chessGame.black_king.label.setLocation((7-chessGame.black_king.label.getX()/chess_object_size)*chess_object_size
                ,(7-chessGame.black_king.label.getY()/chess_object_size)*chess_object_size);

        Main.is_flip_board=!Main.is_flip_board;
    }
}
