package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


/**
 * <h1>MainFrame</h1>
 * JFrame to contain the rest
 *
 * @author 	Honorine Lima
 * @version 1.0
 * @since 	2022-09-18
 */
public class MainFrame extends JFrame {
    private Socket socket = null;
    private ToolBar toolBar;
    private JPanel selectedColorPanelContainer;
    private DrawingPanel drawingPanel;
    private JPanel selectedColorPanel;
    private Color selectedColor;

    private JLabel currentCoordinateLabel; // Label for the panel displaying current coordinates
    private Point currentCoordinates; //current coordinates of the cursor which updates as mouse moves
    private Point lastPoint; //Last point from the from freehand drawing

    private Shape currentShape; // current shape being drawn
    private final List<Disposable> disposables = new ArrayList<>(); //store all disposables created to be disposed when exiting program
    private final List<Shape> shapes = new ArrayList<>();

    public MainFrame(String serverAddress, int serverPort) {

        // default window-size.
        this.setSize(1200, 800);
        // application closes when the "x" in the upper-right corner is clicked.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String header = "Reactive Paint";
        this.setTitle(header);

        // Changes layout from default to BorderLayout
        this.setLayout(new BorderLayout());

        initComponents();

        //This updates the coordinates as the cursor is moved over the drawing area.
        Observable<MouseEvent> mouseMoved = mouseMovedObservable(drawingPanel);
        Disposable disposable2 = mouseMoved.subscribe(this::updateCoordinatesOnMouseMoved);
        disposables.add(disposable2);

        Observable<MouseEvent> mousePressed = mousePressedObservable(drawingPanel);
        Observable<MouseEvent> mouseReleased = mouseReleasedObservable(drawingPanel);
        Observable<List<MouseEvent>> mousePressAndRelease = mousePressedAndReleaseObservable(mousePressed, mouseReleased);

        Observable<MouseEvent> mouseDraggedObservable = createMouseDraggedObservable(drawingPanel);

        try{
            socket = new Socket(serverAddress, serverPort);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Disposable inputStreamDisposable = Observable.just(socket)
                    .map(Socket::getInputStream)
                    .map(InputStreamReader::new)
                    .map(BufferedReader::new)
                    .map(BufferedReader::lines)
                    .flatMap(stream -> Observable.fromIterable(stream::iterator).subscribeOn(Schedulers.computation()))
                    .map(s->Utils.stringToShape((String) s))
                    .doOnDispose(()->socket.close())
                    .subscribeOn(Schedulers.io())
                    .subscribe(s-> {
                                drawingPanel.addShapeToDrawing(s);
                                //System.out.println("Broadcast shapes: " + s);
                            },
                            e-> {
                        showNotification("Lost connection t0 server.");
                        System.out.println("Lost connection t0 server.");

                            });
            disposables.add(inputStreamDisposable);

            //This observable emits mouse dragged events and the subscribe observer draw the shape if the chosen
            //shape type is "freehand" and save shapes to shapes container to be sent on mouse released
            Disposable mouseDraggedDisposable = mouseDraggedObservable.subscribeOn(Schedulers.io())
                    .doOnNext(this::updateCoordinatesOnMouseMoved)//Updates displayed coordinates as mouse is dragged
                    .observeOn(Schedulers.io())
                    .subscribe(this::drawFreeHandShape);
            disposables.add(mouseDraggedDisposable);

            //This Observable emits an emission when a mouse is pressed and released.
            //If the selected shape is a Line, Circle, or Rectangle, the subscribed observer
            //draws the shape and store the shape in the shapes container
            //All shapes in shapes container are sent to the server to be broadcast
            Disposable mousePressReleaseDisposable = mousePressAndRelease.observeOn(Schedulers.io()).subscribe(list->{
                drawShape(list.get(0), list.get(1));
                for(Shape shape:shapes){
                    out.write(shape.getInfoToBeSaved());
                    out.newLine();
                }
                out.flush();
                //System.out.println("sent shape" + shapes);
                shapes.clear(); //this prevents sending shapes multiple times. only newly drawn shapes are found here
                lastPoint = null;
            });
            disposables.add(mousePressReleaseDisposable);
        } catch (IOException e) {
            showNotification("Problem connecting to server. Check server connection and restart program.");
            System.out.println("Problem connecting to server.");
            //e.printStackTrace();
        }


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
        JPanel endPageBar = new JPanel(new BorderLayout());
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

    /**
     * sets the passed color to be the selectedColor
     * which will be the shapes color when drawing shape
     * @param color color selected by user
     */
    public void setSelectedColor(Color color){
        this.selectedColor = color;
        selectedColorPanel.setBackground(color);
    }


    /**
     * Draws shape from coordinate from mousePressed and mouseRelease events.
     * Gets selected shape, color and thickness from toolbar and draw the selected shape
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

        if(!Objects.equals(toolBar.getSelectedShapeOption(), "Freehand")){
            Point p2 = new Point(released.getX(), released.getY());
            currentShape.setThickness(toolBar.getSelectedThickness());
            currentShape.addPoint(p2);
            drawingPanel.addShapeToDrawing(currentShape);
            shapes.add(currentShape);
        }
    }

    /**
     * Receives a MouseEvent gets x and y coordinates.
     * If lastPoint is null the coordinates are set to lastPoint.
     * If lastPoint is not null, a line is drawn from between last point and
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
                shapes.add(currentShape);

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
     * across the DrawingPanel
     * @param panel DrawingPanel to be observed
     * @return Observable with emission of type MouseEvent
     */
    private Observable<MouseEvent> createMouseDraggedObservable(DrawingPanel panel){
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
            System.out.println("disposing disposables");
        }
        toolBar.disposeDisposables();
        Schedulers.shutdown();
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

    /**
     * displays error message to user
     * @param message message to be displayed
     */
     private void showNotification(String message){
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
     }

}
