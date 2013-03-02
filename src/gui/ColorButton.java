package gui;

import java.awt.*;
import javax.swing.*;

/**
    * Yksinkertainen painike värin valitsemista varten. Painike näyttää, mikä värin on valittuna, ja avaa klikattaessa JColorChooser-dialogin värin vaihtamista varten.
    */
public class ColorButton extends JButton {
    private static final int WIDTH          = 16;
    private static final int HEIGHT         = 16;
    private static final int BORDER_WIDTH   = 1;
    
    private static final Color BORDER_COLOR = new Color(0, 0, 0);
    
    private Color color = new Color(255, 255, 255);
    private String chooserTitle = "Choose color";
    
    public ColorButton() {
        super();
    }
    
    /** 
        * Palauttaa painikkeen värin.
        *
        * @return Väri.
        */
    public Color getColor() {
        return this.color;
    }
    
    /**
        * Asettaa painikkeen värin.
        *
        * @param c Väri.
        */
    public void setColor(Color c) {
        this.color = c;
        this.repaint();
    }
    
    /**
        * Asettaa klikattaessa avattavan JColorChooser-dialogin otsikon.
        * 
        * @param title Otsikko.
        */
    public void setChooserTitle(String title) {
        this.chooserTitle = title;
    }
    
    /**
        * Palauttaa painikkeen koon.
        *
        * @return Painikkeen koko.
        */
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
    
    /**
        * Piirtää painikkeen.
        *
        * @param g Grafiikkakonteksti, jossa piirto tapahtuu.
        */
    public void paintComponent(Graphics g) {
        g.setColor(this.color);
        g.fillRect(0, 0, WIDTH-1, HEIGHT-1);
        g.setColor(BORDER_COLOR);
        g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
    }
    
    /**
        * Avaa JColorChooser-dialogin ja vaihtaa värin, mikäli uusi sellainen valitaan.
        */
    public void choose() {
        Color newColor = JColorChooser.showDialog(this, this.chooserTitle, this.getColor());
        if(newColor != null) {
            this.setColor(newColor);
        }
    }
}