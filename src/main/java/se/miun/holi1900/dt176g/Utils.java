/**
 * <h1>A class to generate java <code>Color</code> values from their string name
 * 
 * @file : StringColor.java
 * @author : Bernard Che Longho (lobe1602)
 * @since : 2018-01-07
 * @version : 1.0
 */
package se.miun.holi1900.dt176g;

import java.awt.*;
import java.util.Objects;

/**
 * Utility class to convert Color object to html color string (#xxxxxxx)
 * 
 */
public class Utils {
	/**
	 * @param color color to convert to hex string
	 * @return hex string value of a color
	 */
	public static String getHexColorString(Color color) {
		String red = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue = Integer.toHexString(color.getBlue());

		return "#" + (red.length() == 1 ? "0" + red : red)
				+ (green.length() == 1 ? "0" + green : green)
				+ (blue.length() == 1 ? "0" + blue : blue);
	}

	/**
	 * converts string of shape data to Shape object.
	 * @param shapeString String  to be converted to shape
	 * @return Shape
	 */
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
