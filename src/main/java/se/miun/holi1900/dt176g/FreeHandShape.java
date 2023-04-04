package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;

import java.awt.*;
import java.util.ArrayList;

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
    public void addListOfPoints(ArrayList<Point> list){
        this.points.addAll(list);
    }

    @Override
    public void draw(Graphics g) {
        /*Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(Color.decode(this.getColor()));
        g2.setStroke(new BasicStroke(thickness));
        System.out.println("points Size " + points.size());
        if(points.size()>1){
            for(int i=0; i<points.size()-1; i++){
                g2.drawLine(points.get(i).getX(),points.get(i).getY(), points.get(i+1).getX(), points.get(i+1).getY());
            }
        }*/

        ArrayList<Line> lines = new ArrayList<>();
        if(points.size()>1){
            System.out.println("points Size " + points.size());
            for(int i=0; i<points.size()-1; i++){
                Line line = new Line(points.get(i), color);
                line.addPoint(points.get(i+1));
                line.thickness = thickness;
                lines.add(line);
            }

            Observable<Shape> shapesObservable = Observable.fromIterable(lines);
            shapesObservable.subscribe(shape -> shape.draw(g));
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
