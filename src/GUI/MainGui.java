package GUI;

//todo add scan time calculation
//todo progress bar more discrete
//todo last used dir should be saved
//todo total dup size set -> display GB
//todo idea -> display files with size
//todo increment files scanned counter when duplicates are detected
//todo optimize duplicates size estimation method
//todo ignore by size fields should accept integers only

/*todo settings
* what to do in case of duplicate
* save output to file + save current output
* */

import FileManagement.FileScanner;
import FileManagement.InvalidDirectoryException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("FieldCanBeLocal")
public class MainGui implements Runnable {
    //COMPONENTS
    private JPanel contentPanel;
    private JTextField directory;
    private JButton browseButton;
    private JCheckBox recursiveCheckBox;
    private JButton scanButton;
    private JTextArea duplicateOutput;
    private JProgressBar progressBar;
    private JLabel filesScanned;
    private JLabel status;
    private JButton cancelButton;
    private JLabel duplicatesFound;
    private JLabel nowChecking;
    private JLabel totalSize;
    private JButton settingsButton;

    //FRAME AND REMOTE VIEWS
    private JFrame frame;
    private JPanel settingsPanel;
    private JButton applyButton;

    //FILES
    private File home;
    private List<File> chosenDirectories = new ArrayList<>();

    //SCANNER
    private FileScanner fileScanner;
    private boolean scanInProgress = false;

    MainGui(){
        home = new File(System.getProperty("user.home"));
        chosenDirectories.add(home);
        directory.setText(home.getAbsolutePath());
    }

    @Override
    public void run() {
        initFrame();
        initRemoteViews();
        addActionListeners();
    }

    /**
     * creates and shows main GUI frame
     */
    private void initFrame() {
        frame = new JFrame("Duplicate Finder");
        frame.setSize(600,600);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(300,100));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(contentPanel);
        frame.setVisible(true);
    }

    /**
     * creates action listeners for main view components
     */
    private void addActionListeners() {
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setCurrentDirectory(home);
            fileChooser.setMultiSelectionEnabled(true);
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
                setDirectories(fileChooser.getSelectedFiles());
        });

        scanButton.addActionListener(e -> {
            if(scanInProgress)
                return;
            resetStatsComponents();
            try {
                fileScanner = new FileScanner(chosenDirectories, recursiveCheckBox.isSelected());
                fileScanner.execute();
                fileScanner.addPropertyChangeListener(evt -> {
                    switch (evt.getPropertyName()){
                        case "progress"         : progressBar.setValue((Integer)evt.getNewValue()); break;
                        case "filesScanned"     : filesScanned.setText("Files scanned: " + evt.getNewValue().toString()); break;
                        case "status"           : status.setText("Status: " + evt.getNewValue().toString()); break;
                        case "duplicatesFound"  : duplicatesFound.setText("Duplicates found: " + evt.getNewValue().toString()); break;
                        case "nowChecking"      : nowChecking.setText("Now checking: " + evt.getNewValue().toString()); break;
                        case "totalSize"        : totalSize.setText("Duplicates total size: " + evt.getNewValue().toString() + " MB"); break;
                        case "done"             : end(); break;
                        default: break;
                    }
                });
                scanStart();
            }
            catch (InvalidDirectoryException exception){
                JOptionPane.showMessageDialog(null,exception.getMessage(),"Failed to start scan",JOptionPane.WARNING_MESSAGE);
            }
        });

        //FOREIGN COMPONENTS
        cancelButton.addActionListener(e -> {
            fileScanner.setActive(false);
            if(status.getText().equals("Status: cancelled")) end();
        });

        settingsButton.addActionListener(e -> {
            switchView(2);
        });
    }

    /**
     * initializes remote panels objects
     */
    private void initRemoteViews() {
        //SETTINGS PANEL
        Settings settings = new Settings();
        settingsPanel = settings.getSettingsPanel();
        applyButton = settings.getApplyButton();
        applyButton.addActionListener(e -> {
            switchView(1);
            //todo settings save
        });
        settings.getCancelButton().addActionListener(e -> {
            switchView(1);
        });
    }

    /**
     * contains code to execute at end of scan
     */
    private void end() {
        //set output
        try {
            duplicateOutput.setText(fileScanner.get().toString());
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
        }

        //clear now checking label
        nowChecking.setText("Now checking: none");

        //set scan in progress & disable cancel
        scanStop();
    }

    /**
     * sets UI statistics components to default values
     */
    private void resetStatsComponents() {
        status.setText("Status: not started");
        nowChecking.setText("Now checking: none");
        filesScanned.setText("Files scanned: 0");
        duplicatesFound.setText("Duplicates found: 0");
        totalSize.setText("Duplicates total size: 0.00 MB");
        duplicateOutput.setText("duplicate files will appear here after scan is completed");
        progressBar.setValue(0);
    }

    /**
     * sets target directories basing on FileChooser output
     * @param chosenDirectories directories passed from FileChooser
     */
    private void setDirectories(File[] chosenDirectories) {
        this.chosenDirectories.clear();
        this.chosenDirectories.addAll(Arrays.asList(chosenDirectories));
        if(chosenDirectories.length > 1)
            directory.setText("multiple setDirectories selected");
        else
            directory.setText(chosenDirectories[0].getAbsolutePath());
    }

    /**
     * set components' state when file scan starts. Opposite of scanStop();
     */
    private void scanStart(){
        scanInProgress = true;
        cancelButton.setEnabled(true);
        scanButton.setEnabled(false);

        applyButton.setEnabled(false);
    }

    /**
     * set components' state when file scan ends. Opposite of scanStart();
     */
    private void scanStop(){
        scanInProgress = false;
        cancelButton.setEnabled(false);
        scanButton.setEnabled(true);

        applyButton.setEnabled(true);
    }

    private void switchView(int view){
        switch (view){
            case 1:
                frame.remove(settingsPanel);
                frame.add(contentPanel);
                break;
            case 2:
                frame.remove(contentPanel);
                frame.add(settingsPanel);
                break;
            case 3:
                break;
            default : break;
        }
        frame.repaint();
        frame.revalidate();
    }
}
