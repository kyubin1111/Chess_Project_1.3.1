package com.kyubin.chess.functions.move.check;

import com.kyubin.chess.functions.BasicFunctions;
import com.kyubin.chess.functions.move.*;
import com.kyubin.chess.functions.move.king.King;
import com.kyubin.chess.functions.move.pawn.Pawn;
import com.kyubin.chess.object.xy.XYPlus;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import java.util.ArrayList;
import java.util.List;

public class Check {
    public static int checkCount(boolean is_white,boolean no_repeat,ChessGame chessGame) {
        int check_count = 0;
        XY kingPosition = is_white ? chessGame.black_king.xy : chessGame.white_king.xy;

        // 각 색깔의 말들에 대한 배열 생성
        List<Piece> pieces;

        if(is_white){
            pieces=BasicFunctions.addAllWithPiece(chessGame.white_rook, chessGame.white_knight, chessGame.white_bishop, chessGame.white_queen,
                    chessGame.white_pawn,chessGame.white_king);
        } else {
            pieces=BasicFunctions.addAllWithPiece(chessGame.black_rook, chessGame.black_knight, chessGame.black_bishop, chessGame.black_queen,
                    chessGame.black_pawn,chessGame.black_king);
        }

        // 각 말이 킹을 체크할 수 있는지 확인
        for (Piece piece : pieces) {
            if(piece!=null) if (canAttackKing(piece, kingPosition,no_repeat,chessGame,is_white)) {
                check_count++;
            }
        }

        return check_count;
    }

    private static boolean canAttackKing(Piece piece, XY kingPosition, boolean no_repeat,ChessGame chessGame, boolean isWhite) {
        List<XY> moves;
        moves = switch (piece.type) {
            case "white_rook", "black_rook" -> Rook.rookMoves(piece.xy, isWhite,no_repeat,chessGame);
            case "white_knight", "black_knight" -> Knight.knightMoves(piece.xy, isWhite,no_repeat,chessGame);
            case "white_bishop", "black_bishop" -> Bishop.bishopMoves(piece.xy, isWhite,no_repeat,chessGame);
            case "white_queen", "black_queen" -> Queen.queenMoves(piece.xy, isWhite,no_repeat,chessGame);
            case "white_pawn", "black_pawn" -> xyPlusToXY(Pawn.pawnMoves(piece.xy, isWhite,no_repeat,chessGame));
            case "white_king", "black_king" -> King.kingMoves(piece.xy, isWhite,no_repeat,chessGame);
            default -> new ArrayList<>();
        };

        boolean is_check=false;

        for(XY xy:moves){
            if(xy.equals(kingPosition)){
                is_check=true;
                break;
            }
        }

        return is_check;
    }

    private static List<XY> xyPlusToXY(List<XYPlus> xyPlusList){
        List<XY> xyList=new ArrayList<>();

        for(XYPlus xyPlus : xyPlusList){
            xyList.add(xyPlus.xy);
        }
        return xyList;
    }
}
