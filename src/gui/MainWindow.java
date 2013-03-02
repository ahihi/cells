package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import cellularautomaton.*;
import utils.*;

/**
    * Pääikkuna, jossa on graafinen soluruudukko ja sen manipuloimiseen tarvittavat painikkeet. Tämä ikkuna vastaa myös valikkorivin sekä sääntö-, asetus-, tallennus- ja avausdialogien luomisesta ja näyttämisestä.
    */
public class MainWindow extends JFrame implements ActionListener {
    // Nimeämättömän ikkunan otsikko.
    private final static String TITLE_UNTITLED  = "Untitled";
    
    // Tallennustiedostojen tiedostopääte.
    private final static String FILE_EXTENSION = "cells";
    
    // Valikkojen nimet.
    private final static String MENU_FILE   = "File";
    private final static String MENU_GAME   = "Game";
    
    // Tiedostovalikon pikavalintakirjaimet.
    private final static int MNEMO_FILE         = KeyEvent.VK_F;
    private final static int MNEMO_FILE_NEW     = KeyEvent.VK_N;
    private final static int MNEMO_FILE_OPEN    = KeyEvent.VK_O;
    private final static int MNEMO_FILE_CLOSE   = KeyEvent.VK_C;
    private final static int MNEMO_FILE_SAVE    = KeyEvent.VK_S;
    
    // Pelivalikon pikavalintakirjaimet.
    private final static int MNEMO_GAME         = KeyEvent.VK_G;
    private final static int MNEMO_GAME_RUN     = KeyEvent.VK_R;
    private final static int MNEMO_GAME_STOP    = KeyEvent.VK_S;
    private final static int MNEMO_GAME_RECALL  = KeyEvent.VK_E;
    private final static int MNEMO_GAME_STORE   = KeyEvent.VK_T;
    private final static int MNEMO_GAME_ERASE   = KeyEvent.VK_A;
    private final static int MNEMO_GAME_RULES   = KeyEvent.VK_U;
    private final static int MNEMO_GAME_PREFS   = KeyEvent.VK_P;
    
    // Tiedostovalikon näppäinoikotiet.
    private final static KeyStroke ACCEL_FILE_NEW   = getAccelKeyStroke(KeyEvent.VK_N, 0);
    private final static KeyStroke ACCEL_FILE_OPEN  = getAccelKeyStroke(KeyEvent.VK_O, 0);
    private final static KeyStroke ACCEL_FILE_CLOSE = getAccelKeyStroke(KeyEvent.VK_W, 0);
    private final static KeyStroke ACCEL_FILE_SAVE  = getAccelKeyStroke(KeyEvent.VK_S, 0);
    
    // Pelivalikon näppäinoikotiet.
    private final static KeyStroke ACCEL_GAME_RUN       = getAccelKeyStroke(KeyEvent.VK_G, 0);
    private final static KeyStroke ACCEL_GAME_STOP      = getAccelKeyStroke(KeyEvent.VK_PERIOD, 0);
    private final static KeyStroke ACCEL_GAME_RECALL    = getAccelKeyStroke(KeyEvent.VK_R, 0);
    private final static KeyStroke ACCEL_GAME_STORE     = getAccelKeyStroke(KeyEvent.VK_T, 0);
    private final static KeyStroke ACCEL_GAME_ERASE     = getAccelKeyStroke(KeyEvent.VK_E, 0);
    private final static KeyStroke ACCEL_GAME_RULES     = getAccelKeyStroke(KeyEvent.VK_COMMA, InputEvent.ALT_MASK);
    private final static KeyStroke ACCEL_GAME_PREFS     = getAccelKeyStroke(KeyEvent.VK_COMMA, 0);
    
    // Tiedostovalikon nimikkeet.
    private final static String LABEL_NEW       = "New";
    private final static String LABEL_OPEN      = "Open...";
    private final static String LABEL_CLOSE     = "Close";
    private final static String LABEL_SAVE      = "Save As...";
    
    // Pelivalikon (ja vastaavien painikkeiden) nimikkeet.
    private final static String LABEL_RUN       = "Run";
    private final static String LABEL_STOP      = "Stop";
    private final static String LABEL_STEP      = "Step";
    private final static String LABEL_RECALL    = "Recall";
    private final static String LABEL_STORE     = "Store";
    private final static String LABEL_ERASE     = "Erase";
    private final static String LABEL_RULES     = "Rules...";
    private final static String LABEL_PREFS     = "Preferences...";
    
