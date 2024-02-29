package com.kyubin.chess.functions.move.analysis;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.GoBestMoveFrame;
import com.kyubin.chess.frames.chessengine.Engine;
import com.kyubin.chess.functions.PieceFunctions;
import com.kyubin.chess.functions.move.analysis.convert.ConvertMove;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.game.ChessGame;
import com.kyubin.chess.game.MoveTo;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.kyubin.chess.Main.chess_object_size;
import static com.kyubin.chess.functions.PieceFunctions.*;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.toFEN;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.xyToLAN;

public class MoveFake {
    public static ChessGame MovePiece(XY xy, XY xy_now, ChessGame chessGame, PromotionType promotionType) {
        boolean is_white=chessGame.white_turn;

        String object_type=PieceFunctions.isPieceInSquareWithString(xy,chessGame);

        boolean is_en_passant= PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(xy_now.x, is_white?xy_now.y+1:xy_now.y-1),!is_white,chessGame).contains("pawn")
                &&object_type.contains("pawn")&&
                PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(xy_now.x, xy_now.y),!is_white,chessGame).equals("");

        String enemy_type=removeEnemyFake(xy_now,is_white,chessGame);

        boolean is_king_side_castling=object_type.contains("king")&&xy_now.x-xy.x==2;
        boolean is_queen_side_castling=object_type.contains("king")&&xy_now.x-xy.x==-2;

        if(promotionType!=PromotionType.NONE&&object_type.contains("pawn")&&xy_now.y==(is_white?1:8))
            return MovePromotionPieceFake(is_white,xy,xy_now,promotionType,chessGame);
        else {
            removePieceFake(xy,object_type,chessGame);

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
                        }
                    }
                } else {
                    for(Piece p : chessGame.black_rook){
                        if(p.xy.x==8&&p.xy.y==1){
                            p.xy.x=6;
                        }
                    }
                }
            }
            if(is_queen_side_castling){
                if(is_white){
                    for(Piece p : chessGame.white_rook){
                        if(p.xy.x==1&&p.xy.y==8){
                            p.xy.x=4;
                        }
                    }
                } else {
                    for(Piece p : chessGame.black_rook){
                        if(p.xy.x==1&&p.xy.y==1){
                            p.xy.x=4;
                        }
                    }
                }
            }

            if(is_en_passant){
                if(is_white){
                    removeEnemyFake(new XY(xy_now.x,xy_now.y+1),true,chessGame);
                } else {
                    removeEnemyFake(new XY(xy_now.x,xy_now.y-1),false,chessGame);
                }
            }

            chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,object_type,is_king_side_castling,is_queen_side_castling,
                    is_en_passant,new JLabel(),PieceFunctions.isPieceGoThere(xy_now,object_type,chessGame)));

            if(object_type.equals("white_rook")) chessGame.white_rook.add(new Piece(xy_now,new JLabel(),"white_rook"));
            if(object_type.equals("black_rook")) chessGame.black_rook.add(new Piece(xy_now,new JLabel(),"black_rook"));
            if(object_type.equals("white_knight")) chessGame.white_knight.add(new Piece(xy_now,new JLabel(),"white_knight"));
            if(object_type.equals("black_knight")) chessGame.black_knight.add(new Piece(xy_now,new JLabel(),"black_knight"));
            if(object_type.equals("white_bishop")) chessGame.white_bishop.add(new Piece(xy_now,new JLabel(),"white_bishop"));
            if(object_type.equals("black_bishop")) chessGame.black_bishop.add(new Piece(xy_now,new JLabel(),"black_bishop"));
            if(object_type.equals("white_queen")) chessGame.white_queen.add(new Piece(xy_now,new JLabel(),"white_queen"));
            if(object_type.equals("black_queen")) chessGame.black_queen.add(new Piece(xy_now,new JLabel(),"black_queen"));
            if(object_type.equals("white_pawn")) chessGame.white_pawn.add(new Piece(xy_now,new JLabel(),"white_pawn"));
            if(object_type.equals("black_pawn")) chessGame.black_pawn.add(new Piece(xy_now,new JLabel(),"black_pawn"));
            if(object_type.equals("white_king")) chessGame.white_king=new Piece(xy_now,new JLabel(),"white_king");
            if(object_type.equals("black_king")) chessGame.black_king=new Piece(xy_now,new JLabel(),"black_king");

            if(is_white) chessGame.white_turn=false;
            if(!is_white) chessGame.white_turn=true;

            if(!is_white) chessGame.full_move.push(chessGame.full_move.peek()+1);
            else chessGame.full_move.push(chessGame.full_move.peek());

            chessGame.fen_game.add(toFEN(chessGame));
            chessGame.fen_game_no_half_move.add(ConvertMove.toFENWithNoHalfMove(chessGame));

            return chessGame;
        }
    }

    public static ChessGame MovePiece2(XY xy, XY xy_now, ChessGame chessGame, PromotionType promotionType) {
        boolean is_white=chessGame.white_turn;

        String object_type=PieceFunctions.isPieceInSquareWithString(xy,chessGame);

        boolean is_en_passant= PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(xy_now.x, is_white?xy_now.y+1:xy_now.y-1),!is_white,chessGame).contains("pawn")
                &&object_type.contains("pawn")&&
                PieceFunctions.isPieceInSquareWithStringWithWhite(new XY(xy_now.x, xy_now.y),!is_white,chessGame).equals("");

        String enemy_type=removeEnemy(xy_now,is_white,chessGame);

        boolean is_king_side_castling=object_type.contains("king")&&xy_now.x-xy.x==2;
        boolean is_queen_side_castling=object_type.contains("king")&&xy_now.x-xy.x==-2;

        JLabel object=getObject(xy,chessGame);

        if(object_type.equals("white_king")) object.setLocation((chessGame.white_king.xy.x-1)*chess_object_size,(chessGame.white_king.xy.y-1)*chess_object_size);
        if(object_type.equals("black_king")) object.setLocation((chessGame.black_king.xy.x-1)*chess_object_size,(chessGame.black_king.xy.y-1)*chess_object_size);

        for(Component j : Main.frame.getContentPane().getComponents()){
            if(object.equals(j)){
                break;
            } else if(object.getX()==j.getX()&&object.getY()==j.getY()&&!j.equals(Main.boardImage)){
                object = (JLabel) j;
            }
        }

        if(promotionType!=PromotionType.NONE&&object_type.contains("pawn")&&xy_now.y==(is_white?1:8)){
            addPiece(is_white?"white_":"black_"+promotionType.toString(),xy_now,chessGame);

            enemy_type = PieceFunctions.isPieceInSquareWithStringWithWhite(xy_now,!is_white,chessGame);

            removeEnemy(xy_now,is_white,chessGame);

            chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,is_white?"white_pawn":"black_pawn",false,false,false,object,new ArrayList<>()));

            removePiece(xy,is_white?"white_pawn":"black_pawn",chessGame);

            Engine.game=Engine.game+" "+ xyToLAN(xy)+
                    xyToLAN(xy_now)+String.valueOf(promotionType.toString().charAt(0)).toLowerCase();

            Engine.bestMove="";

            GoBestMoveFrame.time=0;

            if(is_white) chessGame.white_turn=false;
            if(!is_white) chessGame.white_turn=true;
        }
        else {
            removePiece(xy,object_type,chessGame);

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
                        }
                    }
                } else {
                    for(Piece p : chessGame.black_rook){
                        if(p.xy.x==8&&p.xy.y==1){
                            p.xy.x=6;
                        }
                    }
                }
            }
            if(is_queen_side_castling){
                if(is_white){
                    for(Piece p : chessGame.white_rook){
                        if(p.xy.x==1&&p.xy.y==8){
                            p.xy.x=4;
                        }
                    }
                } else {
                    for(Piece p : chessGame.black_rook){
                        if(p.xy.x==1&&p.xy.y==1){
                            p.xy.x=4;
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

            object.setLocation((!Main.is_flip_board?xy_now.x-1:9-(xy_now.x+1))*chess_object_size,(!Main.is_flip_board?xy_now.y-1:9-(xy_now.y+1))*chess_object_size);

            chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,object_type,is_king_side_castling,is_queen_side_castling,
                    is_en_passant,object,PieceFunctions.isPieceGoThere(xy_now,object_type,chessGame)));

            if(object_type.equals("white_rook")) chessGame.white_rook.add(new Piece(xy_now,object,"white_rook"));
            if(object_type.equals("black_rook")) chessGame.black_rook.add(new Piece(xy_now,object,"black_rook"));
            if(object_type.equals("white_knight")) chessGame.white_knight.add(new Piece(xy_now,object,"white_knight"));
            if(object_type.equals("black_knight")) chessGame.black_knight.add(new Piece(xy_now,object,"black_knight"));
            if(object_type.equals("white_bishop")) chessGame.white_bishop.add(new Piece(xy_now,object,"white_bishop"));
            if(object_type.equals("black_bishop")) chessGame.black_bishop.add(new Piece(xy_now,object,"black_bishop"));
            if(object_type.equals("white_queen")) chessGame.white_queen.add(new Piece(xy_now,object,"white_queen"));
            if(object_type.equals("black_queen")) chessGame.black_queen.add(new Piece(xy_now,object,"black_queen"));
            if(object_type.equals("white_pawn")) chessGame.white_pawn.add(new Piece(xy_now,object,"white_pawn"));
            if(object_type.equals("black_pawn")) chessGame.black_pawn.add(new Piece(xy_now,object,"black_pawn"));
            if(object_type.equals("white_king")) chessGame.white_king=new Piece(xy_now,object,"white_king");
            if(object_type.equals("black_king")) chessGame.black_king=new Piece(xy_now,object,"black_king");

            if(is_white) chessGame.white_turn=false;
            if(!is_white) chessGame.white_turn=true;

            if(!is_white) chessGame.full_move.push(chessGame.full_move.peek()+1);
            else chessGame.full_move.push(chessGame.full_move.peek());

            chessGame.fen_game.add(toFEN(chessGame));
            chessGame.fen_game_no_half_move.add(ConvertMove.toFENWithNoHalfMove(chessGame));

            Engine.game=Engine.game+" "+ xyToLAN(xy)+ xyToLAN(xy_now);
            GoBestMoveFrame.time=0;
        }
        return chessGame;
    }

    public static ChessGame MovePromotionPieceFake(boolean is_white, XY xy, XY xy_now,
                                              PromotionType get_promotion_type, ChessGame chessGame) {
        addPieceFake(is_white?"white_":"black_"+get_promotion_type.toString(),xy_now,chessGame);

        String enemy_type = PieceFunctions.isPieceInSquareWithStringWithWhite(xy_now,!is_white,chessGame);

        removeEnemyFake(xy_now,is_white,chessGame);

        chessGame.game.add(new MoveTo(xy,xy_now,enemy_type,is_white?"white_pawn":"black_pawn",false,false,false,new JLabel(),new ArrayList<>()));

        removePieceFake(xy,is_white?"white_pawn":"black_pawn",chessGame);

        if(is_white) chessGame.white_turn=false;
        if(!is_white) chessGame.white_turn=true;

        return chessGame;
    }

    public static void addPieceFake(String object_type,XY xy,ChessGame chessGame){
        boolean white=object_type.contains("white");

        if(object_type.contains("queen")){
            if(white){
                chessGame.white_queen.add(new Piece(xy,new JLabel(),"white_queen"));
            } else {
                chessGame.black_queen.add(new Piece(xy,new JLabel(),"black_queen"));
            }
        }
        if(object_type.contains("bishop")){
            if(white){
                chessGame.white_bishop.add(new Piece(xy,new JLabel(),"white_bishop"));
            } else {
                chessGame.black_bishop.add(new Piece(xy,new JLabel(),"black_bishop"));
            }
        }
        if(object_type.contains("knight")){
            if(white){
                chessGame.white_knight.add(new Piece(xy,new JLabel(),"white_knight"));
            } else {
                chessGame.black_knight.add(new Piece(xy,new JLabel(),"black_knight"));
            }
        }
        if(object_type.contains("rook")){
            if(white){
                chessGame.white_rook.add(new Piece(xy,new JLabel(),"white_rook"));
            } else {
                chessGame.black_rook.add(new Piece(xy,new JLabel(),"black_rook"));
            }
        }
        if(object_type.contains("pawn")){
            if(white){
                chessGame.white_pawn.add(new Piece(xy,new JLabel(),"white_pawn"));
            } else {
                chessGame.black_pawn.add(new Piece(xy,new JLabel(),"black_pawn"));
            }
        }
    }

    public static void removePieceFake(XY xy, String object_type,ChessGame chessGame){
        if(object_type.equals("white_rook")){
            for(Piece piece: chessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_rook.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("black_rook")){
            for(Piece piece: chessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_rook.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("white_knight")){
            for(Piece piece: chessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_knight.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("black_knight")){
            for(Piece piece: chessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_knight.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("white_bishop")){
            for(Piece piece:chessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_bishop.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("black_bishop")){
            for(Piece piece: chessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_bishop.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("white_queen")){
            for(Piece piece: chessGame.white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_queen.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("black_queen")){
            for(Piece piece: chessGame.black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_queen.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("white_pawn")){
            for(Piece piece: chessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_pawn.remove(piece);
                    break;
                }
            }
        }
        if(object_type.equals("black_pawn")){
            for(Piece piece: chessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_pawn.remove(piece);
                    break;
                }
            }
        }
    }

    public static String removeEnemyFake(XY xy, boolean is_white, ChessGame chessGame){
        if(is_white){
            for(Piece piece: chessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_rook.remove(piece);
                    return "black_rook";
                }
            }
            for(Piece piece: chessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_knight.remove(piece);
                    return "black_knight";
                }
            }
            for(Piece piece: chessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_bishop.remove(piece);
                    return "black_bishop";
                }
            }
            for(Piece piece: chessGame.black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_queen.remove(piece);
                    return "black_queen";
                }
            }
            for(Piece piece: chessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.black_pawn.remove(piece);
                    return "black_pawn";
                }
            }
        } else {
            for(Piece piece: chessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_rook.remove(piece);
                    return "white_rook";
                }
            }
            for(Piece piece: chessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_knight.remove(piece);
                    return "white_knight";
                }
            }
            for(Piece piece: chessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_bishop.remove(piece);
                    return "white_bishop";
                }
            }
            for(Piece piece: chessGame.white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_queen.remove(piece);
                    return "white_queen";
                }
            }
            for(Piece piece: chessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    chessGame.white_pawn.remove(piece);
                    return "white_pawn";
                }
            }
        }

        return "";
    }
}
