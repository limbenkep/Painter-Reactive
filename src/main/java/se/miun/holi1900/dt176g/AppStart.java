package se.miun.holi1900.dt176g;

import javax.swing.*;

/**
 * <h1>AppStart</h1>
 *
 * @author  Honorine Lima
 * @version 1.0
 * @since   2022-09-18
 */

public class AppStart {
    public static void main(String[] args) {

        // Make sure GUI is created on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame("localhost", 11111).setVisible(true);
            }
        });
    }
}