    // Päällekirjoitusdialogin painikkeiden nimikkeet.
    private final static String OVERWRITE_BUTTON_YES    = "Replace";
    private final static String OVERWRITE_BUTTON_NO     = "Cancel";
    
    // Painikkeet.
    private JButton runButton       = new JButton(LABEL_RUN);
    private JButton stepButton      = new JButton(LABEL_STEP);
    private JButton recallButton    = new JButton(LABEL_RECALL);
    private JButton storeButton     = new JButton(LABEL_STORE);
    private JButton eraseButton     = new JButton(LABEL_ERASE);
    private JButton rulesButton     = new JButton(LABEL_RULES);
    
    // Tiedostovalikon kohdat.
    private JMenuItem fileNewMenuItem       = new JMenuItem(LABEL_NEW);
    private JMenuItem fileOpenMenuItem      = new JMenuItem(LABEL_OPEN);
    private JMenuItem fileCloseMenuItem     = new JMenuItem(LABEL_CLOSE);
    private JMenuItem fileSaveMenuItem      = new JMenuItem(LABEL_SAVE);
    
    // Pelivalikon kohdat.
    private JMenuItem gameRunMenuItem       = new JMenuItem(LABEL_RUN);
    private JMenuItem gameStopMenuItem      = new JMenuItem(LABEL_STOP);
    private JMenuItem gameRecallMenuItem    = new JMenuItem(LABEL_RECALL);
    private JMenuItem gameStoreMenuItem     = new JMenuItem(LABEL_STORE);
    private JMenuItem gameEraseMenuItem     = new JMenuItem(LABEL_ERASE);
    private JMenuItem gameRulesMenuItem     = new JMenuItem(LABEL_RULES);
    private JMenuItem gamePrefsMenuItem     = new JMenuItem(LABEL_PREFS);
    
    /**
        * Asetusdialogi.
        */
    private PrefsDialog dPrefs;
    
    /**
        * Sääntödialogi.
        */
    private RulesDialog dRules;
    
    /**
        * Graafinen soluruudukon esitys.
        */
    private BinaryGrid grid;
    
    /**
        * Luo ja alustaa pääikkunan.
        */
    public MainWindow() {
        super(TITLE_UNTITLED);
                
        this.grid = new BinaryGrid(new CA(), this);
        this.dPrefs = new PrefsDialog(this);
        this.dRules = new RulesDialog(this);
        
        this.createAndSetMenuBar();
    
        Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    
        pane.add(this.createActionPanel());
        pane.add(this.grid);
    
        this.setResizable(false);
    }

