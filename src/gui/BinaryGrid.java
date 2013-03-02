package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import cellularautomaton.*;
import utils.*;

/**
    * Graafinen esitys soluruudukolle.
    */
public class BinaryGrid extends JPanel {
    // Minimiarvot muuttujille.
    private static final int MIN_CELL_SIZE      = 1;
    private static final int MIN_BORDER_WIDTH   = 0;
    private static final int MIN_ANIMATION_FPS  = 1;
    
    /**
        * Soluruutujen koko.
        */
    private int cellSize = 16;
    
    /**
        * Solujen välisten reunusten paksuus.
        */
    private int borderWidth = 1;
    
    /**
        * Evoluutioanimaation ruututaajuus.
        */
    private int animationFPS = 20;
    
    /**
        * Kuolleiden solujen väri.
        */
    private Color deadColor = new Color(1.0f, 1.0f, 1.0f);
    
    /**
        * Elävien solujen väri.
        */
    private Color aliveColor = new Color(0.0f, 0.0f, 0.0f);
    
    /**
        * Reunusten väri.
        */
    private Color borderColor = new Color(0.5f, 0.5f, 0.5f);

    /**
        * Soluautomaatti, jota graafinen ruudukko esittää.
        */
    private CA game;
    
    /**
        * Pääikkuna, johon graafinen ruudukko on sidoksissa.
        */
    private MainWindow owner;
    
    /**
        * Viimeksi piirretyn solun koordinaatti.
        */
    private Cell lastDrawn;
    
    /**
        * Tila, jonka saavat solut, joiden päälle piirretään hiirinapin ollessa alhaalla. Asettuu kuolleeksi, jos piirtäminen aloitetaan elävästä solusta ja päin vastoin.
        */
    private boolean paintLive = true;
    
    /**
        * Evoluutioanimaation ajastin.
        */
    private Timer timer;
    
