package com.kyubin.chess.functions.move.analysis.convert;

public class LoadFENFailedException extends Exception{
    public LoadFENFailedException() {
        super("Load failed with not enough / index");
    }
}
