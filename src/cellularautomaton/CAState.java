package cellularautomaton;

import java.io.*;

import utils.*;

/**
    * Soluautomaatin tila. Sisältää simppeleitä tilanmuokkausoperaatioita.
    */
public class CAState {
    
    /**
        * Minimiarvo soluautomaatin sarake- ja rivimäärälle. Määritellään minimiarvoksi 3, koska pienemmillä automaateilla solujen naapurimäärän laskenta joutuu lukemaan samoja soluja useaan kertaan.
        */
    public final static int MIN_DIMENSION = 3;
    
    /**
        * Oletusarvo soluautomaatin sarakemäärälle.
        */
    private final static int DEFAULT_COLS   = 32;
    
    /**
        * Oletusarvo soluautomaatin rivimäärälle.
        */
    private final static int DEFAULT_ROWS   = 32;
    
    /**
        * Soluruudukkoa esittävä kaksiulotteinen taulukko. Kaksiarvoisen boolean-tyypin käyttäminen rajoittaa solujen mahdolliset tilat kahteen.
        */
    protected boolean[][] cells;
    
    /**
        * Automaatin säännöt.
        */
    protected CARules rules;
    
    /**
        * Luo oletuskokoisen, oletussäännöillä varustetun soluautomaattitilan.
        */
    public CAState() {
        this.rules = CARules.newLifeRules();
        this.setCells(Grid.getDeadGrid(DEFAULT_COLS, DEFAULT_ROWS));
    }

    /**
        * Palauttaa sarakkeiden lukumäärän.
        *
        * @return Sarakkeiden lukumäärä.
        */
    public int getCols() {
        return Grid.getCols(this.cells);
    }
    
    /**
        * Palauttaa rivien lukumäärän.
        *
        * @return Rivien lukumäärä.
        */
    public int getRows() {
        return Grid.getRows(this.cells);
    }
    
    /**
        * Palauttaa soluruudukon rajat.
        *
        * @return Vasemman yläkulman ja oikean alakulman solujen koordinaatit.
        */
    protected Bounds<Cell> getBoundaries() {
        return new Bounds<Cell>(new Cell(0, 0), new Cell(this.getCols() - 1, this.getRows() - 1));
    }
    
    /**
        * Kertoo, onko annetussa sijainnissa oleva solu elossa. Mikäli solu sijaitsee ruudukon ulkopuolella, palautettava arvo määräytyy käytettävien sääntöjen EdgeBehavior-arvon mukaan.
        *
        * @param col Solun sarakekoordinaatti.
        * @param row Solun rivikoordinaatti.
        * @return Solun tila.
        */
    public boolean getCell(int col, int row) {
        boolean cell;
        if(this.cellExists(col, row)) {
            cell = this.cells[col][row];
        } else {
            CARules.EdgeBehavior eb = rules.getEdgeBehavior();
            if(eb == CARules.EdgeBehavior.DEAD) {
                cell = false;
            } else if(eb == CARules.EdgeBehavior.ALIVE) {
                cell = true;
            } else { // wrap
                col = Maths.mod(col, this.getCols());
                row = Maths.mod(row, this.getRows());
                cell = this.cells[col][row];
            }
        }
        return cell;
    }
    
    /**
        * Asettaa annetussa sijainnissa olevan solun tilan. Palauttaa true, jos solu on olemassa, muutoin false.
        *
        * @param col Solun sarakekoordinaatti.
        * @param row Solun rivikoordinaatti.
        * @param live Solun uusi tila.
        *
        * @return Kertoo, onnistuiko solun tilan asettaminen.
        */
    public boolean setCell(int col, int row, boolean live) {
        if(this.cellExists(col, row)) {
            this.cells[col][row] = live;
            return true;
        } else {
            return false;
        }
    }
    
    /**
        * Korvaa automaatin solutaulukon annetulla taulukolla. Mikäli annettu taulukko on liian pieni, se suurennetaan minimiulottuvuuksien kokoiseksi.
        * 
        * @param cells Uusi solutaulukko.
        */
    public void setCells(boolean[][] cells) {
        int cols = Grid.getCols(cells);
        int rows = Grid.getRows(cells);
        
        if(cols < MIN_DIMENSION || rows < MIN_DIMENSION) {
            cells = Grid.resize(cells, Math.max(MIN_DIMENSION, cols), Math.max(MIN_DIMENSION, rows));
        }
        
        this.cells = Grid.copy(cells);
    }
        
    /**
        * Alustaa kaikki solut kuolleiksi.
        */
    public void erase() {
        this.setCells(Grid.getDeadGrid(this.getCols(), this.getRows()));
    }
    
    /**
        * Palauttaa soluautomaatin käyttämät säännöt. Palautettu sääntöolio on kopio automaatin säännöistä, ei suora viittaus.
        *
        * @return Soluautomaatin käyttämät säännöt.
        */
    public CARules getRules() {
        return this.rules.clone();
    }
    
    /**
        * Asettaa soluautomaatille uudet säännöt. Automaatin säännöiksi asetetaan kopio annetusta sääntöoliosta. 
        *
        * @param rules Uudet säännöt.
        */
    public void setRules(CARules rules) {
        this.rules = rules.clone();
    }
    
    /**
        * Kertoo, onko annetussa sijainnissa solua, eli ovatko koordinaatit rajojen sisällä.
        *
        * @return Solun olemassaolosta kertova totuusarvo.
        */
    private boolean cellExists(int col, int row) {
        return this.getBoundaries().contains(new Cell(col, row));
    }
    
    /**
        * Luo kopion automaatin käyttämästä solutaulukosta.
        *
        * @return Solutaulukon kopio.
        */
    public boolean[][] copyCells() {
        return Grid.copy(this.cells);
    }
}