package se.miun.holi1900.dt176g;
import java.awt.*;
import java.awt.event.*;
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
    private JPanel selectedColorPanel;
    private JLabel cordinateLabel;
    private String header;
    private DrawingPanel drawingPanel;
    private JPanel endPageBar;
    private JPanel selectedColor;
    private Point cordinates;

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


    }

    private void initComponents(){
        //Creates menu bar and adds it to the MainFrame
        this.setJMenuBar(new Menu(this));

        // Creates toolbar containing color and shape tools and adds it to the MainFrame
        toolBar = new ToolBar(this);
        toolBar.setColorChangeListener(this);
        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);

        // Creates drawing area and adds it to the MainFrame
        drawingPanel = new DrawingPanel();
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

        //Creates endPageBar and adds it to the MainFrame.
        //This displays the selected color and current coordinates
        this.setJMenuBar(new Menu(this));
        endPageBar = new JPanel(new BorderLayout());
        selectedColorPanel = new JPanel();
        selectedColorPanel.setLayout(new BoxLayout(selectedColorPanel ,BoxLayout.X_AXIS));
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
        endPageBar.add(selectedColorPanel);
        selectedColorPanel.setAlignmentX(1);

        add(endPageBar, BorderLayout.PAGE_END);
    };

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
        selectedColor = new JPanel();
        selectedColor.setMaximumSize(selectedColor.getPreferredSize());
        selectedColor.setBackground(Color.GREEN);
        selectedColor.setPreferredSize(new DimensionUIResource(20, 20));
        JLabel selectedColorLabel = new JLabel("Selected color: ");
        selectedColorLabel.setLabelFor(selectedColor);
        selectedColorPanel.add(selectedColorLabel);
        selectedColorPanel.add(selectedColor);
    }
    public void setSelectedColor(Color color){

    }

    @Override
    public void currentColor(Color color) {
        selectedColor.setBackground(color);
        System.out.println("MainFrame.selectedColor new Color " + color);
    }

    /**
     * resets drawing to a empty drawing and clear drawing area
     */
    private void startNewDrawing(){
        drawingPanel.setDrawing(new Drawing());
    }
}
