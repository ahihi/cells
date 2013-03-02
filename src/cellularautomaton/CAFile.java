package cellularautomaton;

import java.util.regex.*;

import utils.*;

/**
    * Soluruudukkotiloja merkkijonoiksi muuntava ja niistä lukeva työkaluluokka.
    * 
    * Määrittelee spesifisesti tätä ohjelmaa varten luodun tekstitiedostomuodon, joka koostuu automaatin säännöt määrittelevästä otsakkeesta sekä elävät solut listaavasta sisältöosasta.
    *
    * Syntaksi (EBNF):
    *
    * <pre>
    * data = otsake , "|" , sisältö ;
    *
    * otsake = koko , "/" , reunakäyttäytyminen , "/" , syntymäsäännöt , "/" , eloonjäämissäännöt ;
    * koko = luku , "," , luku ;
    * reunakäyttäytyminen = "dead" | "alive" | "wrap" ;
    * syntymäsäännöt = luku , { "," , luku } ;
    * eloonjäämissäännöt = luku , { "," , luku } ;
    *
    * sisältö = eläväsolu , { "/" , eläväsolu } ;
    * eläväsolu = luku , "," , luku ;
    *
    * luku = numero , { numero } ;
    * numero = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
    * </pre>
    */
abstract public class CAFile {
    /**
        * Dead-reunakäyttäytymistä ilmaiseva merkkijono.
        */
    private static final String EBSTR_DEAD    = "dead";
    
    /**
        * Alive-reunakäyttäytymistä ilmaiseva merkkijono.
        */
    private static final String EBSTR_ALIVE   = "alive";
    
    /**
        * Wrap-reunakäyttäytymistä ilmaiseva merkkijono.
        */
    private static final String EBSTR_WRAP    = "wrap";
    
    /**
        * Kappaleet (otsake ja sisältö) toisistaan erottava merkki.
        */
    private static final char SEP_SECTION     = '|';
    
    /**
        * Alakappaleet (otsakkeessa koko, reunakäyttäytyminen, syntymäsäännöt ja eloonjäämissäännöt; sisällössä elävät solut) toisistaan erottava merkki.
        */
    private static final char SEP_SUBSECTION  = '/';
    
    /**
        * Atomiset arvot (luku, reunakäyttäytyminen) toisistaan erottava merkki.
        */
    private static final char SEP_ITEM        = ',';
    
    /**
        * Luo CAState-olion annetusta merkkijonoesityksestä.
        *
        * @param str Soluautomaattitilan merkkijonoesitys.
        *
        * @return CAState-olio jos merkkijonoesitys on validi, muutoin null.
        */
    public static CAState read(String str) {
        try {
            String[] sections = str.split(charToRegex(SEP_SECTION));
            guard(sections.length == 2);
            
            String header = sections[0];
            CAState state = readHeader(header);
            guard(state != null);
            
            String body = sections[1];
            state = readBody(body, state);
            guard(state != null);
            
            return state;
        } catch(Exception e) {
            return null;
        }
    }
    
    /**
        * Tiedosto-otsakkeen jäsentävä apumetodi.
        *
        * @param header Otsake.
        *
        * @return CAState-olio, jonka säännöt on asetettu otsakkeen mukaan, mikäli se on validi. Muutoin null.
        */
    private static CAState readHeader(String header) {
        try {
            String[] headerSubsections = header.split(charToRegex(SEP_SUBSECTION));
            guard(headerSubsections.length == 4);

            String dimsSub = headerSubsections[0];
            String[] dimsSubItems = dimsSub.split(charToRegex(SEP_ITEM));
            guard(dimsSubItems.length == 2);
            int cols = Integer.parseInt(dimsSubItems[0]);
            guard(cols >= CAState.MIN_DIMENSION);
            int rows = Integer.parseInt(dimsSubItems[1]);
            guard(rows >= CAState.MIN_DIMENSION);

            String edgeBehaviorSub = headerSubsections[1];
            CARules.EdgeBehavior eb = stringToEB(edgeBehaviorSub);
            guard(eb != null);

            CARules rules = new CARules();
            rules.setEdgeBehavior(eb);

            String birthRulesSub = headerSubsections[2];
            String[] birthRulesSubItems = birthRulesSub.split(charToRegex(SEP_ITEM));
            for(String item : birthRulesSubItems) {
                int neighbors = Integer.parseInt(item);
                guard(0 <= neighbors && neighbors < CARules.NEIGHBOR_COUNT);
                rules.setBirthRule(neighbors, true);
            }

            String survivalRulesSub = headerSubsections[3];
            String[] survivalRulesSubItems = survivalRulesSub.split(charToRegex(SEP_ITEM));
            for(String item : survivalRulesSubItems) {
                int neighbors = Integer.parseInt(item);
                guard(0 <= neighbors && neighbors < CARules.NEIGHBOR_COUNT);
                rules.setSurvivalRule(neighbors, true);
            }
            
            CAState state = new CAState();
            state.setRules(rules);
            state.setCells(Grid.getDeadGrid(cols, rows));
            
            return state;
        } catch(Exception e) {
            return null;
        }
    }
    
