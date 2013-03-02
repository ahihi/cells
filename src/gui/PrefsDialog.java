package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cellularautomaton.*;
import utils.*;

/**
    * Asetusdialogi.
    */
public class PrefsDialog extends JDialog implements ActionListener {
    private final static String TITLE = "Preferences";
    
    private final static String LABEL_COLORS        = "Colors";
    private final static String LABEL_COLOR_LIVING  = "Living cell color";
    private final static String LABEL_COLOR_DEAD    = "Dead cell color";
    private final static String LABEL_COLOR_BORDER  = "Border color";
    private final static String LABEL_CELLSIZE      = "Cell size";
    private final static String LABEL_BORDERWIDTH   = "Border width";
    private final static String LABEL_HISTORYLENGTH = "History length";
    private final static String LABEL_MAXFPS        = "Max. animation speed";
    private final static String LABEL_PIXELS        = " px";
    private final static String LABEL_FPS           = " FPS";
    
    private final static String BUTTON_REVERT   = "Revert";
    private final static String BUTTON_APPLY    = "Apply";
    
    private final static int BORDER_PADDING = 5;    
    
    private MainWindow owner;
    private Container contentPane;
    
    private ColorButton cbLiving;
    private ColorButton cbDead;
    private ColorButton cbBorder;
    
    private IntField cellSizeField;
    private IntField borderWidthField;
    private IntField historyLengthField;
    private IntField maxFPSField;
    
    private JButton revertButton;
    private JButton applyButton;
    
