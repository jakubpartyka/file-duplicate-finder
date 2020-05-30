package gui;

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


//todo
//move to one unified folder (keep one) and link others in text
//UNIX -> file shortcut
//list all in jtable, enable sorting, group by byte content
//todo delete if parent the same (checkbox)
