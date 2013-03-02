package utils;

/**
    * Yleinen abstrakti esitysmuoto 2D-koordinaatille.
    */
abstract public class Coord {
    /**
        * X- ja y-koordinaatit.
        */
    public final int x, y;
    
    /**
        * @param x X-koordinaatti.
        * @param y Y-koordinaatti.
        */
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
        * Luo yksinkertaisen merkkijonoesityksen koordinaatille.
        */
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}