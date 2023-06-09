package se.miun.holi1900.dt176g;
/**
 * <h1>DrawingPanel</h1> Creates a Canvas-object for displaying all graphics
 * already drawn.
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */

import javax.swing.*;
import java.awt.*;

public class DrawingPanel extends JPanel {
    private Drawing drawing;


    public DrawingPanel() {
        drawing = new Drawing();
    }

    public void redraw() {
        repaint();
    }

    public void setDrawing(Drawing d) {
        drawing = d;
        repaint();
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public void addShapeToDrawing(Shape shape){
        drawing.addShape(shape);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawing.draw(g);
    }
}
