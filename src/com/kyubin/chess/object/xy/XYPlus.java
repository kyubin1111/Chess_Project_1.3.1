package com.kyubin.chess.object.xy;

public class XYPlus {
    public XY xy;
    public boolean is_en_passant;

    public XYPlus(XY xy, boolean is_en_passant) {
        this.xy = xy;
        this.is_en_passant = is_en_passant;
    }

    @Override
    public boolean equals(Object object) {
        XYPlus obj = (XYPlus) object;

        return obj.xy.x==xy.x&&obj.xy.y==xy.y;
    }
}
