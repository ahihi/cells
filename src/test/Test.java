package test;

import cellularautomaton.*;
import utils.*;

public class Test {
    private static int tests = 0;
    private static int passed = 0;
    
    public static void main(String[] args) {
        CARules rules;
        CA game;
        
        System.out.println("Luodaan säännöt.");
        rules = new CARules();
        
        System.out.println("Asetetaan Game of Life -säännöt (syntymä: 3, eloonjääminen 2, 3)");
        rules.setBirthRule(3, true);
        rules.setSurvivalRule(2, true);
        rules.setSurvivalRule(3, true);
        
        System.out.println("Asetetaan reunakäyttäytymiseksi DEAD.");
        rules.setEdgeBehavior(CARules.EdgeBehavior.DEAD);
        
        boolean[] expectedBirthRulesArray = {false, false, false, true, false, false, false, false, false};
        System.out.println("Odotetut syntymäsäännöt:\t" + showArray(expectedBirthRulesArray));
        boolean[] birthRulesArray = new boolean[rules.NEIGHBOR_COUNT];
        for(int i = 0; i < birthRulesArray.length; i++) {
            birthRulesArray[i] = rules.getBirthRule(i);
        }
        System.out.println("Saadut syntymäsäännöt:\t\t" + showArray(birthRulesArray));
        testResult("Syntymäsääntöjen alustaminen", rulesEq(birthRulesArray, expectedBirthRulesArray));
        
        boolean[] expectedSurvivalRulesArray = {false, false, true, true, false, false, false, false, false};
        System.out.println("Odotetut eloonjäämisssäännöt:\t" + showArray(expectedSurvivalRulesArray));
        boolean[] survivalRulesArray = new boolean[rules.NEIGHBOR_COUNT];
        for(int i = 0; i < survivalRulesArray.length; i++) {
            survivalRulesArray[i] = rules.getSurvivalRule(i);
        }
        System.out.println("Saadut eloonjäämissäännöt:\t" + showArray(survivalRulesArray));
        testResult("Eloonjäämissääntöjen alustaminen", rulesEq(survivalRulesArray, expectedSurvivalRulesArray));
        
        System.out.println("Luodaan soluautomaatti.");
        game = new CA();
        
        int expectedCols, expectedRows;
        boolean[][] expectedCells, cells;
        
        expectedCols = 16;
        expectedRows = 10;
        System.out.println("Asetetaan soluautomaatin kooksi " + expectedCols + "x" + expectedRows + ".");
        game.setCells(Grid.getDeadGrid(expectedCols, expectedRows));
        expectedCells = Grid.getDeadGrid(expectedCols, expectedRows);
        System.out.println("Odotettu ruudukko:");
        printCells(expectedCells);
        cells = getCells(game);
        System.out.println("Saatu ruudukko:");
        printCells(cells);
        testResult("Ruudukon koon alustaminen", Grid.equal(cells, expectedCells));
        
        expectedCols = 1;
        expectedRows = 1;
        System.out.println("Asetetaan soluautomaatin kooksi " + expectedCols + "x" + expectedRows + ".");
        game.setCells(Grid.getDeadGrid(expectedCols, expectedRows));
        expectedCells = Grid.getDeadGrid(CAState.MIN_DIMENSION, CAState.MIN_DIMENSION);
        System.out.println("Odotettu ruudukko:");
        printCells(expectedCells);
        cells = getCells(game);
        System.out.println("Saatu ruudukko:");
        printCells(cells);
        testResult("Ruudukon koon alustaminen liian pienillä ulottuvuuksilla", Grid.equal(cells, expectedCells));
        
        System.out.println("Asetetaan soluautomaatille Life-säännöt.");
        game.setRules(rules);
                
        for(int i = 0; i < rules.NEIGHBOR_COUNT; i++) {
            testRules(game, i);
        }
                
        System.out.println("Asetetaan soluautomaatin kooksi 4x4.");
        game.setCells(Grid.getDeadGrid(4, 4));
        
        System.out.println("Asetetaan reunakäyttäytymiseksi ALIVE.");
        rules = game.getRules();
        rules.setEdgeBehavior(CARules.EdgeBehavior.ALIVE);
        game.setRules(rules);
        
        System.out.println("Alkutila:");
        printCells(getCells(game));
        
        System.out.println("Suoritetaan evoluutio.");
        game.evolve();
        
        expectedCells = readCells(4, 4,
            "0110" +
            "1001" +
            "1001" +
            "0110"
        );
        cells = getCells(game);
        System.out.println("Odotettu tila:");
        printCells(expectedCells);
        System.out.println("Saatu tila:");
        printCells(cells);
        testResult("Elävien reunojen toimivuus", Grid.equal(expectedCells, cells));
        
        System.out.println("Asetetaan soluautomaatin kooksi 4x4.");
        game.setCells(readCells(4, 4,
            "1001" +
            "0000" +
            "0000" +
            "1001"
        ));
        
        System.out.println("Asetetaan reunakäyttäytymiseksi WRAP.");
        rules = game.getRules();
        rules.setEdgeBehavior(CARules.EdgeBehavior.WRAP);
        game.setRules(rules);
        
        System.out.println("Alkutila:");
        printCells(getCells(game));
        
        System.out.println("Suoritetaan evoluutio.");
        game.evolve();
        
        expectedCells = readCells(4, 4,
            "1001" +
            "0000" +
            "0000" +
            "1001"
        );
        cells = getCells(game);
        System.out.println("Odotettu tila:");
        printCells(expectedCells);
        System.out.println("Saatu tila:");
        printCells(cells);
        testResult("Liitettyjen reunojen toimivuus", Grid.equal(expectedCells, cells));
        
        System.out.println("Asetetaan soluautomaatin kooksi 3x3 ja luodaan 2-vaiheinen oskillaattori.");
        game.setCells(readCells(3, 3,
            "000" +
            "111" +
            "000"
        ));
        
        System.out.println("Asetetaan reunakäyttäytymiseksi DEAD.");
        rules = game.getRules();
        rules.setEdgeBehavior(CARules.EdgeBehavior.DEAD);
        game.setRules(rules);
        
        System.out.println("Alkutila:");
        printCells(getCells(game));
        
        System.out.println("Suoritetaan evoluutio.");
        game.evolve();
        System.out.println("Uusi tila:");
        printCells(getCells(game));
        
        boolean rhythmFound;
        
        System.out.println("Tarkistetaan, onko rytmiä löytynyt.");
        rhythmFound = game.isPeriodic();
        System.out.println("Odotettu vastaus:\t\tfalse");
        System.out.println("Saatu vastaus:\t\t\t" + rhythmFound);
        testResult("Rytmin löytymättömyys ennenaikaisesti", rhythmFound == false);
        
        System.out.println("Suoritetaan toinen evoluutio.");
        game.evolve();
        System.out.println("Uusi tila:");
        printCells(getCells(game));
        
        System.out.println("Tarkistetaan, onko rytmiä löytynyt.");
        rhythmFound = game.isPeriodic();
        System.out.println("Odotettu vastaus:\t\ttrue");
        System.out.println("Saatu vastaus:\t\t\t" + rhythmFound);
        testResult("Rytmin löytyminen", rhythmFound == true);
        
        System.out.println("Tallennetaan nykyinen tila tilapäismuistiin.");
        game.storePattern();
        boolean[][] storedCells = getCells(game);
        
        System.out.println("Tyhjennetään soluautomaatti.");
        game.erase();
        
        expectedCells = Grid.getDeadGrid(3, 3);
        cells = getCells(game);
        System.out.println("Odotettu tila:");
        printCells(expectedCells);
        System.out.println("Saatu tila:");
        printCells(cells);
        testResult("Tyhjennyksen toimivuus", Grid.equal(expectedCells, cells));
        
        System.out.println("Palautetaan tilapäismuistiin säilötty tila.");
        game.recallPattern();
        expectedCells = storedCells;
        cells = getCells(game);
        System.out.println("Odotettu tila:");
        printCells(expectedCells);
        System.out.println("Saatu tila:");
        printCells(cells);
        testResult("Tilan tilapäistallentaminen ja -palauttaminen", Grid.equal(expectedCells, cells));
        
        System.out.println("" + passed + "/" + tests + " testiä onnistui.");
    }
    
