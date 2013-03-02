package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cellularautomaton.*;
import utils.*;

public class RulesDialog extends JDialog implements ActionListener {
    private final static String TITLE = "Rules";
    
    private final static String LABEL_EB                = "Edge behavior";
    private final static String LABEL_RULES_BIRTH       = "Birth";
    private final static String LABEL_RULES_SURVIVAL    = "Survival";
    private final static String LABEL_GRIDSIZE          = "Grid size";
    
    private final static String LABEL_DIM_SEPARATOR     = "x";
    
    private final static String RADIO_EB_ALIVE  = "Alive";
    private final static String RADIO_EB_DEAD   = "Dead";
    private final static String RADIO_EB_WRAP   = "Wrap";
    
    private final static String BUTTON_REVERT   = "Revert";
    private final static String BUTTON_APPLY    = "Apply";
    
    private final static int BORDER_PADDING = 5;    
    
    private MainWindow owner;
    private Container contentPane;
    
    private IntField colsField;
    private IntField rowsField;
    
    private JCheckBox[] birthRuleBoxes;
    private JCheckBox[] survivalRuleBoxes;
    
    private ButtonGroup ebChoice;
    private JRadioButton aliveRadio;
    private JRadioButton deadRadio;
    private JRadioButton wrapRadio;
    
    private JButton revertButton;
    private JButton applyButton;
    
    /**
        * Luo uuden sääntödialogin. Tämä dialogi on ei-modaali, joten pääikkunan toimintoja voi käyttää dialogin ollessa auki.
        *
        * @param owner Pääikkuna, johon sääntödialogi on sidoksissa.
        */
    public RulesDialog(MainWindow owner) {
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
        this.colsField = new IntField();
        this.rowsField = new IntField();
        
        this.birthRuleBoxes = this.getCheckBoxes();
        this.survivalRuleBoxes = this.getCheckBoxes();
        
        this.aliveRadio = new JRadioButton(RADIO_EB_ALIVE);
        this.deadRadio = new JRadioButton(RADIO_EB_DEAD);
        this.wrapRadio = new JRadioButton(RADIO_EB_WRAP);
        
        this.ebChoice = new ButtonGroup();
        this.ebChoice.add(aliveRadio);
        this.ebChoice.add(deadRadio);
        this.ebChoice.add(wrapRadio);
        
        this.revertButton = new JButton(BUTTON_REVERT);
        this.revertButton.addActionListener(this);
        this.applyButton = new JButton(BUTTON_APPLY);
        this.applyButton.addActionListener(this);
    }
    
    /**
        * Lisää kaikki paneelit dialogiin.
        */
    private void addPanels() {
        this.addGridSizePanel();
        this.addRulesPanel();
        this.addEdgeBehaviorPanel();
        this.addButtonPanel();
    }
        
