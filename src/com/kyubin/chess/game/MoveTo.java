package com.kyubin.chess.game;

import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.util.List;

public class MoveTo {
    public XY pre_xy;
    public XY now_xy;
    public String enemy_type;
    public String type;
    public JLabel object;
    public boolean is_ks;
    public boolean is_qs;
    public boolean is_ep;
    public boolean no_able_undo=false;
    public PromotionType promotionType=PromotionType.NONE;
    public List<XY> goingPiece;

    public MoveTo(XY pre_xy, XY now_xy, String enemy_type, String type, boolean is_ks, boolean is_qs, boolean is_ep, JLabel object,List<XY> goingPiece) {
        this.pre_xy = pre_xy;
        this.now_xy = now_xy;
        this.enemy_type = enemy_type;
        this.type = type;
        this.is_ks = is_ks;
        this.is_qs = is_qs;
        this.is_ep = is_ep;
        this.object = object;
        this.goingPiece=goingPiece;
    }

    public MoveTo(XY pre_xy, XY now_xy, String enemy_type, String type, boolean is_ks, boolean is_qs, boolean is_ep, JLabel object,boolean no_able_undo,List<XY> goingPiece) {
        this.pre_xy = pre_xy;
        this.now_xy = now_xy;
        this.enemy_type = enemy_type;
        this.type = type;
        this.is_ks = is_ks;
        this.is_qs = is_qs;
        this.is_ep = is_ep;
        this.object = object;
        this.no_able_undo = no_able_undo;
        this.goingPiece=goingPiece;
    }

    public MoveTo(XY pre_xy, XY now_xy, String enemy_type, String type, boolean is_ks, boolean is_qs, boolean is_ep,
                  JLabel object, List<XY> goingPiece, PromotionType promotionType) {
        this.pre_xy = pre_xy;
        this.now_xy = now_xy;
        this.enemy_type = enemy_type;
        this.type = type;
        this.is_ks = is_ks;
        this.is_qs = is_qs;
        this.is_ep = is_ep;
        this.object = object;
        this.goingPiece=goingPiece;
        this.promotionType=promotionType;
    }

    @Override
    public String toString() {
        return "MoveTo{" +
                "pre_xy=" + pre_xy +
                ", now_xy=" + now_xy +
                ", enemy_type='" + enemy_type + '\'' +
                ", type='" + type + '\'' +
                ", is_ks=" + is_ks +
                ", is_qs=" + is_qs +
                ", is_ep=" + is_ep +
                ", no_able_undo=" + no_able_undo +
                ", goingPiece=" + goingPiece +
                '}';
    }
}
