package com.kyubin.chess.object.xy;

public class XY {
    public int x,y;

    public XY(int x,int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString() {
        return x+" "+y;
    }

    @Override
    public boolean equals(Object object) {
        XY obj = (XY) object;

        return obj.x==x&&obj.y==y;
    }
}
