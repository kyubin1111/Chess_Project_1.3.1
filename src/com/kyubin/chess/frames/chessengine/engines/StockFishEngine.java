package com.kyubin.chess.frames.chessengine.engines;

import com.kyubin.chess.frames.chessengine.Engine;

import java.io.*;

import static com.kyubin.chess.Main.*;

public class StockFishEngine extends Engine {
    public static void getStockFishChoice(boolean is_white) {
        getEngineChoice(is_white,stockfish_file);
    }
}
