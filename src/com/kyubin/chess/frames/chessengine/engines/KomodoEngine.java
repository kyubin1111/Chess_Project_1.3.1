package com.kyubin.chess.frames.chessengine.engines;

import com.kyubin.chess.frames.chessengine.Engine;
import java.io.IOException;

import static com.kyubin.chess.Main.*;

public class KomodoEngine extends Engine {
    public static void getKomodoChoice(boolean is_white) {
        getEngineChoice(is_white,komodo_file);
    }
}
