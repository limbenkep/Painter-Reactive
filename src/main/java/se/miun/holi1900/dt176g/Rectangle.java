package se.miun.holi1900.dt176g;

import java.awt.*;

/**
 * <h1>Rectangle</h1>
 * This Class manages the properties and behaviours of the shape type, rectangle.
 * Rectangle class inherits from Shape class
 * inherits two properties, color, and an Array of Point objects
 * This class has two constructors, one that receives color and a point object has parameter and
 * the second that receives color and x and y values for a point.
 * overwrites inherited methods to get circumference, get area, get information about rectangle as a String,
 * to draw the shape to the standard output, draw Shape to a GUI implemented from Drawable
 * Implements class methods to get height and get width
 * <p>
 *
 *
 * @author Lima Honorine (holi1900)
 * @version 1.0
 */

public class Rectangle extends Shape {

    /**       Constructors   */
    public Rectangle(int x, int y, String color) {
        this(new Point(x, y), color);
    }

    public Rectangle(Point point, String color) {
        super(point, color);
        this.maxPointsCount = 2;
    }

    /**
     * computes height of a rectangle if the endpoint of the rectangle has been added else returns -1.
     * height is absolut value of the difference between the y values of the two points
     * @return height in int
     */
    public int getHeight(){
        if(hasEndPoint())
        {

            return Math.abs(points.get(1).getY() - points.get(0).getY());
        }
        return -1;
    }

    /**
     * computes width of a rectangle if the endpoint of the rectangle has been added else returns -1.
     * width is absolut value of the difference between the x values of the two points
     * @return width
     */
    public int getWidth(){
        if(hasEndPoint())
        {
            return Math.abs(points.get(1).getX() - points.get(0).getX());
        }
        return -1;
    }

    /**
     * returns the points, width, height, colors of the rectangle as a single String
     */
    @Override
    public String toString(){
        String startPoint = points.get(0).toString();
        String color =  getColor();
        String endPoint, height, width;

        if(hasEndPoint())
        {
            endPoint = points.get(1).toString();
            width = String.valueOf(getWidth());
            height = String.valueOf(getHeight());
        }
        else{
            endPoint = "N/A";
            height = "N/A";
            width = "N/A";
        }

        return "Rectangle [start=" + startPoint + "; end=" + endPoint + "; width=" + width + "; height="
                + height + "; color=" + color + "; thickness=" + thickness + "]";
    }

    /**
     * returns a String data to be written to file when shape is to be saved to file
     */
    @Override
    public String getInfoToBeSaved() {
        return "Rectangle," + points.get(0).getX() + "," + points.get(0).getY() + ","
                + points.get(1).getX() + "," + points.get(1).getY() + "," + this.color + "," + this.thickness;
    }

    @Override
    public void draw(Graphics g) {
        if(hasEndPoint()){
            Graphics2D g2 = (Graphics2D)g;
            Point startPoint = points.get(0);
            int x = startPoint.getX();
            int y = startPoint.getY();
            int height = getHeight();
            int width = getWidth();

            g2.setPaint(Color.decode(this.getColor()));
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new java.awt.Rectangle(x, y, width, height));

        }
        else{
            System.out.println("Shape cannot be draw because endpoint is missing.");
        }
    }
}
