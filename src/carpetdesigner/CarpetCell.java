package carpetdesigner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * L-System
 * Created by s154796 on 10-8-2016.
 */
public class CarpetCell extends JPanel implements MouseListener{
    private boolean enabled = false;

    public CarpetCell(boolean startState){
        this();
        this.enabled = startState;
    }

    public CarpetCell(){
        this.addMouseListener(this);
        this.setPreferredSize(new Dimension(50, 50));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setBackground(isEnabled() ? Color.black : Color.white);
        g2d.clearRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.setEnabled(!this.isEnabled());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
