package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class ImagePreviewer extends JFrame {
    BufferedImage imageToDisplay = null;
    int maxOutputWidth = 1600;
    int maxOutputHeight= 900;
    public ImagePreviewer(BufferedImage img){
        super("Image Preview");
        this.imageToDisplay = img;
    }

    public void initialize(){
        SwingUtilities.invokeLater(() -> {
            double pWidth = (double)maxOutputWidth / (double)imageToDisplay.getWidth();
            double pHeight = (double)maxOutputHeight / (double)imageToDisplay.getHeight();
            double minProp = Math.min(pHeight, pWidth);
            Image scaledImage = imageToDisplay.getScaledInstance((int)(imageToDisplay.getWidth() * minProp), (int)(imageToDisplay.getHeight() * minProp), Image.SCALE_SMOOTH);
            JLabel imageContainer = new JLabel(new ImageIcon(scaledImage));
            this.add(imageContainer, BorderLayout.CENTER);
            this.setBackground(Color.black);
            this.pack();
            this.setResizable(false);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setVisible(true);
        });
    }
}
