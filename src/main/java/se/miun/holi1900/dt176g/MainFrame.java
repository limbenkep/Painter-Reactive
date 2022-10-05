package se.miun.holi1900.dt176g;
import io.reactivex.rxjava3.core.Observable;

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
public class MainFrame extends JFrame implements OnToolBarColorChanged{
    private ToolBar toolBar;
    private JPanel selectedColorPanelContainer;


    private String header;
    private DrawingPanel drawingPanel;
    private JPanel endPageBar;
    private JPanel selectedColorPanel;
    private Color selectedColor;

    private JLabel cordinateLabel;
    private Point cordinates;


    private Drawing drawing; // a drawing to be displayed in the drawingPanel
    Point startPoint;
    Point endPoint;
    Rectangle rectangle;
    Circle circle;
    Line line;

    Shape shape;

    JScrollPane scrollPane;

    public MainFrame() {


        // default window-size.
        this.setSize(1200, 900);
        // application closes when the "x" in the upper-right corner is clicked.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.header = "Reactive Paint";
        this.setTitle(header);

        // Changes layout from default to BorderLayout
        this.setLayout(new BorderLayout());

        initComponents();

        drawingPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                cordinates.setX(e.getX());
                cordinates.setY(e.getY());
                loadCoordinateInfo();
            }
        });

        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("In mousePressed");
                Point p1 = new Point(e.getX(), e.getY());
                if (Objects.equals(toolBar.getSelectedShapeOption(), "Rectangle")) {
                    shape = new Rectangle(p1,
                            StringColor.getHexColorString(selectedColor));
                    System.out.println("First point of rectangle[" + p1 +
                    "]");
                } else if (Objects.equals(toolBar.getSelectedShapeOption(), "Oval")) {
                    System.out.println(" selected color; " + StringColor.getHexColorString(selectedColor));
                    shape = new Circle(p1, StringColor.getHexColorString(selectedColor));
                           // StringColor.getHexColorString(selectedColor));
                    // System.out.println("First point of circle[" + p1 + "]");
                }else if(Objects.equals(toolBar.getSelectedShapeOption(), "Line")){
                    shape = new Line(p1, StringColor.getHexColorString(selectedColor));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("In mouseReleased");
                Point p2 = new Point(e.getX(), e.getY());
                shape.addPoint(p2);
                drawingPanel.addShapeToDrawing(shape);
                /*if (Objects.equals(toolBar.getSelectedShapeOption(), "Rectangle")) {
                    rectangle.addPoint(p2);
                    drawingPanel.addShapeToDrawing(rectangle);
                    // rectangle.draw(graphics);
                    System.out.println("Second point of rectangle[" + p2 + "]");
                    // System.out.println(rectangle);


                } else if (Objects.equals(toolBar.getSelectedShapeOption(), "Oval")) {
                    circle.addPoint(p2);
                    // System.out.println("Second point of circle[" + p2 + "]");
                    // System.out.println(circle);
                    drawingPanel.addShapeToDrawing(circle);
                }else if(Objects.equals(toolBar.getSelectedShapeOption(), "Line")){
                    line.addPoint(p2);
                    drawingPanel.addShapeToDrawing(line);
                }*/
            }
        });

    //});
    }

    private void initComponents(){

        //scrollPane = makeDisplayArea();
        //Creates menu bar and adds it to the MainFrame
        this.setJMenuBar(new Menu(this));

        //set default color to black
        selectedColor = Color.black;
        // Creates toolbar containing color and shape tools and adds it to the MainFrame
        toolBar = new ToolBar(this);
        toolBar.setColorChangeListener(this);
        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);

        // Creates drawing area and adds it to the MainFrame
        drawingPanel = new DrawingPanel();
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        //this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        //Creates endPageBar and adds it to the MainFrame.
        //This displays the selected color and current coordinates
        this.setJMenuBar(new Menu(this));
        endPageBar = new JPanel(new BorderLayout());
        selectedColorPanelContainer = new JPanel();
        selectedColorPanelContainer.setLayout(new BoxLayout(selectedColorPanelContainer ,BoxLayout.X_AXIS));
        endPageBar = new JPanel();
        endPageBar.setLayout(new BoxLayout(endPageBar, BoxLayout.X_AXIS));

        cordinates = new Point(0,0); //default coordinates is 0,0
        //Add cordinateLabel and selectedColorPanel to container enPageBar JPanel
        cordinateLabel = new JLabel();
        loadCoordinateInfo();
        //Create the panel displaying selected color information
        createSelectedColorPanel();
        endPageBar.add(cordinateLabel);
        cordinateLabel.setAlignmentX(0);
        endPageBar.add(Box.createHorizontalGlue());
        endPageBar.add(selectedColorPanelContainer);
        selectedColorPanelContainer.setAlignmentX(1);

        add(endPageBar, BorderLayout.PAGE_END);

    };

    private Observable<List<MouseEvent>> mousePressedAndReleaseObservable(Observable<MouseEvent> press, Observable<MouseEvent> release){
        return Observable.zip(press, release, (p, r) ->{
            List<MouseEvent> events = new ArrayList<>();
            events.add(p);
            events.add(r);
            return events;
        });
    }
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

    private Observable<MouseEvent> mouseReleasedObservable(DrawingPanel panel){
        return Observable.create(emitter -> {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    emitter.onNext(e);
                }
            });
        });
    }

    private Observable<MouseEvent> mouseDraggedObservable(DrawingPanel panel){
        return Observable.create(emitter -> {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    emitter.onNext(e);
                }
            });
        });
    }



    private JScrollPane makeDisplayArea() {
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(600, 600));
        drawingPanel.setToolTipText("Images will be displayed here");
        drawingPanel.setBackground(Color.white);
        drawingPanel.setOpaque(true);

        drawingPanel.addMouseMotionListener(new MouseAdapter() {
            /*@Override
            public void mouseDragged(MouseEvent e) {
            }*/
            @Override
            public void mouseMoved(MouseEvent e) {
                cordinates.setX(e.getX());
                cordinates.setY(e.getY());
                loadCoordinateInfo();
            }
        });
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent press) {
                // System.err.println("mouse pressed at " + press.getPoint());
                Point p1 = new Point(press.getX(), press.getY());
                if (toolBar.getSelectedShapeOption() == "Rectangle") {
                    rectangle = new Rectangle(p1,
                            StringColor.getHexColorString(selectedColor));
                    // System.out.println("First point of rectangle[" + p1 +
                    // "]");
                } else if (toolBar.getSelectedShapeOption() == "Oval") {
                    circle = new Circle(p1,
                            StringColor.getHexColorString(selectedColor));
                    // System.out.println("First point of circle[" + p1 + "]");
                }
            }

            @Override
            public void mouseReleased(MouseEvent release) {
                Point p2 = new Point(release.getX(), release.getY());
                if (toolBar.getSelectedShapeOption() == "Rectangle") {
                    rectangle.addPoint(p2);
                    // rectangle.draw(graphics);
                    // System.out.println("Second point of rectangle[" + p2 +
                    // "]");
                    // System.out.println(rectangle);

                    if (drawing == null) {
                        drawing = new Drawing();
                        drawing.addShape(rectangle);
                        drawingPanel.setDrawing(drawing);
                    } else {
                        try {
                            Drawing d = new Drawing();
                            d.addShape(rectangle);
                            //drawingPanel.addDrawing(d);
                            drawingPanel.setDrawing(drawing);
                        } catch (NullPointerException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                } else if (toolBar.getSelectedShapeOption() == "Oval") {
                    circle.addPoint(p2);
                    // System.out.println("Second point of circle[" + p2 + "]");
                    // System.out.println(circle);
                    if (drawing == null) {
                        drawing = new Drawing();
                        drawing.addShape(circle);
                        drawingPanel.setDrawing(drawing);
                    } else {
                        try {
                            Drawing d = new Drawing();
                            d.addShape(circle);
                            //drawingPanel.addDrawing(d);
                            drawingPanel.setDrawing(drawing);
                        } catch (NullPointerException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }

        });
        return new JScrollPane(drawingPanel);


    }


    /**
     * updates coordinates information with current cordinates
     */
    private void loadCoordinateInfo(){
        cordinateLabel.setText("Coordinates: " + cordinates.toString());
    }

    /**
     * sets properties of JPanel dispaying selected colors
     */
    private void createSelectedColorPanel(){
        selectedColorPanel = new JPanel();
        selectedColorPanel.setMaximumSize(selectedColorPanel.getPreferredSize());
        selectedColorPanel.setBackground(Color.GREEN);
        selectedColorPanel.setPreferredSize(new DimensionUIResource(20, 20));
        JLabel selectedColorLabel = new JLabel("Selected color: ");
        selectedColorLabel.setLabelFor(selectedColorPanel);
        selectedColorPanelContainer.add(selectedColorLabel);
        selectedColorPanelContainer.add(selectedColorPanel);
    }
    public void setSelectedColorPanel(Color color){

    }

    @Override
    public void currentColor(Color color) {
        selectedColor = color;
        selectedColorPanel.setBackground(color);
        System.out.println("MainFrame.selectedColor new Color " + color);
    }

    /**
     * resets drawing to a empty drawing and clear drawing area
     */
    private void startNewDrawing(){
        drawingPanel.setDrawing(new Drawing());
    }


    MouseListener l = new MouseAdapter(){

    };

    private Observable<MouseEvent> getMouseDragged(DrawingPanel panel){
        return Observable.create(emitter -> {


            panel.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    emitter.onNext(e);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("In mousePressed");
                    Point p1 = new Point(e.getX(), e.getY());
                    if (Objects.equals(toolBar.getSelectedShapeOption(), "rectangle")) {
                        rectangle = new Rectangle(p1,
                                StringColor.getHexColorString(selectedColor));
                        // System.out.println("First point of rectangle[" + p1 +
                        // "]");
                    } else if (Objects.equals(toolBar.getSelectedShapeOption(), "circle")) {
                        circle = new Circle(p1,
                                StringColor.getHexColorString(selectedColor));
                        // System.out.println("First point of circle[" + p1 + "]");
                    }else if(Objects.equals(toolBar.getSelectedShapeOption(), "line")){
                        line = new Line(p1, StringColor.getHexColorString(selectedColor));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("In mouseReleased");
                    Point p2 = new Point(e.getX(), e.getY());
                    if (Objects.equals(toolBar.getSelectedShapeOption(), "rectangle")) {
                        rectangle.addPoint(p2);
                        panel.addShapeToDrawing(rectangle);
                        // rectangle.draw(graphics);
                        // System.out.println("Second point of rectangle[" + p2 +
                        // "]");
                        // System.out.println(rectangle);


                    } else if (Objects.equals(toolBar.getSelectedShapeOption(), "circle")) {
                        circle.addPoint(p2);
                        // System.out.println("Second point of circle[" + p2 + "]");
                        // System.out.println(circle);
                        panel.addShapeToDrawing(circle);
                    }
                }


                @Override
                public void mouseMoved(MouseEvent e) {
                    cordinates.setX(e.getX());
                    cordinates.setY(e.getY());
                    loadCoordinateInfo();
                }
            });

        });
    }



}
