package com.kyubin.chess.frames;

import javax.swing.*;
import java.awt.*;

public class HeightOverlayChartFrame extends JPanel {
    private double value = 0; // 차트에 그릴 값
    private boolean white_mate = false;
    private boolean black_mate = false;

    private final int multiply=100;

    private static final int real_max=7;
    private static final int real_min=-7;

    private final int max=real_max*multiply;
    private final int min=real_min*multiply;

    private int width=getWidth();
    private int height=getHeight();

    private final int divide=20;

    public HeightOverlayChartFrame() {
        setPreferredSize(new Dimension(200, 200)); // 적당한 패널 크기 설정
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int max2=max-min;

        int barHeight = (int) (Math.abs(value) * height / max2);
        int barY = height - barHeight;
        int barYHeight2= (int) ((max2-Math.abs(value)) * height / max2);

        if(barHeight>=height&&!white_mate){
            barHeight=height-height/divide;
            barY=height - barHeight;
            barYHeight2=height/divide;
        } else if(barHeight<=0){
            barHeight = height / divide;
            barY = height - height / divide;
            barYHeight2 = height - height / divide;
        }

        if(black_mate){
            barHeight=0;
            barYHeight2=height;
        }

        g.setColor(Color.white);
        g.fillRect(0, barY, width, barHeight);

        g.setColor(Color.black);
        g.fillRect(0,0,width, barYHeight2);

        g.setColor(Color.RED);
        g.fillRect(0,height/2,width, 5);
    }

    public void setHeight(int height){
        this.height=height;
    }

    public void setWidth(int width){
        this.width=width;
    }

    public void setValue(double newValue,boolean white_mate,boolean black_mate) {
        this.value = (newValue*multiply-min);
        this.white_mate=white_mate;
        this.black_mate=black_mate;
        repaint();
    }
}