    /**
        * Lisää paneelin soluautomaatin sarake- ja rivimäärän muuttamista varten.
        */
    private void addGridSizePanel() {
        JComponent panel = getBorderedPanel(LABEL_GRIDSIZE);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        innerPanel.add(this.colsField);
        innerPanel.add(new JLabel(LABEL_DIM_SEPARATOR));
        innerPanel.add(this.rowsField);
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää paneelit soluautomaatin syntymä- ja eloonjäämissääntöjen muuttamista varten.
        */
    private void addRulesPanel() {
        this.addRulePanel(birthRuleBoxes, LABEL_RULES_BIRTH);
        this.addRulePanel(survivalRuleBoxes, LABEL_RULES_SURVIVAL);
    }
    
    /**
        * Lisää yhden sääntöpaneelin.
        *
        * @param boxes Paneeliin laitettavat checkboxit.
        * @param title Paneelin otsikko.
        */
    private void addRulePanel(JCheckBox[] boxes, String title) {
        JComponent panel = getBorderedPanel(title);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        for(JCheckBox box : boxes) {
            innerPanel.add(box);
        }
        panel.add(innerPanel);
        this.contentPane.add(panel);
    }
    
    /**
        * Lisää paneelin soluautomaatin reunakäyttäytymisen muuttamista varten.
        */
    private void addEdgeBehaviorPanel() {
        JComponent panel = getBorderedPanel(LABEL_EB);
        JComponent innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        innerPanel.add(Box.createHorizontalGlue());
        innerPanel.add(this.aliveRadio);
        innerPanel.add(this.deadRadio);
        innerPanel.add(this.wrapRadio);
        innerPanel.add(Box.createHorizontalGlue());
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
        * Palauttaa annettua reunakäyttäytymistä vastaavan radiopainikkeen.
        *
        * @param eb Reunakäyttäytyminen.
        *
        * @return Radiopainike.
        */
    private JRadioButton getEBRadio(CARules.EdgeBehavior eb) {
        if(eb == CARules.EdgeBehavior.ALIVE) {
            return this.aliveRadio;
        } else if(eb == CARules.EdgeBehavior.DEAD) {
            return this.deadRadio;
        } else if(eb == CARules.EdgeBehavior.WRAP) {
            return this.wrapRadio;
        } else {
            return null;
        }
    }
    
    /**
        * Palauttaa annettua radiopainiketta vastaavan reunakäyttäytymisen.
        *
        * @param radio Radiopainike.
        *
        * @return Reunakäyttäytyminen.
        */
    private CARules.EdgeBehavior getModelEB(ButtonModel radio) {
        if(radio == aliveRadio.getModel()) {
            return CARules.EdgeBehavior.ALIVE;
        } else if(radio == deadRadio.getModel()) {
            return CARules.EdgeBehavior.DEAD;
        } else if(radio == wrapRadio.getModel()) {
            return CARules.EdgeBehavior.WRAP;
        } else {
            return null;
        }
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
        * Luo checkboxin jokaiselle mahdolliselle elävien naapureiden lukumäärälle.
        *
        * @return Checkbox-taulukko.
        */
    private JCheckBox[] getCheckBoxes() {
        int count = this.owner.getGrid().getGame().getRules().NEIGHBOR_COUNT;
        JCheckBox[] boxes = new JCheckBox[count];
        for(int i = 0; i < count; i++) {
            boxes[i] = new JCheckBox("" + i);
        }
        return boxes;
    }
    
    /**
        * Alustaa dialogin muokattavat arvot vastaamaan soluautomaatin nykyistä tilaa.
        */
    public void revert() {
        CA game = this.owner.getGrid().getGame();
        CARules rules = game.getRules();
        
        this.colsField.setValue(game.getCols());
        this.rowsField.setValue(game.getRows());
        
        for(int i = 0; i < rules.NEIGHBOR_COUNT; i++) {
            this.birthRuleBoxes[i].setSelected(rules.getBirthRule(i));
            this.survivalRuleBoxes[i].setSelected(rules.getSurvivalRule(i));
        }
        
        this.ebChoice.setSelected(this.getEBRadio(rules.getEdgeBehavior()).getModel(), true);
        //this.repaint();
    }
    
    /**
        * Propagoi dialogin muokattavat arvot soluautomaattiin.
        */
    private void apply() {
        CA game = this.owner.getGrid().getGame();
        BinaryGrid grid = this.owner.getGrid();
        CARules rules = game.getRules();
        
        CARules.EdgeBehavior newEB = this.getModelEB(this.ebChoice.getSelection());
        
        CARules newRules = new CARules();
        newRules.setEdgeBehavior(newEB);

        for(int i = 0; i < rules.NEIGHBOR_COUNT; i++) {
            newRules.setBirthRule(i, this.birthRuleBoxes[i].isSelected());
            newRules.setSurvivalRule(i, this.survivalRuleBoxes[i].isSelected());
        }
                
        game.setRules(newRules);
        
        grid.resizeGrid(this.colsField.getIntValue(), this.rowsField.getIntValue());
        
        this.revert();
    }
    
    /**
        * ActionListener-rajapinnan metodi, joka kuvaa painikkeiden painallukset oikeisiin operaatioihin.
        *
        * @param ev Tapahtuma.
        */
    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();
        if(source == this.revertButton) {
            this.revert();
        } else if(source == this.applyButton) {
            this.apply();
        }
    }
}