    /**
        * @param game Soluautomaatti, jonka tilaa graafinen ruudukko esittää.
        * @param owner Pääikkuna, johon graafinen ruudukko on sidoksissa.
        */
    public BinaryGrid(CA game, MainWindow owner) {
        this.game = game;
        this.owner = owner;
        
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e, false);
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                handleMouseEvent(e, true);
            }
        });
        
        this.initTimer();
        this.setAnimationFPS(animationFPS);
    }
    
    /**
        * Palauttaa graafisen ruudukon sarakelukumäärän.
        *
        * @return Sarakkeiden lukumäärä.
        */
    public int getCols() {
        return this.game.getCols();
    }
    
    /**
        * Palauttaa graafisen ruudukon rivilukumäärän.
        *
        * @return Rivien lukumäärä.
        */
    public int getRows() {
        return this.game.getRows();
    }
    
    /**
        * Palauttaa graafisen ruudukon leveyden pikseleissä.
        *
        * @return Leveys.
        */
    public int getWidth() {
        return this.getCols() * this.cellSize + (this.getCols() + 1) * this.borderWidth;
    }
    
    /**
        * Palauttaa graafisen ruudukon korkeuden pikseleissä.
        *
        * @return Korkeus.
        */
    public int getHeight() {
        return this.getRows() * this.cellSize + (this.getRows() + 1) * this.borderWidth;
    }
    
    /**
        * Palauttaa graafisen ruudukon rajat.
        *
        * @return Vasemman yläkulman ja oikean alakulman pikselikoordinaatit.
        */
    public Bounds<Pixel> getBoundaries() {
        return new Bounds<Pixel>(new Pixel(0, 0), new Pixel(this.getWidth() - 1, this.getHeight() - 1));
    }
    
    /**
        * Palauttaa graafisen ruudukon toivotun koon pikseleissä. Ruudukolla on ainoastaan yksi järkevä koko, ja siksi getPreferredSize(), getMaximumSize() sekä getMinimumSize() palauttavat saman koon.
        *
        * return Toivottu koko.
        */
    public Dimension getPreferredSize() {
        return new Dimension(this.getWidth(), this.getHeight());
    }
    
    /**
        * Palauttaa graafisen ruudukon maksimikoon pikseleissä. Ruudukolla on ainoastaan yksi järkevä koko, ja siksi getPreferredSize(), getMaximumSize() sekä getMinimumSize() palauttavat saman koon.
        *
        * return Maksimikoko.
        */
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    
    /**
        * Palauttaa graafisen ruudukon minimikoon pikseleissä. Ruudukolla on ainoastaan yksi järkevä koko, ja siksi getPreferredSize(), getMaximumSize() sekä getMinimumSize() palauttavat saman koon.
        *
        * return Minimikoko.
        */
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    /**
        * Palauttaa graafisen ruudukon käyttämän soluautomaatin.
        *
        * @return Soluautomaatti.
        */
    public CA getGame() {
        return this.game;
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden soluautomaatin käytettäväksi ja pakottaa pääikkunan piirtymään uudelleen.
        *
        * @param game Uusi soluautomaatti.
        */
    public void setGame(CA game) {
        this.game = game;
        this.owner.repack();
        this.repaint();
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden soluautomaattitilan, säilyttäen CA-spesifiset ominaisuudet.
        *
        * @param state Uusi soluautomaattitila.
        */
    public void updateGameState(CAState state) {
        this.setGame(this.game.deriveNewCA(state));
        this.store();
    }
    
    /**
        * Palauttaa graafisen ruudukon yksittäisen solun koon.
        *
        * @return Solun koko.
        */
    public int getCellSize() {
        return this.cellSize;
    }
    
    /**
        * Muuttaa graafisen ruudukon solujen kokoa ja pakottaa pääikkunan piirtymään uudelleen. Jos annettu koko on minimikokoa pienempi, käytetään minimikokoa.
        *
        * @param cellSize Uusi solun koko.
        */
    public void setCellSize(int cellSize) {
        this.cellSize = Math.max(MIN_CELL_SIZE, cellSize);
        this.owner.repack();
    }
    
    /**
        * Palauttaa graafisen ruudukon reunuksien paksuuden.
        *
        * @return Reunuksien paksuuden.
        */
    public int getBorderWidth() {
        return this.borderWidth;
    }
    
    /**
        * Muuttaa graafisen ruudukon reunuksien paksuutta ja pakottaa pääikkunan piirtymään uudelleen. Jos annettu paksuus on minimipaksuutta pienempi, käytetään minimipaksuutta.
        *
        * @param borderWidth Uusi reunusten paksuus.
        */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = Math.max(MIN_BORDER_WIDTH, borderWidth);
        this.owner.repack();
    }
    
    
    /**
        * Palauttaa graafisen ruudukon animaation ruututaajuuden (evoluutioita per sekunti).
        *
        * @return Ruututaajuus.
        */
    public int getAnimationFPS() {
        return this.animationFPS;
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden animaation ruututaajuuden. Jos annettu taajuus on minimitaajuutta pienempi, käytetään minimitaajuutta.
        *
        * @param fps Uusi ruututaajuus.
        */
    public void setAnimationFPS(int fps) {
        this.animationFPS = Math.max(MIN_ANIMATION_FPS, fps);
        this.setTimerDelay();
        if(timer.isRunning()) {
            timer.restart();
        }
    }
    
    /**
        * Palauttaa graafisen ruudukon kuolleiden solujen värin.
        *
        * @return Kuolleiden solujen väri.
        */
    public Color getDeadColor() {
        return this.deadColor;
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden kuolleiden solujen värin.
        *
        * @param c Uusi väri.
        */
    public void setDeadColor(Color c) {
        this.deadColor = c;
        this.repaint();
    }
    
    /**
        * Palauttaa graafisen ruudukon elävien solujen värin.
        *
        * @return Elävien solujen väri.
        */
    public Color getAliveColor() {
        return this.aliveColor;
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden elävien solujen värin.
        *
        * @param c Uusi väri.
        */
    public void setAliveColor(Color c) {
        this.aliveColor = c;
        this.repaint();
    }
    
    /**
        * Palauttaa graafisen ruudukon reunusten värin.
        *
        * @return Reunusten väri.
        */
    public Color getBorderColor() {
        return this.borderColor;
    }
    
    /**
        * Asettaa graafiselle ruudukolle uuden reunusten värin.
        *
        * @param c Uusi väri.
        */
    public void setBorderColor(Color c) {
        this.borderColor = c;
        this.repaint();
    }
    
    /**
        * Piirtää graafisen ruudukon.
        *
        * @param g Grafiikkakonteksti, jossa piirto tapahtuu.
        */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.paintCells(g);
        this.paintBorders(g);
    }
    
    /**
        * Piirtää solujen väliset reunukset.
        *
        * @param g Grafiikkakonteksti, jossa piirto tapahtuu.
        */
    private void paintBorders(Graphics g) {   
        g.setColor(this.borderColor);
        Bounds<Pixel> bounds = this.getBoundaries();
        
        // Piirretään pystysuorat viivat
        int cols = this.getCols();
        int y1 = 0;
        int y2 = bounds.b.y;
        for(int col = 0; col <= cols; col++) {
            int x = col * this.cellSize + col * this.borderWidth;
            for(int i = 0; i < this.borderWidth; i++) {
                g.drawLine(x+i, y1, x+i, y2);
            }
        }
        
        // Piirretään vaakasuorat viivat
        int rows = this.getRows();
        int x1 = 0;
        int x2 = bounds.b.x;
        for(int row = 0; row <= rows; row++) {
            int y = row * cellSize + row * borderWidth;
            for(int i = 0; i < this.borderWidth; i++) {
                g.drawLine(x1, y+i, x2, y+i);
            }
        }
    }
    
    /**
        * Piirtää varsinaiset solut.
        *
        * @param g Grafiikkakonteksti, jossa piirto tapahtuu.
        */
    private void paintCells(Graphics g) {
        for(int col = 0; col < game.getCols(); col++) {
            for(int row = 0; row < game.getRows(); row++) {
                Color color = (game.getCell(col, row) ? this.aliveColor : this.deadColor);
                Pixel topLeft = this.getCellTopLeft(new Cell(col, row));
                g.setColor(color);
                g.fillRect(topLeft.x, topLeft.y, this.cellSize, this.cellSize);
            }
        }
    }
    
    /**
        * Suorittaa graafisen ruudukon sisällä tapahtuvan klikkauksen mukaisen operaation.
        *
        * @param e Hiiritapahtuma.
        * @param motion Kertoo, saiko kursorin liike aikaan tapahtuman.
        */
    private void handleMouseEvent(MouseEvent e, boolean motion) {
        Pixel pixel = this.getMouseEventCoord(e);
        Cell cell = this.getCellCoord(pixel);
        
        if(cell != null) {            
            if(motion) {
                if(!cell.equals(this.lastDrawn)) {
                    this.setCell(cell, paintLive);
                    this.lastDrawn = cell;
                }
            } else {
                this.paintLive = !game.getCell(cell.x, cell.y);
                this.setCell(cell, paintLive);
                this.lastDrawn = cell;
            }
        }
    }
    
    /**
        * Palauttaa hiiritapahtuman pikselikoordinaatit.
        *
        * @param e Hiiritapahtuma.
        *
        * @return Pikselikoordinaatit.
        */
    private Pixel getMouseEventCoord(MouseEvent e) {
        return new Pixel(e.getX(), e.getY());
    }
    
    /**
        * Palauttaa annettuja solukoordinaatteja vastaavan graafisen ruudun vasemman yläkulman pikselikoordinaatit.
        *
        * @param cell Solukoordinaatit.
        *
        * @return Pikselikoordinaatit.
        */
    private Pixel getCellTopLeft(Cell cell) {
        int x = (cell.x + 1) * this.borderWidth + cell.x * this.cellSize;
        int y = (cell.y + 1) * this.borderWidth + cell.y * this.cellSize;
        return new Pixel(x, y);
    }
    
    /**
        * Palauttaa annettuja pikselikoordinaatteja vastaavan solukoordinaatin.
        *
        * @param pixel Pikselikoordinaatit.
        *
        * @return Solukoordinaatit.
        */
    private Cell getCellCoord(Pixel pixel) {
        if(this.getBoundaries().contains(pixel)) {
            boolean xOnBorder = this.getBorderWidth() > 0 && pixel.x % (this.borderWidth + this.cellSize) == 0;
            boolean yOnBorder = this.getBorderWidth() > 0 && pixel.y % (this.borderWidth + this.cellSize) == 0;
            if(!xOnBorder && !yOnBorder) {
                int xCell = pixel.x / (this.borderWidth + this.cellSize);
                int yCell = pixel.y / (this.borderWidth + this.cellSize);
                return new Cell(xCell, yCell);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
        * Asettaa annetun solun tilan.
        *
        * @param cell Solukoordinaatit.
        * @param live Solun uusi tila.
        */
    private void setCell(Cell cell, boolean live) {
        this.game.setCell(cell.x, cell.y, live);
        Pixel topLeft = this.getCellTopLeft(cell);
        this.repaint(topLeft.x, topLeft.y, this.cellSize, this.cellSize);
    }
    
    /**
        * Suorittaa yhden evoluutioaskeleen ja piirtää graafisen ruudukon uudelleen. Jos evoluution jälkeen havaitaan soluautomaatissa toistuva rytmi, mahdollisesti käynnissä oleva animaatio pysäytetään.
        */
    public void evolve() {
        this.game.evolve();
        this.repaint();
        if(this.game.isPeriodic()) {
            this.owner.stop();
        }
    }
    
    /**
        * Säilöö automaatin nykyisen tilan tilapäismuistiin.
        */
    public void store() {
        game.storePattern();
    }
    
    /**
        * Alustaa automaatin tilan tilapäismuistissa säilötyn tilan mukaiseksi ja piirtää graafisen ruudukon uudelleen.
        */
    public void recall() {
        game.recallPattern();
        this.repaint();
    }
    
    /**
        * Alustaa kaikki solut kuolleiksi ja piirtää graafisen ruudukon uudelleen.
        */
    public void erase() {
        game.erase();
        this.repaint();
    }
    
    /**
        * Muuttaa soluautomaatin ja graafisen ruudukon kokoa. Koon muutoksen jälkeinen tila säilötään tilapäismuistiin, jotta sinne ei jää "kummittelemaan" väärän kokoista tilaa.
        *
        * @param newCols Uusi sarakelukumäärä.
        * @param newRows Uusi rivilukumäärä.
        */
    public void resizeGrid(int newCols, int newRows) {
        this.game.setCells(Grid.resize(this.game.copyCells(), newCols, newRows));
        this.store();
        this.owner.repack();
    }
    
    /**
        * Alustaa evoluutioanimaation ajastimen.
        */
    private void initTimer() {
        ActionListener evolver = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evolve();
            }
        };
        this.timer = new Timer(0, evolver);
        this.timer.setInitialDelay(0);
    }

    /**
        * Käynnistää evoluutioanimaation.
        */
    public void startAnimation() {
        this.game.resetHistory();
        this.timer.start();
    }
    
    /**
        * Pysäyttää evoluutioanimaation.
        */
    public void stopAnimation() {
        this.timer.stop();
    }
    
    /**
        * Kertoo, onko evoluutioanimaatio käynnissä.
        *
        * @return Evoluutioanimaation käynnissäolosta kertova totuusarvo.
        */
    public boolean animationIsRunning() {
        return this.timer.isRunning();
    }
    
    /**
        * Asettaa evoluutioanimaation ajastimen intervallin animationFPS-muuttujan perusteella.
        */
    private void setTimerDelay() {
        this.timer.setDelay(fpsToDuration(this.animationFPS));
    }
    
    /**
        * Kääntää ruututaajuusarvon yhden ruudun kestoon millisekunneissa.
        *
        * @param fps Ruututaajuus.
        *
        * @return Ruudun kesto.
        */
    private static int fpsToDuration(int fps) {
        return Math.round(1 / ((float) fps) * 1000);
    }
}