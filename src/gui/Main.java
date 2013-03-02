package gui;

import javax.swing.*;

/**
    * Pääohjelma.
    */
public class Main {
    /**
        * Alustaa ja tuo esiin pääikkunan. 
        */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow window = new MainWindow();
                window.pack();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setVisible(true);
            }
        });
    }
}