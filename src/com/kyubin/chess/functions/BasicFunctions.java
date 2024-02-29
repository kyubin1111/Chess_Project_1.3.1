package com.kyubin.chess.functions;

import com.kyubin.chess.Main;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BasicFunctions {
    public static String removeString(String str, List<String> removeStrs) {
        for(String removeStr : removeStrs) {
            str = str.replace(removeStr, "");
        }
        return str;
    }

    public static List<XY> addAll(List<Piece> rook, List<Piece> knight, List<Piece> bishop, List<Piece> queen, List<Piece> pawn, Piece king){
        List<XY> pieces=new ArrayList<>();

        for(Piece piece : rook){
            pieces.add(piece.xy);
        }
        for(Piece piece : knight){
            pieces.add(piece.xy);
        }
        for(Piece piece : bishop){
            pieces.add(piece.xy);
        }
        for(Piece piece : queen){
            pieces.add(piece.xy);
        }
        for(Piece piece : pawn){
            pieces.add(piece.xy);
        }
        try {
            pieces.add(king.xy);
        } catch (NullPointerException ignored){}

        return pieces;
    }

    public static List<Piece> addAllWithPiece(List<Piece> rook, List<Piece> knight, List<Piece> bishop, List<Piece> queen, List<Piece> pawn, Piece king){
        List<Piece> pieces=new ArrayList<>();

        pieces.addAll(rook);
        pieces.addAll(knight);
        pieces.addAll(bishop);
        pieces.addAll(queen);
        pieces.addAll(pawn);
        try {
            pieces.add(king);
        } catch (NullPointerException ignored){}

        return pieces;
    }

    // 정렬 함수
    public static XY sortObject(int square_size, int x, int y){
        if(Main.is_flip_board){
            return new XY(9-(int) Math.round((double) (x+square_size/2) / (double) square_size),
                    9-(int) Math.round((double) y / (double) square_size));
        }
        return new XY((int) Math.round((double) (x+square_size/2) / (double) square_size),
                (int) Math.round((double) y / (double) square_size));
    }

    // 정렬 함수2
    public static XY sortXY(int square_size, int x, int y){
        if(Main.is_flip_board){
            return new XY(9-(int) Math.round((double) x / (double) square_size)-1,
                    9-(int) Math.round((double) y / (double) square_size)-1);
        }
        return new XY((int) Math.round((double) x / (double) square_size)+1,
                (int) Math.round((double) y / (double) square_size)+1);
    }

    // 이미지 불러오는 함수
    public static JLabel loadImage(int x, int y, int collision_wight, int collision_height, int image_wight, int image_height, String filename){
        ImageIcon imageIcon;
        imageIcon = new ImageIcon(filename);
        Image originalImage = imageIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(image_wight, image_height, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setBounds(x, y, collision_wight,collision_height);
        Main.frame.add(imageLabel);

        return imageLabel;
    }
}
