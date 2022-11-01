package se.miun.holi1900.dt176g;

import java.util.Objects;

public class ShapeConverter {
    public static Shape stringToShape(String shapeString){
        String[] splits =  shapeString.trim().split(",");
        Shape shape = null;
        try {
            int startX = Integer.parseInt(splits[1]);
            int startY = Integer.parseInt(splits[2]);
            int endX = Integer.parseInt(splits[3]);
            int endY = Integer.parseInt(splits[4]);
            float thickness = Float.parseFloat(splits[6]);
            Point startPoint = new Point(startX, startY);
            Point endPoint = new Point(endX, endY);
            if(Objects.equals(splits[0], "Rectangle")){
                shape = new Rectangle(startPoint, splits[5]);
            }
            if(Objects.equals(splits[0], "Circle")){
                shape = new Circle(startPoint, splits[5]);
            }
            if(Objects.equals(splits[0], "Line")){
                shape = new Line(startPoint, splits[5]);
            }
            if(shape!=null){
                shape.addPoint(endPoint);
                shape.setThickness(thickness);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shape;
    }
}
