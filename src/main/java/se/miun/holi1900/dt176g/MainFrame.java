package se.miun.holi1900.dt176g;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;


/**
 * <h1>MainFrame</h1>
 * JFrame to contain the rest
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */
public class MainFrame extends JFrame {
    private ToolBar toolBar;
    private JPanel selectedColorPanelContainer;


    private String header;
    private DrawingPanel drawingPanel;
    private JPanel endPageBar;
    private JPanel selectedColorPanel;
    private Color selectedColor;

    private JLabel currentCoordinateLabel; // Label for the panel displaying current coordinates
    private Point currentCoordinates; //current coordinates of the cursor which updates as mouse moves
    private Point lastPoint; //Last point from the from freehand drawing

    Shape currentShape; // current shape being drawn
    List<Disposable> disposables = new ArrayList<>(); //store all disposables created to be disposed when exiting program


    public MainFrame() {


        // default window-size.
        this.setSize(1200, 800);
        // application closes when the "x" in the upper-right corner is clicked.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.header = "Reactive Paint";
        this.setTitle(header);

        // Changes layout from default to BorderLayout
        this.setLayout(new BorderLayout());

        initComponents();

        Observable<MouseEvent> mousePressed = mousePressedObservable(drawingPanel);
        Observable<MouseEvent> mouseReleased = mouseReleasedObservable(drawingPanel);
        Observable<List<MouseEvent>> mouseDraw = mousePressedAndReleaseObservable(mousePressed, mouseReleased);
        Disposable disposable1 = mouseDraw.subscribe(s->drawShape(s.get(0), s.get(1)));
        disposables.add(disposable1);

        Observable<MouseEvent> mouseMoved = mouseMovedObservable(drawingPanel);
        Disposable disposable2 = mouseMoved.subscribe(this::updateCoordinatesOnMouseMoved);
        disposables.add(disposable2);

        Observable<MouseEvent> mouseDragged = mouseDraggedObservable(drawingPanel);
        Disposable disposable3 = mouseDragged.subscribe(this::drawFreeHandShape);
        disposables.add(disposable3);
    }

    private void initComponents(){

        //Creates menu bar and adds it to the MainFrame
        this.setJMenuBar(new Menu(this));

        //set default selected color to black
        selectedColor = Color.black;

        // Creates toolbar containing color and shape tools and adds it to the MainFrame
        toolBar = new ToolBar(this);
        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);

        // Creates drawing area and adds it to the MainFrame
        drawingPanel = new DrawingPanel();
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

        //Creates endPageBar and adds it to the MainFrame.
        //This displays the selected color and current coordinates of the cursor
        this.setJMenuBar(new Menu(this));
        endPageBar = new JPanel(new BorderLayout());
        selectedColorPanelContainer = new JPanel();
        selectedColorPanelContainer.setLayout(new BoxLayout(selectedColorPanelContainer ,BoxLayout.X_AXIS));
        endPageBar = new JPanel();
        endPageBar.setLayout(new BoxLayout(endPageBar, BoxLayout.X_AXIS));
        currentCoordinates = new Point(0,0); //default coordinates is 0,0
        //Add cordinateLabel and selectedColorPanel to container enPageBar JPanel
        currentCoordinateLabel = new JLabel();
        loadCoordinateInfo();
        //Create the panel displaying selected color information
        createSelectedColorPanel();
        endPageBar.add(currentCoordinateLabel);
        currentCoordinateLabel.setAlignmentX(0);
        endPageBar.add(Box.createHorizontalGlue());
        endPageBar.add(selectedColorPanelContainer);
        selectedColorPanelContainer.setAlignmentX(1);

