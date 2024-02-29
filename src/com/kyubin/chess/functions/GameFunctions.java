package com.kyubin.chess.functions;

public class GameFunctions {
    public static String removeRight(String str){
        int lastSpaceIndex = str.lastIndexOf(' ');

        if (lastSpaceIndex != -1) {
            return str.substring(0, lastSpaceIndex);
        } else {
            return str;
        }
    }
}
