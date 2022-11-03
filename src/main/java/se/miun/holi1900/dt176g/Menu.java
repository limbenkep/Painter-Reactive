package se.miun.holi1900.dt176g;

import javax.swing.*;
import javax.swing.border.LineBorder;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

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

        //menu option to start a new drawing
        JMenuItem newItem = new JMenuItem("New...");
        //newItem.addActionListener(e -> anEvent(frame));
        Observable<Boolean> newItemObservable = getSelectMenuItemObservable(newItem);
        Disposable newItemDisposable = newItemObservable.subscribe(e->{
            frame.startNewDrawing();
        });
        file.add(newItem);


        //menu option to Exit drawing
        JMenuItem exitItem = new JMenuItem("Exit");
        file.add(exitItem);
        exitItem.setBorder(new LineBorder(Color.BLACK));
        Completable exitSelected = getCompletable(exitItem);
        exitSelected.subscribe(()->{
            System.out.println("Shutting of program...");
            frame.disposeDisposables();
            newItemDisposable.dispose();
            System.exit(0);
            System.out.println("End of program");

        });
    }

    private Completable getCompletable(JMenuItem menuItem){
        return Completable.create(emitter ->{
            menuItem.addActionListener(event->emitter.onComplete());
        });
    }

    private Observable<Boolean> getSelectMenuItemObservable(JMenuItem menuItem){
        return Observable.create(emitter -> {
            menuItem.addActionListener(event-> emitter.onNext(true));
        });
    }


}
