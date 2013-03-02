package utils;

/**
    * Kahden koordinaatin (vasen yläkulma, oikea alakulma) muodostaman suorakulmion rajoja kuvaava olio.
    */
public class Bounds<C extends Coord> {
    /**
        * Rajakoordinaatit.
        */
    public final C a, b;
    
    /**
        * @param a Vasemman yläkulman koordinaatti.
        * @param b Oikean alakulman koordinaatti.
        */
    public Bounds(C a, C b) {
        this.a = a;
        this.b = b;
    }
    
    /**
        * Kertoo, sijaitseeko annettu koordinaatti rajojen merkitsemän suorakulmion sisällä.
        *
        * @param c Koordinaatti.
        * 
        * @return True jos koordinaatti on rajojen sisällä, false muutoin.
        */
    public boolean contains(C c) {
        return this.a.x <= c.x && c.x <= this.b.x && this.a.y <= c.y && c.y <= this.b.y;
    }
}