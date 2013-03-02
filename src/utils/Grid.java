package utils;

/**
    * boolean[][]-tyyppisiä soluruudukoita käsitteleviä metodeja sisältävä työkaluluokka.
    */
abstract public class Grid {
    /**
        * Palauttaa ruudukon sarakkeiden lukumäärän.
        * 
        * @param cells Soluruudukko.
        *
        * @return Sarakkeiden lukumäärä.
        */
    public static int getCols(boolean[][] cells) {
        return cells.length;
    }
    
    /**
        * Palauttaa ruudukon rivien lukumäärän.
        * 
        * @param cells Soluruudukko.
        *
        * @return Rivien lukumäärä.
        */
    public static int getRows(boolean[][] cells) {
        return (cells.length > 0 ? cells[0].length : 0);
    }
    
    /**
        * Palauttaa, ovatko kaksi soluruudukkoa samanlaiset.
        * 
        * @param a Ensimmäinen soluruudukko.
        * @param b Toinen soluruudukko.
        *
        * @return Ruudukkojen samanlaisuudesta kertova totuusarvo.
        */
    public static boolean equal(boolean[][] a, boolean[][] b) {
        if(a != null && b != null) {
            if(a.length == b.length) {
                for(int i = 0; i < a.length; i++) {
                    if(a[i].length == b[i].length) {
                        for(int j = 0; j < a[i].length; j++) {
                            if(a[i][j] != b[i][j]) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }            
        } else if(a == null && b == null) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
        * Palauttaa haluttuun kokoon suurennetun/pienennetyn kopion annetusta soluruudukosta, säilyttäen sen sisällöstä niin paljon kuin mahdollista.
        *
        * @param oldCells Alkuperäinen soluruudukko.
        * @param newCols Uuden soluruudukon sarakkeiden lukumäärä.
        * @param newRows Uuden soluruudukon rivien lukumäärä.
        *
        * @return Uusi soluruudukko.
        */
    public static boolean[][] resize(boolean[][] oldCells, int newCols, int newRows) {
        int oldCols = getCols(oldCells);
        int oldRows = getRows(oldCells);
        
        boolean[][] newCells = new boolean[newCols][newRows];
        for(int col = 0; col < newCols; col++) {
            int oldCol = mapResizedGridCoord(col, oldCols, newCols);
            for(int row = 0; row < newRows; row++) {
                boolean live = false;
                if(oldCol >= 0) {
                    int oldRow = mapResizedGridCoord(row, oldRows, newRows);
                    if(oldRow >= 0) {
                        live = oldCells[oldCol][oldRow];
                    }
                }
                newCells[col][row] = live;
            }
        }
        return newCells;
    }
    
    /**
        * Kuvaa koordinaatin suurennetusta/pienennetystä koordinaatistosta alkuperäisen kokoiseen koordinaatistoon, mikäli mahdollista. Muutoin palautetaan -1.
        *
        * Tämä on resize():n käyttämä apumetodi.
        *
        * @param newCoord Koordinaatti uudessa koordinaatistossa.
        * @param oldSize Vanhan koordinaatiston koko.
        * @param newSize Uuden koordinaatiston koko.
        * 
        * @return Koordinaatti vanhassa koordinaatistossa tai -1 mikäli kuvaus ei onnistunut.
        */
    private static int mapResizedGridCoord(int newCoord, int oldSize, int newSize) {
        int diff = newSize - oldSize;
        int leftDead = diff / 2;
        int rightDead = diff - leftDead;
        
        if(newCoord >= leftDead && newCoord < newSize - rightDead) {
            return newCoord - leftDead;
        } else {
            return -1;
        }            
    }
    
    /**
        * Luo annetun koon kokoisen soluruudukon, jonka kaikki solut on alustettu kuolleiksi.
        * 
        * @param cols Luotavan ruudukon sarakemäärä.
        * @param rows Luotavan ruudukon rivimäärä.
        *
        * @return Kuolleeksi alustettu soluruudukko.
        */
    public static boolean[][] getDeadGrid(int cols, int rows) {
        boolean[][] deadCells = new boolean[cols][rows];
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                deadCells[col][row] = false;
            }
        }
        return deadCells;
    }
    
    /**
        * Luo kopion annetusta soluruudukosta.
        *
        * @param source Alkuperäinen soluruudukko.
        *
        * @return Kopio soluruudukosta.
        */
    public static boolean[][] copy(boolean[][] source) {
        int cols = getCols(source);
        int rows = getRows(source);
        boolean[][] copiedCells = new boolean[cols][rows];
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                copiedCells[col][row] = source[col][row];
            }
        }
        return copiedCells;
    }
}