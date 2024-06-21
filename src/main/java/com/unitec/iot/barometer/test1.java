package com.unitec.iot.barometer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class test1 extends JPanel {

    private List<Point> points = new ArrayList<>();
    private int luxMin = Integer.MAX_VALUE;
    private int luxMax = Integer.MIN_VALUE;
    private long timestamp = 0;

    public void updateJsonData(int lux) {
        points.add(new Point((int) timestamp++, lux));

        luxMin = Math.min(luxMin, lux);
        luxMax = Math.max(luxMax, lux);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xStart = 50;
        int yStart = getHeight() - 50;
        int xEnd = getWidth() - 50;
        int yEnd = 50;

        g2d.setColor(Color.BLACK);

        // Draw x-axis
        g2d.drawLine(xStart, yStart, xEnd, yStart);

        // Draw y-axis
        g2d.drawLine(xStart, yStart, xStart, yEnd);

        // Draw curve line
        if (points.size() >= 2) {
            GeneralPath curve = new GeneralPath();
            Point firstPoint = points.get(0);
            curve.moveTo(mapXValue(firstPoint.x, xStart, xEnd), mapYValue(firstPoint.y, yStart, yEnd));
            for (int i = 1; i < points.size(); i++) {
                Point point = points.get(i);
                curve.lineTo(mapXValue(point.x, xStart, xEnd), mapYValue(point.y, yStart, yEnd));
            }
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(curve);
        }

        // Draw data points
        for (Point point : points) {
            int x = mapXValue(point.x, xStart, xEnd);
            int y = mapYValue(point.y, yStart, yEnd);
            g2d.setColor(Color.RED);
            g2d.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private int mapXValue(int x, int xStart, int xEnd) {
        return xStart + (int) ((x / (double) (timestamp - 1)) * (xEnd - xStart));
    }

    private int mapYValue(int y, int yStart, int yEnd) {
        return yStart - (int) ((y / (double) (luxMax - luxMin)) * (yStart - yEnd));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dynamic Line Graph Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            test1 panel = new test1();

            // Create a timer to update the graph every second
            Timer timer = new Timer(1000, e -> {
                Random random = new Random();
                int lux = random.nextInt(100); // Random lux value between 0 and 99
                panel.updateJsonData(lux);
            });
            timer.start();

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
