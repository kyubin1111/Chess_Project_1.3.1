package com.kyubin.chess.frames;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GraphFrame extends JPanel {
    private List<Long> values;

    public GraphFrame(List<Long> values) {
        this.values = values;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.size() < 2) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int padding = 25;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;

        long maxVal = Long.MIN_VALUE;
        long minVal = Long.MAX_VALUE;
        for (long value : values) {
            if (value > maxVal) {
                maxVal = value;
            }
            if (value < minVal) {
                minVal = value;
            }
        }

        int previousX = padding;
        int previousY = height - padding - (int) ((double) (values.get(0) - minVal) / (maxVal - minVal) * graphHeight);
        for (int i = 1; i < values.size(); i++) {
            int x = padding + i * graphWidth / (values.size() - 1);
            int y = height - padding - (int) ((double) (values.get(i) - minVal) / (maxVal - minVal) * graphHeight);

            g.drawLine(previousX, previousY, x, y); // 선 그리기

            // 수직선 그리기
            g.setColor(Color.RED); // 수직선 색상 설정
            g.drawLine(x, y, x, height - padding); // 데이터 포인트에서 아래쪽 테두리까지 선 그리기
            g.setColor(Color.BLACK); // 다시 기본 색상으로 변경

            previousX = x;
            previousY = y;
        }

        // 마지막 포인트에 대한 수직선 그리기
        g.setColor(Color.RED);
        g.drawLine(previousX, previousY, previousX, height - padding);
        g.setColor(Color.BLACK);
    }

    public static void showGraph(List<Long> values) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("List Line Graph Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            GraphFrame panel = new GraphFrame(values);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}