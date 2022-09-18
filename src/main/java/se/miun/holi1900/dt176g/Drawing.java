package se.miun.holi1900.dt176g;

import java.awt.*;

/**
 * <h1>Drawing</h1>
 * Let this class store an arbitrary number of AbstractShape-objects in
 * some kind of container.
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */
public class Drawing implements Drawable{
    // private SomeContainer shapes;


    /**
     * <h1>addShape</h1> add a shape to the "SomeContainer shapes"
     *
     * @param s a {@link Shape} object.
     */
    public void addShape(Shape s) {

    }


    @Override
    public void draw(Graphics g) {

        // iterate over all shapes and draw them using the draw-method found in
        // each concrete subclass.
    }
}
