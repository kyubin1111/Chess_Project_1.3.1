package com.kyubin.chess.functions.move.analysis.convert;

import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.game.MoveTo;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.LANToXY;

public class GetFEN {
    public static void parseFEN(String fen, Container container, ChessGame chessGame) throws LoadFENFailedException {
        try {
            String[] rows = fen.split(" ")[0].split("/");
            for (int y = 0; y < rows.length; y++) {
                int x = 0;
                for (char c : rows[y].toCharArray()) {
                    if (Character.isDigit(c)) {
                        x += Character.getNumericValue(c);
                    } else {
                        x++;
                        y++;

                        if (c == 'P') chessGame.Pawn(container, true, x,y);
                        else if (c == 'p') chessGame.Pawn(container, false, x,y);
                        if (c == 'R') chessGame.Rook(container, true, x,y);
                        else if (c == 'r') chessGame.Rook(container, false, x,y);
                        if (c == 'N') chessGame.Knight(container, true, x,y);
                        else if (c == 'n') chessGame.Knight(container, false, x,y);
                        if (c == 'B') chessGame.Bishop(container, true, x,y);
                        else if (c == 'b') chessGame.Bishop(container, false, x,y);
                        if (c == 'Q') chessGame.Queen(container, true, x,y);
                        else if (c == 'q') chessGame.Queen(container, false, x,y);
                        if (c == 'K') chessGame.King(container, true, x,y);
                        else if (c == 'k') chessGame.King(container, false, x,y);

                        x--;
                        y--;

                        x++;
                    }
                }
            }

            String[] parts = fen.split(" ");
            chessGame.white_turn=parts[1].equals("w");

            String castlingRights = parts[2];

            chessGame.white_is_possible_kingSideCastling=castlingRights.contains("K");
            chessGame.white_is_possible_queenSideCastling=castlingRights.contains("Q");
            chessGame.black_is_possible_kingSideCastling=castlingRights.contains("k");
            chessGame.black_is_possible_queenSideCastling=castlingRights.contains("q");

            if(!parts[3].equals("-")){
                XY pre_enpassant_move=!chessGame.white_turn?new XY(LANToXY(parts[3]).x, LANToXY(parts[3]).y-1):
                        new XY(LANToXY(parts[3]).x, LANToXY(parts[3]).y+1);
                XY now_enpassant_move=!chessGame.white_turn?new XY(LANToXY(parts[3]).x, LANToXY(parts[3]).y+1):
                        new XY(LANToXY(parts[3]).x, LANToXY(parts[3]).y-1);
                chessGame.game.add(new MoveTo(pre_enpassant_move,now_enpassant_move,"",(chessGame.white_turn?"black":"white")+"_pawn",false,false,
                        false,new JLabel(),true, new ArrayList<>()));
            }

            chessGame.half_move=new Stack<>();
            chessGame.half_move.push(Integer.valueOf(parts[4]));
            chessGame.full_move=new Stack<>();
            chessGame.full_move.push(Integer.valueOf(parts[5]));

            chessGame.fen_game=new ArrayList<>();
            chessGame.fen_game_no_half_move=new ArrayList<>();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void parseFENOnlyPieces(String fen, Container container, ChessGame chessGame) {
        try {
            String[] rows = fen.split(" ")[0].split("/");
            for (int y = 0; y < rows.length; y++) {
                int x = 0;
                for (char c : rows[y].toCharArray()) {
                    if (Character.isDigit(c)) {
                        x += Character.getNumericValue(c);
                    } else {
                        x++;
                        y++;

                        if (c == 'P') chessGame.Pawn(container, true, x,y);
                        else if (c == 'p') chessGame.Pawn(container, false, x,y);
                        if (c == 'R') chessGame.Rook(container, true, x,y);
                        else if (c == 'r') chessGame.Rook(container, false, x,y);
                        if (c == 'N') chessGame.Knight(container, true, x,y);
                        else if (c == 'n') chessGame.Knight(container, false, x,y);
                        if (c == 'B') chessGame.Bishop(container, true, x,y);
                        else if (c == 'b') chessGame.Bishop(container, false, x,y);
                        if (c == 'Q') chessGame.Queen(container, true, x,y);
                        else if (c == 'q') chessGame.Queen(container, false, x,y);
                        if (c == 'K') chessGame.King(container, true, x,y);
                        else if (c == 'k') chessGame.King(container, false, x,y);

                        x--;
                        y--;

                        x++;
                    }
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
