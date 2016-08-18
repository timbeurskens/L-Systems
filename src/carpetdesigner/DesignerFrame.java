package carpetdesigner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import static main.ApplicationLoader.outputTextContent;

/**
 * L-System
 * Created by s154796 on 10-8-2016.
 */
public class DesignerFrame extends JFrame {
    private int horizontalCount;
    private int verticalCount;
    private CarpetCell[][] currentCarpet;
    private ArrayList<String> additionalSettings = new ArrayList<>();
    private String outFile = null;
    private boolean defaultValue = false;
    private String jumpSequence = "f";
    private boolean extended = false;
    public DesignerFrame(String title, int hor, int ver){
        super(title);
        setSize(hor, ver);
    }

    public void setSize(int width, int height){
        horizontalCount = width;
        verticalCount = height;
        currentCarpet = new CarpetCell[horizontalCount][verticalCount];
    }

    public void putSetting(String st){
        additionalSettings.add(st);
    }

    public String export(){
        String blockRule = "B=";
        String widthRule = "w=";
        String heightRule = "h=";
        for(int h = 0; h < horizontalCount; h++){
            blockRule = blockRule + "[";
            for(int v = verticalCount - 1; v >= 0; v--){
                blockRule = blockRule + (currentCarpet[h][v].isEnabled() ? "B" : "h");
            }
            blockRule = blockRule + "]" + (h < horizontalCount - 1 ? "+w-" : (extended ? "[+w]" : ""));
        }
        blockRule = blockRule + "-";
        for(int h = 0; h < horizontalCount - 1; h++){
            blockRule = blockRule + "w";
            widthRule = widthRule + "w";
        }
        widthRule = widthRule + "w";
        blockRule = blockRule + "+";
        for(int v = 0; v < verticalCount; v++){
            blockRule = blockRule + "h";
            heightRule = heightRule + "h";
        }
        return "[rules]\r\n" + blockRule + "\r\n" + widthRule + "\r\n" + heightRule + "\r\n[turtle]\r\nB={g+g+g+g+}f\r\nw=" + jumpSequence + "\r\nh=" + jumpSequence + "\r\n[settings]\r\naxiom=B\r\nangle=90\r\n" + String.join("\r\n", additionalSettings);
    }

    public void initialize(){
        SwingUtilities.invokeLater(() -> {
            JPanel carpetPanel = new JPanel();
            GridLayout carpetLayoutGrid = new GridLayout(verticalCount, horizontalCount);
            carpetLayoutGrid.setHgap(5);
            carpetLayoutGrid.setVgap(5);
            carpetPanel.setLayout(carpetLayoutGrid);

            for(int v = 0; v < verticalCount; v++){
                for(int h = 0; h < horizontalCount; h++){
                    currentCarpet[h][v] = new CarpetCell(defaultValue);
                    carpetPanel.add(currentCarpet[h][v]);
                }
            }

            JButton acceptBtn = new JButton("Accept");
            acceptBtn.addActionListener(e -> {
                if(outFile != null){
                    outputTextContent(outFile, this.export());
                }else{
                    System.out.println(this.export());
                }
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            });

            this.add(carpetPanel, BorderLayout.CENTER);
            this.add(acceptBtn, BorderLayout.SOUTH);

            this.pack();
            this.setResizable(false);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setVisible(true);
        });
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setJumpSequence(String jumpSequence) {
        this.jumpSequence = jumpSequence;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }
}
