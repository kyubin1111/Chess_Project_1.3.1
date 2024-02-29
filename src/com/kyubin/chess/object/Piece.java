package com.kyubin.chess.object;

import com.kyubin.chess.object.xy.XY;

import javax.swing.*;

public class Piece {
    public XY xy;
    public JLabel label;
    public String type;

    public Piece(XY xy, JLabel label,String type){
        this.xy=xy;
        this.label=label;
        this.type=type;
    }

    @Override
    public boolean equals(Object object) {
        Piece obj = (Piece) object;

        return obj.xy.equals(xy)&&obj.type.equals(type);
    }
}
