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
import java.util.ArrayList;
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
		String[] splits =  shapeString.trim().split("\\|");
		String[] pointStrings = splits[1].split(";");
		Shape shape = null;
		try {
			ArrayList<Point> points = new ArrayList<>();
			for(String s: pointStrings){
				String[] axis = s.trim().replace("[","").replace("]","").split(",");
				int x = Integer.parseInt(axis[0].trim());
				int y = Integer.parseInt(axis[1].trim());
				points.add(new Point(x,y));
			}
			float thickness = Float.parseFloat(splits[3]);
			if(Objects.equals(splits[0], "Rectangle")){
				shape = new Rectangle(points.get(0), splits[2]);
			}
			if(Objects.equals(splits[0], "Circle")){
				shape = new Circle(points.get(0), splits[2]);
			}
			if(Objects.equals(splits[0], "Line")){
				shape = new Line(points.get(0), splits[2]);
			}
			if(Objects.equals(splits[0], "FreeHand")){
				shape = new FreeHandShape(points.get(0), splits[2]);
			}
			if(shape!=null){
				for(int i=1; i<points.size(); i++){
					shape.addPoint(points.get(i));
				}
				shape.setThickness(thickness);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shape;
	}
}

