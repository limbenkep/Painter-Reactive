package se.miun.holi1900.dt176g;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Shape</h1>
 * This Class is an abstract class that  defines the properties and behaviours of the Shape type, Circle.
 * This class implements Interface Drawable
 * Shape has two properties, color, and an Array of Point objects of size 2 where the first position
 * holds the start point of the shape and position 1 holds the end point of the shape.
 * This class has two constructors, one that receives color and a point object has parameter and
 * the second that recieves color and x and y values for a point.
 * Methods to get circumference, get area, and get information about circle as a string are abstract.
 * Methods to draw the shape to the standard output, draw Shape to a GUI implemented from Drawable
 * <p>
 *
 *
 * @author Lima Honorine
 * @version 1.0
 * @since 	2022-09-18
 */
public abstract class Shape implements Drawable, Serializable {
    protected int maxPointsCount; //The number of required to make the shape
    protected List<Point> points = new ArrayList<>(maxPointsCount);
    private String color;

    protected float thickness  = 1.0f;
    /**
     * Constructor takes a point and color string
     * shape requires at least one point to be created
     */

    public Shape(int x, int y, String color) {
        this(new Point(x,y), color);
    }
    public Shape(Point point, String color) {
        this.color = color;
        this.points.add(0, point);
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public void setThickness(float thickness){
        this.thickness = thickness;
    }


    public abstract String toString();
    public abstract String getInfoToBeSaved();

    /**
     * This function receives x and y values used to create a point object assigns the point object to position 2 of the Point array
     * @param x x coordinate
     * @param y coordinates
     */
    public void addPoint(int x, int y){
        this.addPoint(new Point(x, y));
    }
    /**
     * functions receives a point object assigns to position 2 of the Point array
     * @param point
     */
    public void addPoint(Point point){
        if(points.size()< maxPointsCount){
            this.points.add(1, point);
        }
        if(points.size()== maxPointsCount){
            this.points.remove(1);
            this.points.add(1, point);
        }
    }

    public boolean hasEndPoint(){
        return points.size() >= maxPointsCount;
    }


    /*@Override
    public void draw(Graphics g) {
        // TODO Auto-generated method stub

    }*/

}