    /**
        * Luo uuden asetusdialogin. Tämä dialogi on ei-modaali, joten pääikkunan toimintoja voi käyttää dialogin ollessa auki.
        *
        * @param owner Pääikkuna, johon asetusdialogi on sidoksissa.
        */
    public PrefsDialog(MainWindow owner) {        
        super(owner, TITLE, false);        
        
        this.owner = owner;
        this.contentPane = this.getContentPane();
        
        this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));
        this.setResizable(false);
                
        this.initEditables();
        this.addPanels();
        
        this.pack();
        this.revert();
    }
    
    /**
        * Alustaa muokattavat kentät.
        */
    private void initEditables() {
        this.cbLiving = new ColorButton();
        this.cbLiving.setChooserTitle(LABEL_COLOR_LIVING);
        this.cbLiving.addActionListener(this);
        
        this.cbDead = new ColorButton();
        this.cbDead.setChooserTitle(LABEL_COLOR_DEAD);
        this.cbDead.addActionListener(this);
        
        this.cbBorder = new ColorButton();
        this.cbBorder.setChooserTitle(LABEL_COLOR_BORDER);
        this.cbBorder.addActionListener(this);
        
        this.cellSizeField = new IntField();
        this.borderWidthField = new IntField();
        this.historyLengthField = new IntField();
        this.maxFPSField = new IntField();
        
        this.revertButton = new JButton(BUTTON_REVERT);
        this.revertButton.addActionListener(this);
        this.applyButton = new JButton(BUTTON_APPLY);
        this.applyButton.addActionListener(this);
    }
    
    /**
        * Lisää kaikki paneelit dialogiin.
        */
    private void addPanels() {
        this.addColorsPanel();
        this.addCellSizePanel();
        this.addBorderWidthPanel();
        this.addHistoryLengthPanel();
        this.addMaxFPSPanel();
        this.addButtonPanel();        
    }
    
    /**
        * Lisää värivalintapaneelin.
        */
    private void addColorsPanel() {
        JComponent panel = getBorderedPanel(LABEL_COLORS);
        JComponent innerPanel = new JPanel();
        Dimension fillerDim = new Dimension(0, 2);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.add(getColorRow(LABEL_COLOR_LIVING, this.cbLiving));
        innerPanel.add(new Box.Filler(fillerDim, fillerDim, fillerDim));
        innerPanel.add(getColorRow(LABEL_COLOR_DEAD, this.cbDead));
        innerPanel.add(new Box.Filler(fillerDim, fillerDim, fillerDim));
        innerPanel.add(getColorRow(LABEL_COLOR_BORDER, this.cbBorder));
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Luo yhden väripaneelin rivin.
        *
        * @param label Värin nimike.
        * @param cb Väripainike.
        *
        * @return Väripaneeliin tuleva alapaneeli.
        */
    private static JPanel getColorRow(String label, ColorButton cb) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(label));
        panel.add(Box.createHorizontalGlue());
        panel.add(cb);
        return panel;
    }
    
    /**
        * Lisää paneelin solujen koon muokkausta varten.
        */
    private void addCellSizePanel() {
        JComponent panel = getBorderedPanel(LABEL_CELLSIZE);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));        
        innerPanel.add(this.cellSizeField);
        innerPanel.add(new JLabel(LABEL_PIXELS));
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää paneelin reunuksien paksuuden muokkausta varten.
        */
    private void addBorderWidthPanel() {
        JComponent panel = getBorderedPanel(LABEL_BORDERWIDTH);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));        
        innerPanel.add(this.borderWidthField);
        innerPanel.add(new JLabel(LABEL_PIXELS));
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää paneelin historian pituuden muokkausta varten.
        */
    private void addHistoryLengthPanel() {
        JComponent panel = getBorderedPanel(LABEL_HISTORYLENGTH);
        panel.add(this.historyLengthField);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää paneelin animaation maksimiruututaajuuden muokkausta varten.
        */
    private void addMaxFPSPanel() {
        JComponent panel = getBorderedPanel(LABEL_MAXFPS);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));        
        innerPanel.add(this.maxFPSField);
        innerPanel.add(new JLabel(LABEL_FPS));
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää revert/apply-paneelin.
        */
    private void addButtonPanel() {
        JComponent panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this.revertButton);
        panel.add(this.applyButton);
        this.contentPane.add(panel);
    }
    
    /**
        * Luo käärepaneelin, jolla on reunukset ja otsikko.
        * 
        * @param title Paneelin otsikko.
        *
        * @return Paneeli.
        */
    private static JPanel getBorderedPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING)
            )
        );
        return panel;
    }

    /**
        * Alustaa dialogin muokattavat arvot vastaamaan soluautomaatin nykyistä tilaa.
        */
    public void revert() {
        BinaryGrid grid = this.owner.getGrid();
        
        this.cbLiving.setColor(this.owner.getGrid().getAliveColor());
        this.cbDead.setColor(this.owner.getGrid().getDeadColor());
        this.cbBorder.setColor(this.owner.getGrid().getBorderColor());
        
        this.cellSizeField.setValue(grid.getCellSize());
        this.borderWidthField.setValue(grid.getBorderWidth());
        this.historyLengthField.setValue(grid.getGame().getHistoryLength());
        this.maxFPSField.setValue(grid.getAnimationFPS());
    }
    
    /**
        * Propagoi dialogin muokattavat arvot soluautomaattiin.
        */
    private void apply() {
        BinaryGrid grid = this.owner.getGrid();
        
        grid.setAliveColor(this.cbLiving.getColor());
        grid.setDeadColor(this.cbDead.getColor());
        grid.setBorderColor(this.cbBorder.getColor());
        
        grid.setCellSize(this.cellSizeField.getIntValue());        
        grid.setBorderWidth(this.borderWidthField.getIntValue());
        grid.getGame().setHistoryLength(this.historyLengthField.getIntValue());
        grid.setAnimationFPS(this.maxFPSField.getIntValue());
        
        this.revert();
    }
    
    /**
        * ActionListener-rajapinnan metodi, joka kuvaa painikkeiden painallukset oikeisiin operaatioihin.
        *
        * @param ev Tapahtuma.
        */
    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();
        if(source == this.cbLiving || source == this.cbDead || source == this.cbBorder) {
            ((ColorButton) source).choose();
        } else if(source == this.revertButton) {
            this.revert();
        } else if(source == this.applyButton) {
            this.apply();
        }
    }
}