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

/**
 * Utility class to convert Color object to html color string (#xxxxxxx)
 * 
 */
public class StringColor {
	/**
	 * @param color
	 *            color to convert to hex string
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
}
