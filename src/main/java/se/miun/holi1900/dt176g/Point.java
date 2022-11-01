package se.miun.holi1900.dt176g;

import java.io.Serializable;

/**
 * <h1>Point</h1>
 * This Class is defines the state and behaviours of a point.
 * has two instant variables x and y of data type double
 * Defines two constructors, one tha takes no parameter and one that takes the x and y values as parameters.
 * Methods getter and setters for x and y, toString which returns x and y values in a String
 *
 * @author Lima Honorine
 * @version 1.0
 * @since 	2022-09-18
 */
public class Point implements Serializable {
    private int x,y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        Point p = (Point) obj;
        return (obj.getClass()== this.getClass() && x == p.getX() && y == p.getY());
    }

    public String toString(){
        return "[" + x + ", " + y + "]";
    }

}
