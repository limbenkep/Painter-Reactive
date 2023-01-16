package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;

import java.awt.*;

public class FreeHandShape extends Shape{
    public FreeHandShape(int x, int y, String color) {
        this(new Point(x, y), color);
    }

    public FreeHandShape(Point point, String color) {
        super(point, color);
        this.maxPointsCount = Integer.MAX_VALUE;
    }
    private String pointsToString(){
        String shapeString = points.get(0).toString();
        for(int i=1; i<points.size(); i++){
            shapeString += ";";
            shapeString += points.get(i);
        }
        return shapeString;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(Color.decode(this.getColor()));
        g2.setStroke(new BasicStroke(thickness));
        if(points.size()>1){
            for(int i=0; i<points.size()-1; i++){
                g2.drawLine(points.get(i).getX(),points.get(i).getY(), points.get(i+1).getX(), points.get(i+1).getY());
            }
        }
    }

    @Override
    public String toString() {
        return "FreeHand [Points=" + pointsToString() + "; color=" + getColor() + "; thickness=" + thickness + "]";
    }

    @Override
    public String getInfoToBeSaved() {
        return "FreeHand|" + pointsToString() + "|" + getColor() + "|" + thickness;
    }
}
