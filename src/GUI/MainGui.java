package GUI;

//todo add scan time calculation
//todo stats (number of scanned files)

import FileManagement.FileScanner;
import FileManagement.InvalidDirectoryException;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainGui implements Runnable {
    //COMPONENTS
    private JTextField directory;
    private JButton browseButton;
    private JPanel contentPanel;
    private JCheckBox recursiveCheckBox;
    private JButton scanButton;
    private JTextArea duplicateOutput;
    private JProgressBar progressBar;
    private JLabel filesScanned;
    private JLabel currentTask;
    private JButton cancelButton;
    private JLabel duplicatesFound;

    //FRAME
    private JFrame frame;

    //FILES
    private File home;
    private List<File> chosenDirectories = new ArrayList<>();

    //SCANNER
    private FileScanner fileScanner;

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
        frame = new JFrame("Duplicate Finder");
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
                directories(fileChooser.getSelectedFiles());
        });

        scanButton.addActionListener(e -> {
            try {
                fileScanner = new FileScanner(chosenDirectories, recursiveCheckBox.isSelected());
                fileScanner.execute();
                fileScanner.addPropertyChangeListener(evt -> {
                    switch (evt.getPropertyName()){
                        case "progress"         : progressBar.setValue((Integer)evt.getNewValue()); break;
                        case "filesScanned"     : filesScanned.setText("Files scanned: " + evt.getNewValue().toString()); break;
                        case "status"           : currentTask.setText("Status: " + evt.getNewValue().toString()); break;
                        case "duplicatesFound"  : duplicatesFound.setText("Duplicates found: " + evt.getNewValue().toString()); break;
                        default: break;
                    }
                });
            }
            catch (InvalidDirectoryException exception){
                JOptionPane.showMessageDialog(null,exception.getMessage(),"Failed to start scan",JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> fileScanner.setActive(false));
    }

    private void directories(File[] chosenDirectories) {
        this.chosenDirectories.clear();
        this.chosenDirectories.addAll(Arrays.asList(chosenDirectories));
        if(chosenDirectories.length > 1)
            directory.setText("multiple directories selected");
        else
            directory.setText(chosenDirectories[0].getAbsolutePath());
    }
}
