package gui;

import java.text.*;
import javax.swing.*;

/**
    * Tekstikenttä, joka hyväksyy ainoastaan kokonaislukuarvoja.
    */
public class IntField extends JFormattedTextField {
    public IntField() {
        super(getIntFormat());
        this.setHorizontalAlignment(CENTER);
    }
    
    /**
        * Luo kentän käyttämän numeroformaatin.
        *
        * @return Numeroformaatti.
        */
    private static NumberFormat getIntFormat() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        return format;
    }
        
    /**
        * Palauttaa kentän viimeisen validin kokonaislukuarvon.
        *
        * @return Kokonaisluku.
        */
    public int getIntValue() {
        Object val = this.getValue();
        if(val instanceof Integer) {
            return ((Integer) val).intValue();
        } else {
            return ((Long) val).intValue();
        }
    }
}