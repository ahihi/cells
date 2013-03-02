package cellularautomaton;

import java.io.*;

import utils.*;

/**
    * Soluautomaatin ydintoiminnallisuus.
    */
public class CA extends CAState {
    /**
        * Minimiarvo tilahistorian pituudelle.
        */
    private static final int MIN_HISTORY_LENGTH = 0;
    
    /**
        * Oletusarvo historian pituudelle.
        */
    private static final int DEFAULT_HISTORY_LENGTH = 2;
    
    /**
        * Soluautomaatin väliaikaismuistiin säilötty tila.
        */
    private boolean[][] storedCells;
    
    /**
        * Soluautomaatin seuraava tila.
        */
    private boolean[][] newCells;
    
    /**
        * Tilahistoria.
        */
    private boolean[][][] history;
    
    /**
        * Luo oletuskokoisen, oletussäännöillä ja oletuspituisella historialla varustetun soluautomaattitilan.
        */
    public CA() {
        super();
        this.storePattern();
        this.setHistoryLength(DEFAULT_HISTORY_LENGTH);
    }
    
    /**
        * Asettaa annetun solun arvon seuraavassa tilassa.
        *
        * @param col Solun sarakekoordinaatti.
        * @param row Solun rivikoordinaatti.
        * @param live Solun uusi tila.
        */
    private void setNewCell(int col, int row, boolean live) {
        this.newCells[col][row] = live;
    }
    
    /**
        * Korvaa automaatin soluruudukon annetulla taulukolla. Mikäli annettu taulukko on liian pieni, se suurennetaan minimiulottuvuuksien kokoiseksi.
        * 
        * @param cells Uusi soluruudukko.
        */
    public void setCells(boolean[][] cells) {
        super.setCells(cells);
        this.newCells = Grid.getDeadGrid(this.getCols(), this.getRows());
    }
    
    /**
        * Säilöö automaatin nykyisen tilan tilapäismuistiin.
        */
    public void storePattern() {
        this.storedCells = this.copyCells();
    }
    
    /**
        * Alustaa automaatin tilan tilapäismuistissa säilötyn tilan mukaiseksi.
        */
    public void recallPattern() {
        this.cells = Grid.copy(this.storedCells);
    }
    
    /**
        * Palauttaa automaatin historian pituuden.
        */
    public int getHistoryLength() {
        return this.history.length;
    }
    
    /**
        * Asettaa automaatin historian pituuden ja tyhjentää historiataulukon. Mikäli annettu pituus on pienempi kuin historian minimipituus, käytetään minimipituutta.
        *
        * @param len Historian uusi pituus.
        */
    public void setHistoryLength(int len) {
        len = Math.max(MIN_HISTORY_LENGTH, len);
        this.history = new boolean[len][this.getCols()][this.getRows()];
    }
    
    /**
        * Tyhjentää historiataulukon.
        */
    public void resetHistory() {
        this.setHistoryLength(this.history.length);
    }
    
    /**
        * Suorittaa yhden sääntöjen mukaisen tilasiirtymän ja säilöö vanhan tilan historiaan.
        */
    public void evolve() {
        int cols = this.getCols();
        int rows = this.getRows();
        
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                int aliveNeighbors = this.countAliveNeighbors(col, row);
                boolean live;
                if(this.getCell(col, row)) {
                    live = this.rules.getSurvivalRule(aliveNeighbors);
                } else {
                    live = this.rules.getBirthRule(aliveNeighbors);
                }
                this.setNewCell(col, row, live);
            }
        }
        this.updateHistory();
        this.swapArrays();
    }
    
    /**
        * Laskee solun elossa olevien naapureiden lukumäärän. Reunoilla sijaitseville soluille tämä määrittyy automaatin reunakäyttäytymisen mukaan.
        *
        * @param centerCol Solun sarakekoordinaatti.
        * @param centerRow Solun rivikoordinaatti.
        *
        * @return Elävien naapurisolujen lukumäärä.
        */
    private int countAliveNeighbors(int centerCol, int centerRow) {
        int aliveCount = 0;
        for(int col = centerCol - 1; col <= centerCol + 1; col++) {
            for(int row = centerRow - 1; row <= centerRow + 1; row++) {
                boolean atCenter = col == centerCol && row == centerRow;
                if(!atCenter && this.getCell(col, row)) {
                    aliveCount++;
                }
            }
        }
        return aliveCount;
    }
    
    /**
        * Säilöö automaatin nykyisen tilan historiaan. Mikäli historia on täynnä, vanhin säilötty tila poistetaan.
        */
    private void updateHistory() {
        boolean[][] prev = this.copyCells();
        for(int i = 0; i < this.history.length; i++) {
            boolean[][] cur = this.history[i];
            this.history[i] = prev;
            prev = cur;
        }
    }
    
    /**
        * Kertoo, onko automaatissa havaittavissa toistuva rytmi. Jos automaatin nykyinen tila löytyy historasta, on rytmi olemassa.
        *
        * @return Totuusarvo, joka kertoo havaittiinko rytmi.
        */
    public boolean isPeriodic() {
        for(boolean[][] state : this.history) {
            if(Grid.equal(state, this.cells)) {
                return true;
            }
        }
        return false;
    }
    
    /**
        * Vaihtaa automaatin nykyisen ja seuraavan tilan paikkoja keskenään. Tulos on, että entinen seuraava tila siirtyy nykyiseksi tilaksi ja entistä nykyistä tilaa voidaan muokata kun halutaan määrittää uusi seuraava tila.
        */
    private void swapArrays() {
        boolean[][] tempNew = this.newCells;
        this.newCells = this.cells;
        this.cells = tempNew;
    }
    
    /**
        * Luo uuden soluautomaatin, jonka CA-spesifiset ominaisuudet (historian pituus) kopioidaan tältä soluautomaatilta, mutta tila ja säännöt kopioidaan annetulta CAState-oliolta.
        *
        * @param state Soluautomaattitila.
        *
        * @return Uusi soluautomaatti.
        */
    public CA deriveNewCA(CAState state) {
        CA newGame = new CA();
        newGame.setRules(state.getRules());
        newGame.setCells(state.cells);
        newGame.setHistoryLength(this.getHistoryLength());
        return newGame;
    }
}