    private static String showArray(Object[] array) {
        String result = "";
        for(int i = 0; i < array.length; i++) {
            if(!result.equals("")) {
                result += ", ";
            }
            result += "" + i + ": " + array[i];
        }
        return "{" + result + "}";
    }
    
    private static String showArray(boolean[] array) {
        String result = "";
        for(int i = 0; i < array.length; i++) {
            if(!result.equals("")) {
                result += ", ";
            }
            result += "" + i + ": " + array[i];
        }
        return "{" + result + "}";
    }
    
    private static void printCells(boolean[][] cells) {
        boolean[][] transposed = transposeCells(cells);
        for(boolean[] row : transposed) {
            System.out.print("\t\t\t\t");
            for(boolean cell : row) {
                System.out.print(cell ? "1" : "0");
            }
            System.out.println();
        }
    }
            
    private static boolean rulesEq(boolean[] r1, boolean[] r2) {
        if(r1 != null && r2 != null) {
            if(r1.length == r2.length) {
                for(int i = 0; i < r1.length; i++) {
                    if(r1[i] != r2[i]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else if(r1 == null && r2 == null) {
            return true;
        } else {
            return false;
        }
    }
    
    private static boolean[][] getCells(CA game) {
        int cols = game.getCols();
        int rows = game.getRows();
        boolean[][] cells = new boolean[cols][rows];
        
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                cells[col][row] = game.getCell(col, row);
            }
        }
        return cells;
    }
    
    private static boolean[][] transposeCells(boolean[][] cells) {
        int cols = Grid.getCols(cells);
        int rows = Grid.getRows(cells);
        boolean[][] transposed = new boolean[rows][cols];
        
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                transposed[row][col] = cells[col][row];
            }
        }
        return transposed;
    }
    