    /**
        * Sisältöosan jäsentävä apumetodi.
        *
        * @param body Sisältöosa.
        * @param state Soluautomaattitila.
        *
        * @return CAState-olio, jonka säännöt on kopioitu annetusta soluautomaattitilasta ja solut on asetettu jäsennetyn sisältömerkkijonon mukaisesti, mikäli se on validi. Muutoin null.
        */
    private static CAState readBody(String body, CAState state) {
        try {
            String[] bodySubsections = body.split(charToRegex(SEP_SUBSECTION));
            for(String cell : bodySubsections) {
                String[] items = cell.split(charToRegex(SEP_ITEM));
                guard(items.length == 2);
                
                int col = Integer.parseInt(items[0]);
                guard(0 <= col && col < state.getCols());
                int row = Integer.parseInt(items[1]);
                guard(0 <= row && row < state.getRows());
                
                state.setCell(col, row, true);
            }
            
            return state;
        } catch(Exception e) {
            return null;
        }
    }
    
    /**
        * Ohjausapumetodi, joka heittää poikkeuksen jos annettu predikaatti on epätosi.
        *
        * @param predicate Predikaatti.
        *
        * @exception Exception Ilmaisee, että predikaatti oli epätosi.
        */
    private static void guard(boolean predicate) throws Exception {
        if(!predicate) {
            throw new Exception();
        }
    }
    
    /**
        * Luo merkkijonoesityksen soluautomaattitilasta.
        *
        * @param game Soluautomaattitila.
        *
        * @return Soluautomaattitilaa esittävä merkkijono.
        */
    public static String write(CAState game) {
        String result = "";
        result += writeHeader(game.getCols(), game.getRows(), game.getRules());
        result += SEP_SECTION;
        result += writeBody(game.copyCells());
        return result;
    }
    
    /**
        * Luo tiedosto-otsakkeen.
        *
        * @param cols Soluautomaatin sarakkeiden lukumäärä.
        * @param rows Soluautomaatin rivien lukumäärä.
        * @param rules Soluautomaatin säännöt.
        *
        * @return Tiedosto-otsake.
        */
    private static String writeHeader(int cols, int rows, CARules rules) {
        String header = "";
        header += "" + cols + SEP_ITEM + rows;
        header += SEP_SUBSECTION;
        header += ebToString(rules.getEdgeBehavior());
        header += SEP_SUBSECTION;
        header += lifeRulesToString(rules);
        return header;
    }
    
    /**
        * Luo merkkijonoesityksen syntymä- ja eloonjäämissäännöistä.
        *
        * @param rules Soluautomaatin säännöt.
        * 
        * @return Syntymä- ja eloonjäämissääntöjen merkkijonoesitys.
        */
    private static String lifeRulesToString(CARules rules) {
        String birth = "";
        String survival = "";
        for(int i = 0; i < rules.NEIGHBOR_COUNT; i++) {
           if(rules.getBirthRule(i)) {
               birth = append(birth, SEP_ITEM, "" + i);
           }
           if(rules.getSurvivalRule(i)) {
               survival = append(survival, SEP_ITEM, "" + i);
           }
        }
        return birth + SEP_SUBSECTION + survival;
    }
        
    /**
        * Kääntää reunakäyttäytymisen merkkijonoksi.
        *
        * @param eb Reunakäyttäytyminen.
        *
        * @return Merkkijonoesitys.
        */
    private static String ebToString(CARules.EdgeBehavior eb) {
        if(eb == CARules.EdgeBehavior.DEAD) {
            return EBSTR_DEAD;
        } else if(eb == CARules.EdgeBehavior.ALIVE) {
            return EBSTR_ALIVE;
        } else if(eb == CARules.EdgeBehavior.WRAP) {
            return EBSTR_WRAP;
        } else {
            return null;
        }
    }
    
    /**
        * Kääntää merkkijonon reunakäyttäytymiseksi.
        *
        * @param str Merkkijonoesitys.
        *
        * @return Reunakäyttäytyminen.
        */
    private static CARules.EdgeBehavior stringToEB(String str) {
        if(str.equals(EBSTR_DEAD)) {
            return CARules.EdgeBehavior.DEAD;
        } else if(str.equals(EBSTR_ALIVE)) {
            return CARules.EdgeBehavior.ALIVE;
        } else if(str.equals(EBSTR_WRAP)) {
            return CARules.EdgeBehavior.WRAP;
        } else {
            return null;
        }
    }
    
    /**
        * Luo merkkijonoesityksen soluruudukosta.
        *
        * @param cells Soluruudukko.
        *
        * @return Soluruudukon merkkijonoesitys.
        */
    private static String writeBody(boolean[][] cells) {
        int cols = Grid.getCols(cells);
        int rows = Grid.getRows(cells);
        String body = "";
        
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                if(cells[col][row]) {
                    body = append(body, SEP_SUBSECTION, cellToString(col, row));
                }
            }
        }
        return body;
    }
    
    /**
        * Luo merkkijonoesityksen yksittäisestä elävästä solusta.
        *
        * @param col Solun sarakekoordinaatti.
        * @param row Solun rivikoordinaatti.
        *
        * @return Solun merkkijonoesitys.
        */
    private static String cellToString(int col, int row) {
        return "" + col + SEP_ITEM + row;
    }
    
    /**
        * Apumetodi, jolla yhdistetään kaksi merkkijonoa ja asetetaan niiden väliin erotusmerkki.
        *
        * @param str Alkuosa.
        * @param sep Erotusmerkki.
        * @param item Loppuosa.
        * 
        * @return Yhdistetty merkkijono.
        */
    private static String append(String str, char sep, String item) {
        if(!str.equals("")) {
            str += sep;
        }
        str += item;
        return str;
    }
    
    /**
        * Apumetodi, joka luo merkkiliteraalia esittävän regex-arvon merkkijonojen split()-metodia varten.
        *
        * @param c Merkki.
        *
        * @return Regex.
        */
    private static String charToRegex(char c) {
        return Pattern.quote("" + c);
    }
}