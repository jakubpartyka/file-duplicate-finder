package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
	    new Main();
    }

    private Main(){
        SwingUtilities.invokeLater(new MainGui());
    }
}


//todo cleanup (hide deleted files)
