package com.kyubin.chess.frames.chessengine.engines;

import com.kyubin.chess.frames.chessengine.Engine;

import java.io.IOException;

import static com.kyubin.chess.Main.lc0_file;

public class LeelaChessZero extends Engine{
    public static void getLC0Choice(boolean is_white) {
        getEngineChoice(is_white,lc0_file);
    }
}
