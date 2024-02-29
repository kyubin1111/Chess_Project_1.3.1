package com.kyubin.chess.frames.chessengine.engines.custom;

import com.kyubin.chess.frames.chessengine.Engine;

import static com.kyubin.chess.Main.custom_engine_file;

public class CustomEngine extends Engine {
    public static void getCustomEngineChoice(boolean is_white) {
        getEngineChoice(is_white,custom_engine_file);
    }
}