        add(endPageBar, BorderLayout.PAGE_END);

    }

    public void setSelectedColor(Color color){
        this.selectedColor = color;
        selectedColorPanel.setBackground(color);
    }

    /**
     * Draws shape from coordinate from mousePressed and mouseRelease events.
     * Gets selected shape and color from toolbar and draw the selected shape
     * using selected color.
     * Gets first coordinate from first passed event and second coordinate
     * from second passed event.
     * @param pressed mousePressed MouseEvent
     * @param released mouseReleased MouseEvent
     */
    private void drawShape(MouseEvent pressed, MouseEvent released){
        Point p1 = new Point(pressed.getX(), pressed.getY());
        if (Objects.equals(toolBar.getSelectedShapeOption(), "Rectangle")) {
            currentShape = new Rectangle(p1, Utils.getHexColorString(selectedColor));
        }else if (Objects.equals(toolBar.getSelectedShapeOption(), "Circle")) {
            currentShape = new Circle(p1, Utils.getHexColorString(selectedColor));
        }else if(Objects.equals(toolBar.getSelectedShapeOption(), "Line")){
            currentShape = new Line(p1, Utils.getHexColorString(selectedColor));
        }

        if(Objects.equals(toolBar.getSelectedShapeOption(), "Freehand")){
            lastPoint = null;
        }else {
            Point p2 = new Point(released.getX(), released.getY());
            currentShape.setThickness(toolBar.getSelectedThickness());
            currentShape.addPoint(p2);
            drawingPanel.addShapeToDrawing(currentShape);
        }

    }

    /**
     * Receives a MouseEvent gets x and y coordinates.
     * If lastPoint is null the coordinates are set to lastPoint.
     * If lastPoint is not null, a line is drawn from between last point and the
     * the coordinates of the event.
     * The MouseEvent's coordinates is then set as to lastPoint
     * @param e MouseEvent holding new coordinates
     */
    private void drawFreeHandShape(MouseEvent e){

        if(Objects.equals(toolBar.getSelectedShapeOption(), "Freehand")){
            if (lastPoint != null) {
                currentShape = new Line(lastPoint, Utils.getHexColorString(selectedColor));
                currentShape.setThickness(toolBar.getSelectedThickness());
                currentShape.addPoint(e.getX(), e.getY());
                drawingPanel.addShapeToDrawing(currentShape);
            }
            lastPoint = new Point(e.getX(), e.getY());
        }
    }

    /**
     * updates the displayed current coordinates with point from passed MouseEvents
     * @param e MouseEvent
     */
    private void updateCoordinatesOnMouseMoved(MouseEvent e){
        currentCoordinates.setX(e.getX());
        currentCoordinates.setY(e.getY());
        loadCoordinateInfo();
    }

    /**
     * Zips emissions from two MouseEvent Observables into a single
     * emission of a list of the two emissions
     * @param press first Observable
     * @param release second Observable
     * @return an Observable of a List of MouseEvents
     */
    private Observable<List<MouseEvent>> mousePressedAndReleaseObservable(
            Observable<MouseEvent> press, Observable<MouseEvent> release){
        return Observable.zip(press, release, (p, r) ->{
            List<MouseEvent> events = new ArrayList<>();
            events.add(p);
            events.add(r);
            return events;
        });
    }

    /**
     * Receives a DrawingPanel and creates an observable that wraps a MouseListener to the DrawingPanel
     * the Observable emits MouseEvent when the MouseListener emits an event on mousePressed
     * @param panel DrawingPanel to be observed
     * @return Observable with emission of type MouseEvent
     */
    private Observable<MouseEvent> mousePressedObservable(DrawingPanel panel){
        return Observable.create(emitter -> {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    emitter.onNext(e);
                }
            });
        });
    }

    /**
     * Receives a DrawingPanel and creates an observable that wraps a MouseListener to the DrawingPanel
     * the Observable emits MouseEvent when the MouseListener emits an event on mouseReleased
     * @param panel DrawingPanel to be observed
     * @return Observable with emission of type MouseEvent
     */
    private Observable<MouseEvent> mouseReleasedObservable(DrawingPanel panel){
        return Observable.create(emitter -> panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                emitter.onNext(e);
            }
        }));
    }

    /**
     * Receives a DrawingPanel and creates an observable that wraps a MouseMotionListener to the DrawingPanel.
     * The Observable continuously emits MouseEvent from the MouseListener as the mouse is dragged
     * accross the DrawingPanel
     * @param panel DrawingPanel to be observed
     * @return Observable with emission of type MouseEvent
     */
    private Observable<MouseEvent> mouseDraggedObservable(DrawingPanel panel){
        return Observable.create(emitter -> panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                emitter.onNext(e);
            }
        }));
    }

    /**
     * Receives a DrawingPanel and creates an observable that wraps a MouseListener to the DrawingPanel
     * the Observable emits MouseEvent when the MouseListener emits an event on mouseMoved
     * @param panel DrawingPanel to be observed
     * @return Observable with emission of type MouseEvent
     */
    private Observable<MouseEvent> mouseMovedObservable(DrawingPanel panel){
        return Observable.create(emitter -> panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                emitter.onNext(e);
            }
        }));
    }

    /**
     * dispose of all disposables when program if exiting
     */
    public void disposeDisposables(){
        for(Disposable d: disposables){
            d.dispose();
        }
        toolBar.disposeDisposables();
    }


    /**
     * updates coordinates information with current cordinates
     */
    private void loadCoordinateInfo(){
        currentCoordinateLabel.setText("Coordinates: " + currentCoordinates.toString());
    }

    /**
     * sets properties of JPanel dispaying selected colors
     */
    private void createSelectedColorPanel(){
        selectedColorPanel = new JPanel();
        selectedColorPanel.setMaximumSize(selectedColorPanel.getPreferredSize());
        selectedColorPanel.setBackground(selectedColor);
        selectedColorPanel.setPreferredSize(new DimensionUIResource(20, 20));
        JLabel selectedColorLabel = new JLabel("Selected color: ");
        selectedColorLabel.setLabelFor(selectedColorPanel);
        selectedColorPanelContainer.add(selectedColorLabel);
        selectedColorPanelContainer.add(selectedColorPanel);
    }



    /**
     * resets drawing to a empty drawing and clear drawing area
     */
    public void startNewDrawing(){
        drawingPanel.setDrawing(new Drawing());
    }

    public void deleteLastShape(){
        Drawing drawing = drawingPanel.getDrawing();
        drawing.removeLastShape();
        drawingPanel.setDrawing(drawing);
    }

}
