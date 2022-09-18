package se.miun.holi1900.dt176g;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class PointTest {
    private final Point point = new Point(0,0);
    @Test
    public void testIsEqual(){
        Point pt = new Point(0,0);
        assertTrue(point.equals(pt));
    }

    @Test
    public void testToString(){
        assertEquals("[0, 0]", point.toString());
    }


}