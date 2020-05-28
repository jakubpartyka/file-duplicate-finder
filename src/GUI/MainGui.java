package GUI;

//todo add scan time calculation
//todo stats (number of scanned files)

import FileManagement.FileScanner;
import FileManagement.InvalidDirectoryException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainGui implements Runnable {
    //COMPONENTS
    private JTextField directory;
    private JButton browseButton;
    private JPanel contentPanel;
    private JCheckBox recursiveCheckBox;
    private JButton scanButton;
    private JTextArea duplicateOutput;

    //FILES
    private File home;
    private List<File> chosenDirectories = new ArrayList<>();

    MainGui(){
        home = new File(System.getProperty("user.home"));
        chosenDirectories.add(home);
        directory.setText(home.getAbsolutePath());
    }

    @Override
    public void run() {
        initFrame();
        addActionListeners();
    }

    private void initFrame() {
        //FRAME
        JFrame frame = new JFrame();
        frame.setSize(600,600);
        frame.setMinimumSize(new Dimension(300,100));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(contentPanel);
        frame.setVisible(true);
    }

    private void addActionListeners() {
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setCurrentDirectory(home);
            fileChooser.setMultiSelectionEnabled(true);
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
                setChosenDirectory(fileChooser.getSelectedFiles());
        });

        scanButton.addActionListener(e -> {
            try {
                FileScanner fileScanner = new FileScanner(chosenDirectories, recursiveCheckBox.isSelected());
                fileScanner.scan();     //todo new Thread?
                duplicateOutput.setText(fileScanner.getOutput());
            }
            catch (InvalidDirectoryException exception){
                JOptionPane.showMessageDialog(null,exception.getMessage(),"Failed to start scan",JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void setChosenDirectory(File[] chosenDirectories) {
        if(chosenDirectories.length > 1)
            directory.setText("multiple directories selected");
        else
            directory.setText(chosenDirectories[0].getAbsolutePath());
    }
}
