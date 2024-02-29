package com.kyubin.chess.functions.move.analysis;

import com.kyubin.chess.Main;
import com.kyubin.chess.frames.GoBestMoveFrame;
import com.kyubin.chess.frames.chessengine.Engine;
import com.kyubin.chess.frames.chessengine.EngineType;
import com.kyubin.chess.frames.chessengine.engines.KomodoEngine;
import com.kyubin.chess.frames.chessengine.engines.LeelaChessZero;
import com.kyubin.chess.frames.chessengine.engines.StockFishEngine;
import com.kyubin.chess.frames.chessengine.engines.custom.CustomEngine;
import com.kyubin.chess.functions.move.pawn.promotion.Promotion;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.object.xy.XY;

import static com.kyubin.chess.Main.chessGame;
import static com.kyubin.chess.functions.move.analysis.convert.ConvertMove.*;

public class MoveLAN {
    public static void MovePiece(String lan, boolean engine_mode) {
        Engine.bestMove="";

        XY xy=LANToXY(lan.substring(0,2));
        XY xy_now=LANToXY(lan.substring(2,4));
        PromotionType promotionType=PromotionType.NONE;
        try {
            promotionType= Promotion.charToPromotionType(lan.charAt(4));
        } catch (Exception ignored){}

        boolean is_white=chessGame.white_turn;

        MoveFake.MovePiece2(xy,xy_now,chessGame,promotionType);

        if(chessGame.future_move.size()!=0) chessGame.future_move.pop();

        chessGame.reload();
        GoBestMoveFrame.time=0;

        if(engine_mode&&Main.engine_mode) new Thread(()->{
            Engine.bestMove="";

            GoBestMoveFrame.time++;
            do {
                if (GoBestMoveFrame.time == 0) return;
            } while (GoBestMoveFrame.time > 500);

            if(Main.analysis_engine_type== EngineType.STOCKFISH) StockFishEngine.getStockFishChoice(is_white);
            if(Main.analysis_engine_type==EngineType.KOMODO) KomodoEngine.getKomodoChoice(is_white);
            if(Main.analysis_engine_type==EngineType.LCO) LeelaChessZero.getLC0Choice(is_white);
            if(Main.analysis_engine_type == EngineType.CUSTOM) CustomEngine.getCustomEngineChoice(is_white);
        }).start();
    }
}
