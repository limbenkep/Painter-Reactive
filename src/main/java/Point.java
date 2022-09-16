
/**
 * <h1>Point</h1>
 * This Class is defines the state and behaviours of a point.
 * has two instant variables x and y of data type double
 * Defines two constructors, one tha takes no parameter and one that takes the x and y values as parameters.
 * Methods getter and setters for x and y, toString which returns x and y values in a String
 *
 *
 * @author Lima Honorine (holi1900)
 * @version 1.0
 */
public class Point {
    private double x,y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString(){
        return x + ", " + y;
    }

}
