package se.miun.holi1900.dt176g;

import java.awt.*;

public class Line extends Shape{
    public Line(int x, int y, String color) {
        this(new Point(x, y), color);
    }

    public Line(Point point, String color) {
        super(point, color);
        this.maxPointsCount = 2;
    }

    private double getLength(){
        if(hasEndPoint())
        {

            double sumOfSquares = Math.pow(points.get(1).getX() - points.get(0).getX(), 2)
                    + Math.pow(points.get(1).getY() - points.get(0).getY(), 2);
            return Math.pow(sumOfSquares, 0.5);
        }else{
            return 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(Color.decode(this.getColor()));
        if(hasEndPoint()){
            g2.drawLine(points.get(0).getX(),points.get(0).getY(), points.get(1).getX(), points.get(1).getY());
        }
    }

    @Override
    public String toString() {
        String startPoint = points.get(0).toString();
        String color =  getColor();
        String endPoint;

        if(hasEndPoint())
        {
            endPoint = points.get(1).toString();
        }
        else{
            endPoint = "N/A";
        }
        return "Line [start=" + startPoint + "; end=" + endPoint + "; color=" + color + "]";
    }

    @Override
    public String getInfoToBeSaved() {
        return null;
    }
}
