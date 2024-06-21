package com.unitec.iot.barometer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

class DynamicGraphicsJsonExample extends JPanel {
    private List<Point> points = new ArrayList<>();
    private int luxMin = Integer.MAX_VALUE;
    private int luxMax = Integer.MIN_VALUE;
    private long timestamp = 0;
    private boolean centered = false;

    public void updateJsonData(int lux) {
        points.add(new Point((int) timestamp++, lux));

        luxMin = Math.min(luxMin, lux);
        luxMax = Math.max(luxMax, lux);

        if (points.size() > 10) {
            points.remove(0); // Remove the oldest point

            // Adjust the x-coordinate values of the remaining points
            for (int i = 0; i < points.size(); ++i) {
                Point point = points.get(i);
                point.x = i + 1; // Update the x-coordinate value to move points to previous positions
            }
            // Update the timestamp to match the new x-coordinate values
            timestamp = points.size();
        }

        if (!centered) {
            centerGraph();
            centered = true;
        }
    }

    private void centerGraph() {
        int dataRange = luxMax - luxMin;
        int graphRange = getHeight() - 100; // Adjust as needed
        int offset = (graphRange - dataRange) / 2;

        for (Point point : points) {
            point.y += offset;
        }

        luxMin += offset;
        luxMax += offset;
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

        // Draw x-axis values
     // Draw x-axis values
        int xRange = Math.min(points.size(), 10); // Limit to 10 values or the total number of points, whichever is smaller
       
        int xStep = 1; // Default xStep value
        if (xRange > 1) {
            xStep = (points.size() - 1) / (xRange - 1); // Divide the x-axis into xRange - 1 equal steps
        }
        
        for (int i = 0; i < xRange; i++) {
            int index = i * xStep;
            Point point = points.get(index);
            int xValue = point.x;
            int xPosition = mapXValue(xValue, xStart, xEnd);
            String xAxisValue = Integer.toString(xValue);
            g2d.setColor(Color.BLACK);
            g2d.drawString(xAxisValue, xPosition, yStart + 20);
        }


        // Draw y-axis values
        int yRange = luxMax - luxMin;
        int yStep = yRange / 10; // Divide the y-axis into 10 equal steps
        for (int i = 0; i <= 10; i++) {
            int yValue = luxMin + (yStep * i);
            int yPosition = mapYValue(yValue, yStart, yEnd);
            String yAxisValue = Integer.toString(yValue);
            g2d.setColor(Color.BLACK);
            g2d.drawString(yAxisValue, xStart - 40, yPosition);
        }

     // Draw curve line
        if (points.size() >= 2) {
            GeneralPath curve = new GeneralPath();
            Point firstPoint = points.get(0);
            curve.moveTo(mapXValue(firstPoint.x, xStart, xEnd), mapYValue(firstPoint.y, yStart, yEnd));
            for (int i = 1; i < points.size(); i++) {
                Point point1 = points.get(i - 1);
                Point point2 = points.get(i);
                int x1 = mapXValue(point1.x, xStart, xEnd);
                int y1 = mapYValue(point1.y, yStart, yEnd);
                int x2 = mapXValue(point2.x, xStart, xEnd);
                int y2 = mapYValue(point2.y, yStart, yEnd);
                int ctrlX = (x1 + x2) / 2;
                int ctrlY = (y1 + y2) / 2;
                curve.quadTo(x1, y1, ctrlX, ctrlY);
            }
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.draw(curve);
        }


        // Draw data points
        for (Point point : points) {
            int x = mapXValue(point.x, xStart, xEnd);
            int y = mapYValue(point.y, yStart, yEnd);
            g2d.setColor(Color.RED);
            g2d.fillOval(x - 4, y - 4, 8, 8);
        }
        
        // Draw "LUX Meter" in the top center
        paintTitle(g2d);

    }

	private void paintTitle(Graphics2D g2d) {
		String title = "LUX METER";
        Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        g2d.setFont(titleFont);

        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = 20;
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(title, titleX, titleY+20);
	}

    private int mapXValue(int x, int xStart, int xEnd) {
        return xStart + (int) ((x / (double) (timestamp - 1)) * (xEnd - xStart));
    }

    private int mapYValue(int y, int yStart, int yEnd) {
        return yStart - (int) ((y / (double) (luxMax - luxMin)) * (yStart - yEnd));
    }
}