    private static void testResult(String name, boolean success) {
        tests++;
        System.out.println("Testi " + tests + ": \"" + name + "\":");
        if(success) {
            passed++;
            System.out.println("\33[32mONNISTUI\33[0m");
        } else {
            System.out.println("\33[31mEPÄONNISTUI\33[0m");
        }
        try {
            Thread.sleep(500);
        } catch(Exception e) {
            
        }
        System.out.println();
    }
    
    private static void testRules(CA game, int neighborCount) {
        System.out.println("Asetetaan soluautomaatin kooksi 6x3.");
        boolean[][] initialCells = Grid.getDeadGrid(6, 3);
        
        initialCells[4][1] = true;
        for(int n = 0; n < neighborCount; n++) {
            int i = n + (n >= 4 ? 1 : 0);
            int col = i % 3;
            int row = i / 3;
            initialCells[col][row] = true;
            initialCells[col + 3][row] = true;
        }
        game.setCells(initialCells);
        System.out.println("Alkutila:");
        printCells(getCells(game));
        
        System.out.println("Suoritetaan evoluutio.");
        game.evolve();
        
        System.out.println("Uusi tila:");
        printCells(getCells(game));
        System.out.println();
        
        boolean expectedState, state;
        
        expectedState = game.getRules().getBirthRule(neighborCount);
        state = game.getCell(1, 1);
        System.out.println("Solun (1, 1) odotettu tila:\t" + (expectedState ? "1" : "0"));
        System.out.println("Solun (1, 1) saatu tila:\t" + (state ? "1" : "0"));
        testResult("" + neighborCount + " naapurin syntymäsäännön toimivuus", expectedState == state);
        
        expectedState = game.getRules().getSurvivalRule(neighborCount);
        state = game.getCell(4, 1);
        System.out.println("Solun (4, 1) odotettu tila:\t" + (expectedState ? "1" : "0"));
        System.out.println("Solun (4, 1) saatu tila:\t" + (state ? "1" : "0"));
        testResult("" + neighborCount + " naapurin eloonjäämissäännön toimivuus", expectedState == state);
    }
    
    private static boolean[][] readCells(int cols, int rows, String str) {
        boolean[][] cells = new boolean[cols][rows];
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                int i = row*cols + col;
                char ch = str.charAt(i);
                cells[col][row] = (ch == '1' ? true : false);
            }
        }
        return cells;
    }
}