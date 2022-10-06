package se.miun.holi1900.dt176g;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolBar extends JToolBar {
    private JPanel colorPanelSContainer;
    private MouseListener mouseListener;

    private OnToolBarColorChanged colorChangeListener;
    List<JPanel> colorPanels = new ArrayList<>();
    private JComboBox<String> shapeOptions;
    private JComboBox<Float> thicknessOptions;

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
    public String getSelectedShapeOption(){
        String shape = Objects.requireNonNull(shapeOptions.getSelectedItem()).toString();
        System.out.println("selected shape option: " + shape);
        return  shape;
    }

    public float getSelectedThickness(){
        return (float) thicknessOptions.getSelectedItem();
    }

    public void setMouseListener(MouseListener mouseListener) {
        System.out.println("ToolBar.setMouseListener");
        this.mouseListener = mouseListener;
    }

    /**
     * create JPanel for each color object in colors List and store them in the colorPanels List
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
            panel.addMouseListener(l);
            colorPanels.add(panel);
            colorPanelSContainer.add(panel);
        }

    }

    /**
     * creates JCombobox containing different shape options user can create
     */
    private void loadShapeOptions(){
        shapeOptions.addItem("Rectangle");
        shapeOptions.addItem("Oval");
        shapeOptions.addItem("Line");
        shapeOptions.addItem("Freehand");
        shapeOptions.setMaximumSize(shapeOptions.getPreferredSize());
        shapeOptions.setMinimumSize(shapeOptions.getPreferredSize());
    }

    private void loadThicknessOption(){
        float value = 0.0f;
        for(int i=0; i<10; i++){
            value+=1.0f;
            thicknessOptions.addItem(value);
        }
        thicknessOptions.setMaximumSize(thicknessOptions.getPreferredSize());
        thicknessOptions.setMinimumSize(thicknessOptions.getPreferredSize());
    }

    MouseListener l = new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e) {
            for (JPanel colorPanel : colorPanels) {
                if (e.getComponent().equals(colorPanel)) {
                    Color selectedColor = colorPanel.getBackground();
                    System.out.println("ToolBar.mouseClicked selected color " +selectedColor);
                    //selectedColor.setBackground(select);
                    colorChangeListener.currentColor(selectedColor);
                }
            }
        }
    };

    public void setColorChangeListener(OnToolBarColorChanged colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
    }

    public Color getColor(JPanel panel){
        for (JPanel colorPanel : colorPanels) {
            if (panel == colorPanel) {
                return colorPanel.getBackground();
            }
        }
        return null;
    }

}
