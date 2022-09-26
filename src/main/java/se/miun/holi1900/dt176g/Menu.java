package se.miun.holi1900.dt176g;

import javax.swing.*;
import javax.swing.border.LineBorder;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import java.awt.*;


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

        JMenu file = new JMenu("File");
        this.add(file);
        JMenu edit = new JMenu("Edit");
        this.add(edit);

        //menu option to start a new drawing
        JMenuItem newItem = new JMenuItem("New...");
        newItem.addActionListener(e -> anEvent(frame));
        file.add(newItem);

        //menu option to save drawing
        JMenuItem saveItem = new JMenuItem("Save as...");
        saveItem.addActionListener(e -> anEvent(frame));
        file.add(saveItem);

        //Menu option to load drawing from file
        JMenuItem loadItem = new JMenuItem("Load...");
        loadItem.addActionListener(e -> anEvent(frame));
        file.add(loadItem);

        //menu option to Exit drawing
        JMenuItem exitItem = new JMenuItem("Exit");
        file.add(exitItem);
        exitItem.setBorder(new LineBorder(Color.BLACK));
        Completable exitSelected = getCompletable(exitItem);
        exitSelected.subscribe(()->System.exit(0));

        //menu option to helpful information about the program
        JMenuItem infoItem = new JMenuItem("Info");
        infoItem.addActionListener(e -> anEvent(frame));
        file.add(infoItem);

        //menu option to undo changes to drawing
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> anEvent(frame));
        edit.add(undoItem);

        //menu option to edit the name of a drawing
        JMenuItem nameItem = new JMenuItem("Name...");
        nameItem.addActionListener(e -> anEvent(frame));
        edit.add(nameItem);

        //menu option to edit the author of a drawing
        JMenuItem authorItem = new JMenuItem("Author...");
        authorItem.addActionListener(e -> anEvent(frame));
        edit.add(authorItem);
    }

    private Completable getCompletable(JMenuItem menuItem){
        return Completable.create(emitter ->{
            menuItem.addActionListener(event->emitter.onComplete());
        });
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