    /**
        * ActionListener-rajapinnan metodi, joka kuvaa erilaiset tapahtumat (esim painikkeiden painallukset) oikeisiin operaatioihin.
        *
        * @param ev Tapahtuma.
        */
    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();
        if(source == this.stepButton) {
            grid.evolve();
        } else if(source == this.runButton || source == this.gameRunMenuItem || source == this.gameStopMenuItem) {
            if(this.getGrid().animationIsRunning()) {
                this.stop();
            } else {
                this.start();
            }
        } else if(source == this.storeButton || source == this.gameStoreMenuItem) {
            grid.store();
        } else if(source == this.recallButton || source == this.gameRecallMenuItem) {
            grid.recall();
        } else if(source == this.eraseButton || source == this.gameEraseMenuItem) {
            grid.erase();
        } else if(source == this.rulesButton || source == this.gameRulesMenuItem) {
            this.dRules.setVisible(true);
        } else if(source == this.gamePrefsMenuItem) {
            this.dPrefs.setVisible(true);
        } else if(source == this.fileNewMenuItem) {
            this.reset();
        } else if(source == this.fileOpenMenuItem) {
            this.load();
        } else if(source == this.fileCloseMenuItem) {
            this.dispose();
            System.exit(0);
        } else if(source == this.fileSaveMenuItem) {
            this.save();
        }
    }
    
    /**
        * Alustaa pääikkunan takaisin alkutilaan.
        */
    private void reset() {
        this.grid.updateGameState(new CAState());
        this.revertDialogs();
        this.repack();
        this.setTitle(TITLE_UNTITLED);
    }
    
    /**
        * Avaa tiedostonvalintadialogin ja lukee uuden soluautomaattitilan valitusta tallennustiedostosta. Virheen tapahtuessa näytetään virheilmoitus.
        */
    private void load() {
        JFileChooser fc = new JFileChooser();
        File defaultFile = null;
        boolean loading = true;
        
        while(loading) {
            fc.setSelectedFile(defaultFile);
            int result = fc.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION) {
                File loadFile = fc.getSelectedFile();
                try {
                    FileLoader r = new FileLoader(loadFile);
                    String data = r.readAll();
                    r.close();
                    CAState newState = CAFile.read(data);
                    if(newState != null) {
                        this.grid.updateGameState(newState);
                        this.revertDialogs();
                        this.setTitle(loadFile.getName());
                        loading = false;
                    } else {
                        JOptionPane.showMessageDialog(this, "The file " + loadFile.getPath() + " does not contain valid save data.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch(FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch(IOException e) {
                    JOptionPane.showMessageDialog(this, "Input/output error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                loading = false;
            }
        }
    }
    
    /**
        * Avaa tiedostonvalintadialogin ja tallentaa nykyisen soluautomaattitilan valittuun tiedostoon. Virheen tapahtuessa näytetään virheilmoitus.
        */
    private void save() {
        JFileChooser fc = new JFileChooser();
        File currentDir = new File(".");
        File defaultFile = new File(currentDir, "Untitled." + FILE_EXTENSION);
        boolean saving = true;
        
        while(saving) {
            fc.setSelectedFile(defaultFile);
            int result = fc.showSaveDialog(this);
            if(result == JFileChooser.APPROVE_OPTION) {
                File saveFile = fc.getSelectedFile();
                boolean doSave = true;
                if(saveFile.exists()) {
                    String message = "The file " + saveFile.getPath() + " already exists. Are you sure you want to replace it?";
                    int replaceResult = JOptionPane.showConfirmDialog(this, message, "File exists", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
                    if(replaceResult == JOptionPane.CANCEL_OPTION) {
                        doSave = false;
                    }
                }
                if(doSave) {
                    try {
                        FileWriter w = new FileWriter(saveFile);
                        w.write(CAFile.write(this.getGrid().getGame()));
                        w.close();
                        this.setTitle(saveFile.getName());
                        saving = false;
                    } catch(FileNotFoundException e) {
                        JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(IOException e) {
                        JOptionPane.showMessageDialog(this, "Input/output error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    defaultFile = saveFile;
                }
            } else {
                saving = false;
            }
        }
    }
    
    /**
        * Alustaa sääntö- ja asetusdialogien muokattavat arvot vastaamaan soluautomaatin nykyistä tilaa.
        */
    private void revertDialogs() {
        this.dRules.revert();
        this.dPrefs.revert();
    }
    
    /**
        * Luo painikerivin.
        *
        * @return Painikerivin paneeli.
        */
    private Container createActionPanel() {
        Container panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        panel.add(this.runButton);
        this.runButton.addActionListener(this);
        panel.add(this.stepButton);
        this.stepButton.addActionListener(this);
        
        panel.add(Box.createHorizontalGlue());
        
        panel.add(this.recallButton);
        this.recallButton.addActionListener(this);
        panel.add(this.storeButton);
        this.storeButton.addActionListener(this);
        panel.add(this.eraseButton);
        this.eraseButton.addActionListener(this);
        
        panel.add(Box.createHorizontalGlue());
        
        panel.add(this.rulesButton);
        this.rulesButton.addActionListener(this);
        
        return panel;
    }
    
    /**
        * Luo valikkorivin.
        */
    private void createAndSetMenuBar() {
        // OSX: laitetaan valikot järjestelmän valikkoriviin
        if(System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem item;
        
        // Tiedostovalikko
        menu = new JMenu(MENU_FILE);
        menu.setMnemonic(MNEMO_FILE);
        
        item = this.fileNewMenuItem;
        item.setMnemonic(MNEMO_FILE_NEW);
        item.setAccelerator(ACCEL_FILE_NEW);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.fileOpenMenuItem;
        item.setMnemonic(MNEMO_FILE_OPEN);
        item.setAccelerator(ACCEL_FILE_OPEN);
        item.addActionListener(this);
        menu.add(item);
        
        menu.addSeparator();
        
        item = this.fileCloseMenuItem;
        item.setMnemonic(MNEMO_FILE_CLOSE);
        item.setAccelerator(ACCEL_FILE_CLOSE);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.fileSaveMenuItem;
        item.setMnemonic(MNEMO_FILE_SAVE);
        item.setAccelerator(ACCEL_FILE_SAVE);
        item.addActionListener(this);
        menu.add(item);
                
        menuBar.add(menu);
        
        // Pelivalikko
        menu = new JMenu(MENU_GAME);
        menu.setMnemonic(MNEMO_GAME);
        
        item = this.gameRunMenuItem;
        item.setMnemonic(MNEMO_GAME_RUN);
        item.setAccelerator(ACCEL_GAME_RUN);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.gameStopMenuItem;
        item.setMnemonic(MNEMO_GAME_STOP);
        item.setAccelerator(ACCEL_GAME_STOP);
        item.addActionListener(this);
        item.setEnabled(false);
        menu.add(item);
        
        menu.addSeparator();
        
        item = this.gameStoreMenuItem;
        item.setMnemonic(MNEMO_GAME_STORE);
        item.setAccelerator(ACCEL_GAME_STORE);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.gameRecallMenuItem;
        item.setMnemonic(MNEMO_GAME_RECALL);
        item.setAccelerator(ACCEL_GAME_RECALL);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.gameEraseMenuItem;
        item.setMnemonic(MNEMO_GAME_ERASE);
        item.setAccelerator(ACCEL_GAME_ERASE);
        item.addActionListener(this);
        menu.add(item);
        
        menu.addSeparator();
        
        item = this.gameRulesMenuItem;
        item.setMnemonic(MNEMO_GAME_RULES);
        item.setAccelerator(ACCEL_GAME_RULES);
        item.addActionListener(this);
        menu.add(item);
        
        item = this.gamePrefsMenuItem;
        item.setMnemonic(MNEMO_GAME_PREFS);
        item.setAccelerator(ACCEL_GAME_PREFS);
        item.addActionListener(this);
        menu.add(item);
        
        menuBar.add(menu);
        
        this.setJMenuBar(menuBar);
    }
    
    /**
        * Palauttaa käyttöjärjestelmästä riippuvan näppäinmaskin näppäinoikoteitä varten. OSX:llä tämä vastaa command-äppäintä, muilla järjestelmillä ctrl-näppäintä.
        *
        * @return Näppäinmaskin koodi.
        */
    private static int getAccelMask() {
        if(System.getProperty("os.name").equals("Mac OS X")) {
            return InputEvent.META_MASK;
        } else {
            return InputEvent.CTRL_MASK;
        }
    }
    
    /**
        * Palauttaa käyttöjärjestelmästä riippuvan näppäinoikotien annetulle näppäimelle ja modifioijanäppäinmaskille. Esimerkiksi jos parametreina annetaan (A, shift), metodi tuottaa näppäinoikotien cmd+shift+A (OSX:llä) tai ctrl+shift+A (muillä järjestelmillä).
        *
        * @param key Näppäin.
        * @param modifiers Modifioijamaski. 0 tarkoittaa, ettei lisätä mitään modifioijanäppäimiä.
        */
    private static KeyStroke getAccelKeyStroke(int key, int modifiers) {
        return KeyStroke.getKeyStroke(key, getAccelMask() | modifiers);
    }
    
    /**
        * Palauttaa pääikkunan käyttämän graafisen ruudukon.
        *
        * @return Graafinen ruudukko.
        */
    public BinaryGrid getGrid() {
        return this.grid;
    }
    
    /**
        * Muuttaa pääikkunan kokoa graafisen ruudukon mukaiseksi.
        */
    public void repack() {
        this.getGrid().invalidate();
        this.pack();
    }
    
    /**
        * Käynnistää evoluutioanimaation ja vaihtaa käynnistyspainikkeen nimikettä sekä pelivalikon vastaavien kohtien valittavuutta.
        */
    public void start() {
        grid.startAnimation();
        this.runButton.setText(LABEL_STOP);
        this.gameRunMenuItem.setEnabled(false);
        this.gameStopMenuItem.setEnabled(true);
    }
    
    /**
        * Pysäyttää evoluutioanimaation ja vaihtaa käynnistyspainikkeen nimikettä sekä pelivalikon vastaavien kohtien valittavuutta.
        */
    public void stop() {
        this.grid.stopAnimation();
        this.runButton.setText(LABEL_RUN);
        this.gameRunMenuItem.setEnabled(true);
        this.gameStopMenuItem.setEnabled(false);
    }
}