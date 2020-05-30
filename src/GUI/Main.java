package GUI;

import javax.swing.*;

public class Main {
    //todo include flowchart renders into git repository

    public static void main(String[] args) {
	    new Main();
    }

    private Main(){
        SwingUtilities.invokeLater(new MainGui());
    }
}
