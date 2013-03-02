package utils;

import java.io.*;

/**
    * FileReaderin laajennus, joka mahdollistaa koko tiedoston sisällön lukemisen kerralla.
    */
public class FileLoader extends FileReader {
    /**
        * Avaa annetun tiedoston luettavaksi.
        *
        * @param f Tiedosto-olio.
        *
        * @exception FileNotFoundException Jos tiedoston avaaminen epäonnistui.
        */
    public FileLoader(File f) throws FileNotFoundException {
        super(f);
    }
    
    /**
        * Lukee koko tiedoston sisällön merkkijonoon.
        *
        * @return Tiedoston sisältö.
        *
        * @exception IOException Jos sattui jotain, joka estää lukemisen.
        */
    public String readAll() throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean eof = false;
        do {
            int c = this.read();
            if(c != -1) {
                sb.append((char) c);
            } else {
                eof = true;
            }
        } while(!eof);
        
        return sb.toString();
    }
}