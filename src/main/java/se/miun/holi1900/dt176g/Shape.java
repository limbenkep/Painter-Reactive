package se.miun.holi1900.dt176g;

import java.awt.*;
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
 * @author Lima Honorine (holi1900)
 * @version 1.0
 */
public abstract class Shape implements Drawable{
    protected int maxSize;
    protected List<Point> points = new ArrayList<>(maxSize);
    private String color;
    public Shape(double x, double y, String color) {
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

    /**
     *
     * @return circumference of the shape as a double. To be implemented by each shape type class
     */
    public abstract double getCircumference();

    /**
     *
     * @return area of the shape as a double. To be implemented by each shape type class
     */
    public abstract double getArea();

    public abstract String toString();
    public abstract String getsaveInfo();

    /**
     * This function receives x and y values used to create a point object assigns the point object to position 2 of the Point array
     * @param x x coordinate
     * @param y coordinates
     */
    public void addPoint(double x, double y){
        this.addPoint(new Point(x, y));
    }
    /**
     * functions receives a point object assigns to position 2 of the Point array
     * @param point
     */
    public void addPoint(Point point){
        if(points.size()<maxSize){
            this.points.add(1, point);
        }
        if(points.size()==maxSize){
            this.points.remove(1);
            this.points.add(1, point);
        }
    }

    public boolean hasEndPoint(){
        return points.size() >= maxSize;
    }

    /*public Color stringToColor(){
        Color myColor;
        try {
            Field field = Class.forName("java.awt.Color").getField(color);
            myColor = (Color)field.get(null);
        } catch (Exception e) {
            myColor = null; // Not defined
        }
        return myColor;
    }*/



    /**
     * function computes the difference between the x-cordinates of two points
     * @return difference as a double
     *
    public double getHorizontalDistance(){
    return points[0].getX() - points[1].getX();
    }

    /**
     * function computes the difference between the y-cordinates of two points
     * @return difference as a double
     *
    public double getVerticalDistance(){
    return points[0].getY() - points[1].getY();
    }

    /**
     * function computes the distance between the two points by computing the sum of the squares of
     * the horizontal and vertical distances and then finding the square root.
     * @return difference as a double
     *
    public double getDistanceBetweenPoints(){

    double sumOfSquares = (Math.pow(getHorizontalDistance(), 2) + Math.pow(getVerticalDistance(), 2));
    return Math.pow(sumOfSquares, 0.5);
    }*/

    @Override
    public void draw() {
        System.out.println("A " + color + " " + getClass().getSimpleName() + "has been drawn");
    }
    @Override
    public void draw(Graphics g) {
        // TODO Auto-generated method stub

    }

}
