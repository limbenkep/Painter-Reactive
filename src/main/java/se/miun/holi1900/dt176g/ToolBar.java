package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolBar extends JToolBar {
    private JPanel colorPanelSContainer; //contains all the different color panels
    List<JPanel> colorPanels = new ArrayList<>(); //list of all color panels
    private JComboBox<String> shapeOptions; // displays the different shape options
    private JComboBox<Float> thicknessOptions; // displays the different thickness options
    private final List<Disposable> disposables = new ArrayList<>();// stores all disposables created 
    public ToolBar(MainFrame frame) {
        this.init(frame);
    }

    private void init(MainFrame frame){
        colorPanelSContainer = new JPanel(new GridLayout(1,6));
        shapeOptions = new JComboBox<>();
        thicknessOptions = new JComboBox<>();
        setColorPanels(frame);
        loadShapeOptions();
        loadThicknessOption();

        this.add(colorPanelSContainer);
        this.add(shapeOptions);
        this.add(thicknessOptions);
    }

    /**
     *
     * @return selected shape from the shapeOptions JComboBox
     */
    public String getSelectedShapeOption(){
        return shapeOptions.getSelectedItem().toString();
    }

    /**
     *
     * @return selected line thickness from the thicknessOptions JComboBox
     */
    public float getSelectedThickness(){
        return (float) thicknessOptions.getSelectedItem();
    }

    /**
     * create JPanel for each color object in colors List and store them in the colorPanels List
     * Attach an observable to each color panel that emits a emission when the panel is clicked
     */
    private void setColorPanels(MainFrame frame){
        List<Color> colors = new ArrayList<>();
        colors.add(Color.WHITE);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);

        for (Color color : colors) {
            JPanel panel = new JPanel();
            panel.setBackground(color);
            Observable<MouseEvent> clicked = getMouseClickedObservable(panel);
            Disposable d = clicked.subscribe(e->{
                this.selectColor(e, frame);
            });
            disposables.add(d);
            colorPanels.add(panel);
            colorPanelSContainer.add(panel);
        }

    }

    /**
     * creates JCombobox containing different shape options user can create
     */
    private void loadShapeOptions(){
        shapeOptions.addItem("Rectangle");
        shapeOptions.addItem("Circle");
        shapeOptions.addItem("Line");
        shapeOptions.addItem("Freehand");
        shapeOptions.setMaximumSize(shapeOptions.getPreferredSize());
        shapeOptions.setMinimumSize(shapeOptions.getPreferredSize());
    }

    /**
     * Creates the thickness JComboBox add items.
     */
    private void loadThicknessOption(){
        float value = 0.0f;
        for(int i=0; i<10; i++){
            value+=1.0f;
            thicknessOptions.addItem(value);
        }
        thicknessOptions.setMaximumSize(thicknessOptions.getPreferredSize());
        thicknessOptions.setMinimumSize(thicknessOptions.getPreferredSize());
    }

    /**
     * receives a MouseEvent and mainFrame, get the color of the panel that
     * emit the event and update the selected color in MainFrame.
     * @param e MouseEvent emitted by color panel MouseListener
     * @param frame MainFrame
     */
    private void selectColor(MouseEvent e,MainFrame frame){
        for (JPanel colorPanel : colorPanels) {
            if (e.getComponent().equals(colorPanel)) {
                Color selectedColor = colorPanel.getBackground();
                frame.setSelectedColor(selectedColor);
            }
        }
    }


    /**
     * Creates an Observable that wraps a MouseListener listening to the passed
     * panel and emits MouseEvents when the panel is clicked.
     * @param panel JPanel to be listened for  clicks
     * @return Observable that emit MouseEvents when t
     */
    private Observable<MouseEvent> getMouseClickedObservable(JPanel panel){
        return Observable.create(emitter -> {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    emitter.onNext(e);
                }
            });
        });
    }

    /**
     * dispose of all Disposables created in this class
     * method called when program is about to exit
     */
    public void disposeDisposables(){
        for(Disposable d: disposables){
            d.dispose();
        }
    }

}
