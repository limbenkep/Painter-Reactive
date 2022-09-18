package se.miun.holi1900.dt176g;

import javax.swing.*;


/**
 * <h1>Menu</h1>
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */
public class Menu extends JMenuBar {
    private static final long serialVersionUID = 1L;


    public Menu(MainFrame frame) {
        init(frame);
    }

    private void init(MainFrame frame) {

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("Some Menu category");
        this.add(menu);


        menuItem = new JMenuItem("Some menu item 1");
        menuItem.addActionListener(e -> anEvent(frame));
        menu.add(menuItem);

        menuItem = new JMenuItem("Some menu item 2");
        menuItem.addActionListener(e ->  anotherEvent(frame));
        menu.add(menuItem);
    }

    private void anEvent(MainFrame frame) {

        String message = (String) JOptionPane.showInputDialog(frame,
                "Send message to everyone:");

        if(message != null && !message.isEmpty()) {
            JOptionPane.showMessageDialog(frame, message);
        }
    }

    private void anotherEvent(MainFrame frame) {

    }

}
