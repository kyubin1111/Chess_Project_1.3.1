package com.kyubin.chess.functions.move.analysis;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.GoBestMoveFrame;
import com.kyubin.chess.frames.chessengine.Engine;
import com.kyubin.chess.frames.chessengine.EngineType;
import com.kyubin.chess.frames.chessengine.engines.KomodoEngine;
import com.kyubin.chess.frames.chessengine.engines.LeelaChessZero;
import com.kyubin.chess.frames.chessengine.engines.custom.CustomEngine;
import com.kyubin.chess.functions.move.analysis.convert.ConvertMove;
import com.kyubin.chess.frames.chessengine.engines.StockFishEngine;
import com.kyubin.chess.functions.GameFunctions;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.pawn.promotion.Promotion;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.game.MoveTo;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Stack;

import static com.kyubin.chess.Main.*;
import static com.kyubin.chess.functions.PieceFunctions.*;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.*;

public class Move {
    public static ChessGame MovePiece(boolean is_white, XY xy, XY xy_now, boolean is_king_side_castling, boolean is_queen_side_castling, boolean is_en_passant
    ,String object_type,JLabel object,boolean stockFishMode,ChessGame chessGame) {
        // 만약 갈 칸에 적군이 있다면 없에기
        String enemy_type=removeEnemy(xy_now,is_white,chessGame);

        Engine.bestMove="";

        if(object_type.contains("pawn")||!enemy_type.equals("")){
            chessGame.half_move.push(0);
        } else {
            chessGame.half_move.push(chessGame.half_move.peek()+1);
        }

        if(is_king_side_castling){
            if(is_white){
                for(Piece p : chessGame.white_rook){
                    if(p.xy.x==8&&p.xy.y==8){
                        p.xy.x=6;
                        if(Main.is_flip_board) p.label.setLocation(2*chess_object_size,0);
                        else p.label.setLocation(5*chess_object_size,7*chess_object_size);
                    }
                }
            } else {
                for(Piece p : chessGame.black_rook){
                    if(p.xy.x==8&&p.xy.y==1){
                        p.xy.x=6;
                        if(Main.is_flip_board) p.label.setLocation(2*chess_object_size,7*chess_object_size);
                        else p.label.setLocation(5*chess_object_size,0);
                    }
                }
            }
        }
        if(is_queen_side_castling){
            if(is_white){
                for(Piece p : chessGame.white_rook){
                    if(p.xy.x==1&&p.xy.y==8){
                        p.xy.x=4;
                        if(Main.is_flip_board) p.label.setLocation(5*chess_object_size,0);
                        else p.label.setLocation(3*chess_object_size,7*chess_object_size);
                    }
                }
            } else {
                for(Piece p : chessGame.black_rook){
                    if(p.xy.x==1&&p.xy.y==1){
                        p.xy.x=4;
                        if(Main.is_flip_board) p.label.setLocation(5*chess_object_size,7*chess_object_size);
                        else p.label.setLocation(3*chess_object_size,0);
                    }
                }
            }
        }

        if(is_en_passant){
            if(is_white){
                removeEnemy(new XY(xy_now.x,xy_now.y+1),true,chessGame);
            } else {
                removeEnemy(new XY(xy_now.x,xy_now.y-1),false,chessGame);
            }
        }

        chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,object_type,is_king_side_castling,is_queen_side_castling,
                is_en_passant,object,PieceFunctions.isPieceGoThere(xy_now,object_type,chessGame)));

        object.setLocation((!Main.is_flip_board?xy_now.x-1:9-(xy_now.x+1))*chess_object_size,(!Main.is_flip_board?xy_now.y-1:9-(xy_now.y+1))*chess_object_size);

        if(object_type.contains("king")){
            if(is_white){
                chessGame.white_king.label=object;
                chessGame.white_king.xy=xy_now;
            }
            if(!is_white){
                chessGame.black_king.label=object;
                chessGame.black_king.xy=xy_now;
            }
        } else {
            for(Piece p : PieceFunctions.stringToList(object_type,chessGame)){
                if(p.xy.equals(xy)) {
                    p.xy=xy_now;
                    break;
                }
            }
        }

        if(is_white) chessGame.white_turn=false;
        if(!is_white) chessGame.white_turn=true;

        if(!is_white) chessGame.full_move.push(chessGame.full_move.peek()+1);
        else chessGame.full_move.push(chessGame.full_move.peek());

        chessGame.fen_game.add(toFEN(chessGame));
        chessGame.fen_game_no_half_move.add(ConvertMove.toFENWithNoHalfMove(chessGame));

        if(chessGame.future_move.size()!=0){
            MoveTo future=chessGame.future_move.pop();
            if(!(future.now_xy.equals(xy_now)&&future.pre_xy.equals(xy))) chessGame.future_move=new Stack<>();
        }

        Engine.game=Engine.game+" "+ xyToLAN(xy)+ xyToLAN(xy_now);
        GoBestMoveFrame.time=0;

        if(Main.engine_mode &&stockFishMode) {
            new Thread(()->{
                if(Main.analysis_engine_type==EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(is_white);
                if(Main.analysis_engine_type==EngineType.KOMODO) KomodoEngine.getKomodoChoice(is_white);
                if(Main.analysis_engine_type==EngineType.LCO) LeelaChessZero.getLC0Choice(is_white);
                if(Main.analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(is_white);
            }).start();
        }

        return chessGame;
    }

    public static void UndoPiece(boolean is_stockFishMode,ChessGame chessGame){
        if(chessGame.game.size()==0) return;
        MoveTo move = chessGame.game.get(chessGame.game.size()-1);

        if(move.no_able_undo) return;

        Engine.bestMove="";

        chessGame.future_move.push(move);

        chessGame.fen_game.remove(chessGame.fen_game.size()-1);
        chessGame.fen_game_no_half_move.remove(chessGame.fen_game_no_half_move.size()-1);

        chessGame.half_move.pop();
        chessGame.full_move.pop();

        XY xy = move.pre_xy;
        XY xy_now = move.now_xy;
        boolean is_king_side_castling=move.is_ks;
        boolean is_queen_side_castling=move.is_qs;
        boolean is_en_passant=move.is_ep;
        String object_type=move.type;
        JLabel object=move.object;
        String enemy_type=move.enemy_type;

        boolean is_white = !chessGame.white_turn;

        removeEnemy(xy_now,is_white,chessGame);
        removeEnemy(xy_now,!is_white,chessGame);

        removePiece(xy,object_type,chessGame);
        removePiece(xy_now,object_type,chessGame);

        // 만약 갈 칸에 적군이 있다면 추가
        addPiece(enemy_type,xy_now,chessGame);

        if(is_king_side_castling){
            if(is_white){
                for(Piece p : chessGame.white_rook){
                    if(p.xy.x==6&&p.xy.y==8){
                        p.xy.x=8;
                        if(Main.is_flip_board) p.label.setLocation(0,0);
                        else p.label.setLocation(7*chess_object_size,7*chess_object_size);
                    }
                }
            } else {
                for(Piece p : chessGame.black_rook){
                    if(p.xy.x==6&&p.xy.y==1){
                        p.xy.x=8;
                        if(Main.is_flip_board) p.label.setLocation(0,7*chess_object_size);
                        else p.label.setLocation(7*chess_object_size,0);
                    }
                }
            }
        }
        if(is_queen_side_castling){
            if(is_white){
                for(Piece p : chessGame.white_rook){
                    if(p.xy.x==4&&p.xy.y==8){
                        p.xy.x=1;
                        if(Main.is_flip_board) p.label.setLocation(7*chess_object_size,0);
                        else p.label.setLocation(0,7*chess_object_size);
                    }
                }
            } else {
                for(Piece p : chessGame.black_rook){
                    if(p.xy.x==4&&p.xy.y==1){
                        p.xy.x=1;
                        if(Main.is_flip_board) p.label.setLocation(7*chess_object_size,7*chess_object_size);
                        else p.label.setLocation(0,0);
                    }
                }
            }
        }

        if(is_en_passant){
            if(is_white){
                addPiece("black_pawn",new XY(xy_now.x,xy_now.y+1),chessGame);
            } else {
                addPiece("white_pawn",new XY(xy_now.x,xy_now.y-1),chessGame);
            }
        }

        chessGame.game.remove(chessGame.game.size()-1);

        object.setLocation((!Main.is_flip_board?xy.x-1:9-(xy.x+1))*chess_object_size,(!Main.is_flip_board?xy.y-1:9-(xy.y+1))*chess_object_size);

        if(object_type.equals("white_rook")) chessGame.white_rook.add(new Piece(xy,object,"white_rook"));
        if(object_type.equals("black_rook")) chessGame.black_rook.add(new Piece(xy,object,"black_rook"));
        if(object_type.equals("white_knight")) chessGame.white_knight.add(new Piece(xy,object,"white_knight"));
        if(object_type.equals("black_knight")) chessGame.black_knight.add(new Piece(xy,object,"black_knight"));
        if(object_type.equals("white_bishop")) chessGame.white_bishop.add(new Piece(xy,object,"white_bishop"));
        if(object_type.equals("black_bishop")) chessGame.black_bishop.add(new Piece(xy,object,"black_bishop"));
        if(object_type.equals("white_queen")) chessGame.white_queen.add(new Piece(xy,object,"white_queen"));
        if(object_type.equals("black_queen")) chessGame.black_queen.add(new Piece(xy,object,"black_queen"));
        if(object_type.equals("white_pawn")) chessGame.white_pawn.add(new Piece(xy,object,"white_pawn"));
        if(object_type.equals("black_pawn")) chessGame.black_pawn.add(new Piece(xy,object,"black_pawn"));
        if(object_type.equals("white_king")) chessGame.white_king=new Piece(xy,object,"white_king");
        if(object_type.equals("black_king")) chessGame.black_king=new Piece(xy,object,"black_king");

        if(!is_white) chessGame.white_turn=false;
        if(is_white) chessGame.white_turn=true;

        Engine.bestMove="";
        GoBestMoveFrame.time=0;

        new Thread(()->{
            Engine.game=GameFunctions.removeRight(Engine.game);

            if(Main.engine_mode &&is_stockFishMode) {
                if(Main.analysis_engine_type==EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(!is_white);
                if(Main.analysis_engine_type==EngineType.KOMODO) KomodoEngine.getKomodoChoice(!is_white);
                if(Main.analysis_engine_type==EngineType.LCO) LeelaChessZero.getLC0Choice(!is_white);
                if(Main.analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(!is_white);
            }
        }).start();
    }


    public static void RedoMove(boolean is_stockFishMode,ChessGame chessGame){
        if(chessGame.future_move.size()==0) return;
        MoveTo future=chessGame.future_move.peek();
        MoveLAN.MovePiece(xyToLAN(future.pre_xy)+xyToLAN(future.now_xy)+(future.promotionType.equals(PromotionType.NONE)?"":
                Promotion.promotionTypeToChar(future.promotionType)),is_stockFishMode);

        Engine.bestMove="";
        GoBestMoveFrame.time=0;
    }

    public static ChessGame MovePromotionPiece(boolean is_white, XY xy, XY xy_now, PromotionType get_promotion_type, JLabel object,boolean is_stockFish_mode
    ,ChessGame chessGame){
        if(!is_white) chessGame.full_move.push(chessGame.full_move.peek()+1);
        else chessGame.full_move.push(chessGame.full_move.peek());

        String enemy_type = PieceFunctions.isPieceInSquareWithStringWithWhite(xy_now,!is_white,chessGame);

        removeEnemy(xy_now,is_white,chessGame);

        chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,is_white?"white_pawn":"black_pawn",false,false,false,object,new ArrayList<>(),get_promotion_type));

        if(chessGame.future_move.size()!=0){
            MoveTo future=chessGame.future_move.pop();
            if(!(future.now_xy.equals(xy_now)&&future.pre_xy.equals(xy)&&future.promotionType.equals(get_promotion_type))) chessGame.future_move=new Stack<>();
        }

        removePiece(xy,is_white?"white_pawn":"black_pawn",chessGame);

        if(is_white) chessGame.white_turn=false;
        if(!is_white) chessGame.white_turn=true;

        object.setLocation(-10000,-10000);

        chessGame.fen_game.add(toFEN(chessGame));
        chessGame.fen_game_no_half_move.add(ConvertMove.toFENWithNoHalfMove(chessGame));

        chessGame.half_move.push(0);

        Engine.game=Engine.game+" "+ xyToLAN(xy)+
                xyToLAN(xy_now)+String.valueOf(get_promotion_type.toString().charAt(0)).toLowerCase();

        Engine.bestMove="";

        GoBestMoveFrame.time=0;

        new Thread(()->{
            if(Main.engine_mode &&is_stockFish_mode) {
                if(Main.analysis_engine_type==EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(is_white);
                if(Main.analysis_engine_type==EngineType.KOMODO) KomodoEngine.getKomodoChoice(is_white);
                if(Main.analysis_engine_type==EngineType.LCO) LeelaChessZero.getLC0Choice(is_white);
                if(Main.analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(is_white);
            }
        }).start();

        return chessGame;
    }
}
