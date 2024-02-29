package com.kyubin.chess.functions.draw;

import com.kyubin.chess.game.ChessGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripleRepetition {
    public static boolean isTripleRepetition(ChessGame chessGame){
        List<String> list = new ArrayList<>(chessGame.fen_game_no_half_move);
        HashMap<Object, Integer> itemCounts = new HashMap<>();
        for (Object item : list) {
            itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
            if (itemCounts.get(item) >= 3) {
                return true; // 3개 이상의 동일한 항목이 발견되면 즉시 true 반환
            }
        }
        return false;
    }
}
