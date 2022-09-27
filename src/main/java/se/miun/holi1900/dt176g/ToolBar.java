package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class ToolBar extends JToolBar {
    private JPanel colorPanelSContainer;

    List<JPanel> colorPanels = new ArrayList<>();
    private JComboBox<String> shapeOption;

    public ToolBar(MainFrame frame) {
        this.init(frame);
    }

    private void init(MainFrame frame){
        colorPanelSContainer = new JPanel(new GridLayout(1,6));
        shapeOption = new JComboBox<>();

        this.add(colorPanelSContainer);
        this.add(shapeOption);
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
            //panel.addMouseListener(l);
            colorPanelSContainer.add(panel);
        }
    }

   /* MouseListener l = new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e) {
            for (JPanel colorPanel : colorPanels) {
                if (e.getComponent() == colorPanel) {
                    Color select = colorPanel.getBackground();
                    selectedColor.setBackground(select);
                }
            }
        }
    };*/
}
