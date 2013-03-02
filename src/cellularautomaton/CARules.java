package cellularautomaton;

import java.io.*;

/**
    * Soluautomaatin säännöt.
    */
public class CARules implements Cloneable {
    /**
        * Reunakäyttäytyminen, eli mahdolliset käyttäytymistavat soluille, jotka sijaitsevat ruudukon ulkopuolella.
        */
    public enum EdgeBehavior {
        DEAD,
        ALIVE,
        WRAP
    }
    
    /**
        * Solun naapurien lukumäärä, solu itse mukaanlukien.
        */
    public static final int NEIGHBOR_COUNT = 9;
    
    /**
        * Automaatin reunakäyttäytyminen.
        */
    private EdgeBehavior edgeBehavior;
    
    /**
        * Automaatin syntymäsäännöt. birthRules[n]:n totuusarvo kertoo, muuttuuko kuollut solu eläväksi kun sillä on tasan n naapuria.
        */
    private boolean[] birthRules;
    
    /**
        * Automaatin eloonjäämissäännöt. survivalRules[n]:n totuusarvo kertoo, muuttuuko pysyykö elävä solu elävänä kun sillä on tasan n naapuria.
        */
    private boolean[] survivalRules;
    
    /**
        * Luo uudet säännöt.
        */
    public CARules() {
        this.setEdgeBehavior(EdgeBehavior.DEAD);
        this.birthRules = new boolean[NEIGHBOR_COUNT];
        this.survivalRules = new boolean[NEIGHBOR_COUNT];
        
        for(int i = 0; i < NEIGHBOR_COUNT; i++) {
            this.birthRules[i] = false;
            this.survivalRules[i] = false;
        }
    }
    
    /**
        * Luo uudet Game of Life -säännöt. Syntymäsäännöt ovat {3} ja eloonjäämissäännöt {2, 3}.
        */
    public static CARules newLifeRules() {
        CARules rules = new CARules();
        rules.setSurvivalRule(2, true);
        rules.setSurvivalRule(3, true);
        rules.setBirthRule(3, true);
        return rules;
    }
    
    /**
        * Asettaa uuden reunakäyttäytymisen.
        * 
        * @param eb Uusi reunakäyttäytyminen.
        */
    public void setEdgeBehavior(EdgeBehavior eb) {
        this.edgeBehavior = eb;
    }
    
    /**
        * Palauttaa nykyisen reunakäyttäytymisen.
        *
        * @return Nykyinen reunakäyttäytyminen.
        */
    public EdgeBehavior getEdgeBehavior() {
        return this.edgeBehavior;
    }
    
    /**
        * Palauttaa syntymäsäännön jollekin naapurilukumäärälle. Metodi olettaa naapurilukumäärän olevan välillä [0, NEIGHBOR_COUNT-1].
        *
        * @param neighborCount Naapurien lukumäärä.
        *
        * @return Syntymäsäännön totuusarvo.
        */
    public boolean getBirthRule(int neighborCount) {
        return this.birthRules[neighborCount];
    }
    
    /**
        * Palauttaa eloonjäämissäännön jollekin naapurilukumäärälle. Metodi olettaa naapurilukumäärän olevan välillä [0, NEIGHBOR_COUNT-1].
        *
        * @param neighborCount Naapurien lukumäärä.
        *
        * @return Eloonjäämissäännön totuusarvo.
        */
    public boolean getSurvivalRule(int neighborCount) {
        return this.survivalRules[neighborCount];
    }
    
    /**
        * Asettaa syntymäsäännön jollekin naapurilukumäärälle. Metodi olettaa naapurilukumäärän olevan välillä [0, NEIGHBOR_COUNT-1].
        *
        * @param neighborCount Naapurien lukumäärä.
        * @param live Syntymäsäännön totuusarvo.
        */
    public void setBirthRule(int neighborCount, boolean live) {
        this.birthRules[neighborCount] = live;
    }
    
    /**
        * Asettaa eloonjäämissäännön jollekin naapurilukumäärälle. Metodi olettaa naapurilukumäärän olevan välillä [0, NEIGHBOR_COUNT-1].
        *
        * @param neighborCount Naapurien lukumäärä.
        * @param live Eloonjäämissäännön totuusarvo.
        */
    public void setSurvivalRule(int neighborCount, boolean live) {
        this.survivalRules[neighborCount] = live;
    }
    
    /**
        * Luo sääntöoliosta kopion.
        *
        * @return Kopio sääntöoliosta.
        */
    public CARules clone() {
        CARules rules = new CARules();
        rules.setEdgeBehavior(this.getEdgeBehavior());
        for(int i = 0; i < NEIGHBOR_COUNT; i++) {
            rules.birthRules[i] = this.birthRules[i];
            rules.survivalRules[i] = this.survivalRules[i];
        }
        return rules;
    }
}