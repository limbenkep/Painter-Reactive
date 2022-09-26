package se.miun.holi1900.dt176g;

import java.awt.*;

/**
 * <h1>Circle</h1>
 * This Class manages the properties and behaviours of the Shape type, Circle.
 * Circle class inherits from Shape class
 * This class has two constructors, one that receives color and a point object has parameter and
 * the second that recieves color and x and y values for a point.
 * overwrites inherited methods to get radius, get circumference, get area, draw the
 * Circle to the standard output, draw circle to a GUI, get information about circle as a string.
 * Implements class methods to get radius
 * <p>
 *
 *
 * @author Lima Honorine (holi1900)
 * @version 1.0
 * @since 	2022-09-18
 */

public class Circle extends Shape {

    public static final double PI = 3.14;

    /** Constructors  */
    public Circle(int x, int y, String color) {
        this(new Point(x,y), color);
    }

    public Circle(Point point, String color) {
        super(point, color);
        this.maxPointsCount = 2;
    }

    /**
     * function computes the radius of the circle by computing distance between the two points
     * The square root of sum of the squares of the horizontal and vertical distances.
     * @return radius as a double
     */
    public double getRadius(){
        if(hasEndPoint()){
            double sumOfSquares = Math.pow(points.get(1).getX() - points.get(0).getX(), 2)
                    + Math.pow(points.get(1).getY() - points.get(0).getY(), 2);
            return Math.pow(sumOfSquares, 0.5);
        }
        return-1;
    }
    /**
     * computes area of the circle if the endpoint has been added else returns -1
     * returns area as a double
     */
    @Override
    public double getArea() {

        if(hasEndPoint()){
            return PI*(Math.pow(getRadius(), 2));
        }

        return -1;
    }

    /**
     * computes circumference of the circle if the endpoint has been added else returns -1
     * returns circumference as a double
     */
    @Override
    public double getCircumference() {
        if(hasEndPoint()){
            return 2*PI*getRadius();
        }
        return -1;
    }

    /**
     * returns the points, radius, colors of the cirle in single a String
     */
    @Override
    public String toString(){
        String startPoint = points.get(0).toString();
        String color =  getColor();
        String endPoint, radius;

        if(hasEndPoint())
        {
            endPoint = points.get(1).toString();
            radius = String.valueOf(getRadius());
        }
        else{
            endPoint = "N/A";
            radius = "N/A";
        }

        return "Circle [start=" + startPoint + "; end=" + endPoint + "; radius=" + radius + "; color=" + color + "]";
    }


    @Override
    public String getInfoToBeSaved() {

        return "circle," + points.get(0).getX() + "," + points.get(0).getY() + ","
                + points.get(1).getX() + "," + points.get(1).getY() + "," + this.getColor() + "\n";
    }

    @Override
    public void draw(Graphics g) {
        if(hasEndPoint()){
            Graphics2D g2 = (Graphics2D)g;
            Point startPoint = points.get(0);
            int diameter = (int) getRadius()*2;
            int x = startPoint.getX();
            int y = startPoint.getY();

            g2.setPaint(Color.decode(this.getColor()));
            g2.fillOval(x, y, diameter, diameter);
        }
        else{
            System.out.println("Shape cannot be draw because endpoint is missing.");
        }

    }



}

