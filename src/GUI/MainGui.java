package gui;

//todo add scan time calculation
//todo last used dir should be saved
//todo total dup size set -> display GB
//todo idea -> display files with size
//todo increment files scanned counter when duplicates are detected
//todo optimize duplicates size estimation method
//todo ignore by size fields should accept integers only
//todo move scan recursively checkbox to settings

import file_management.FileScanner;
import file_management.InvalidDirectoryException;
import gui.manualSelectionUI.ManualSelectorUI;

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
    private Settings settings;
    private JFrame frame;
    private JButton applyButton;
    private JProgressBar linkCreatorProgressBar;
    private ManualSelectorUI manualSelectorUI;
    private JButton backToMainButton;
    private HardLinkCreatorUI creatorUI;
    private MergeUI mergeUI;
    private JProgressBar mergeProgress;


    //panels
    private JPanel settingsPanel;
    private JPanel symbolicLinkCreatorPanel;
    private JPanel manualPanel;
    private JPanel mergePanel;

    //FILES
    private File home;
    private List<File> chosenDirectories = new ArrayList<>();

    //SCANNER
    private FileScanner fileScanner;
    private boolean scanInProgress = false;

    //OPTIMIZER
    static int selectedOptimizer = 1;

    MainGui(){
        home = new File(System.getProperty("user.home"));
        chosenDirectories.add(home);
        directory.setText(home.getAbsolutePath());
    }

    @Override
    public void run() {
        initFrame();
        initRemoteViews(true);
        addActionListeners();
    }

    /**
     * creates and shows main gui frame
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
            fileChooser.setCurrentDirectory(new File(chosenDirectories.get(0).getAbsolutePath()));
            fileChooser.setMultiSelectionEnabled(true);
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
                setDirectories(fileChooser.getSelectedFiles());
        });

        scanButton.addActionListener(e -> {
            startScan();
        });

        //FOREIGN COMPONENTS
        cancelButton.addActionListener(e -> {
            fileScanner.setActive(false);
            if(status.getText().equals("Status: cancelled")) endScan();
        });

        settingsButton.addActionListener(e -> switchView(2));
    }

    private void startScan() {
        if(scanInProgress)
            return;
        resetStatsComponents();
        try {
            fileScanner = new FileScanner(chosenDirectories, recursiveCheckBox.isSelected());
            //todo add more ignoring filters to scanner
            fileScanner.execute();
            fileScanner.addPropertyChangeListener(evt -> {
                switch (evt.getPropertyName()){
                    case "progress"         : progressBar.setValue((Integer)evt.getNewValue()); break;
                    case "filesScanned"     : filesScanned.setText("Files scanned: " + evt.getNewValue().toString()); break;
                    case "status"           : status.setText("Status: " + evt.getNewValue().toString()); break;
                    case "duplicatesFound"  : duplicatesFound.setText("Duplicates found: " + evt.getNewValue().toString()); break;
                    case "nowChecking"      : nowChecking.setText("Now checking: " + evt.getNewValue().toString()); break;
                    case "totalSize"        : totalSize.setText("Duplicates total size: " + evt.getNewValue().toString()); break;
                    case "done"             :
                        //starts optimizer when scanning is finished
                        try {
                            //noinspection unchecked
                            List <List<File>> duplicates = (List<List<File>>) fileScanner.get();
                            startOptimizer(duplicates);
                        } catch (InterruptedException | ExecutionException e1) {
                            JOptionPane.showMessageDialog(null,"Failed to optimize files due to error\n" +
                                    e1.getMessage(),"Optimizer failed",JOptionPane.WARNING_MESSAGE);
                            endScan();
                        }
                        break;
                    default: break;
                }
            });
        }
        catch (InvalidDirectoryException exception){
            JOptionPane.showMessageDialog(null,exception.getMessage(),"Failed to start scan",JOptionPane.WARNING_MESSAGE);
        }

        //prepare UI components
        scanInProgress = true;
        cancelButton.setEnabled(true);
        scanButton.setEnabled(false);
        applyButton.setEnabled(false);
    }

    private void startOptimizer(List<List<File>> duplicates) {
        status.setText("Optimizing files");

        switch (selectedOptimizer){
            case 1:
                switchView(4);
                manualSelectorUI.initiate(duplicates);
                break;

            case 2:
                switchView(3);
                creatorUI.setDuplicates(duplicates);
                creatorUI.execute();
                creatorUI.addPropertyChangeListener(evt -> {
                    switch (evt.getPropertyName()){
                        case "prg" :
                            linkCreatorProgressBar.setValue((Integer)evt.getNewValue());
                            break;
                        case "end"      :
                            endScan();
                            break;
                        default : break;
                    }
                });
                break;

            case 3:
                switchView(5);
                mergeUI.setDuplicates(duplicates);
                mergeUI.setOutputDirectory(new File(settings.getMergeOutputDirectory()));
                mergeUI.execute();
                mergeUI.addPropertyChangeListener(evt -> {
                    switch (evt.getPropertyName()){
                        case "prg" :
                            mergeProgress.setValue((Integer)evt.getNewValue());
                            break;
                        case "end" :
                            endScan();
                            break;
                    }
                });
                break;
            default: break;
        }
    }

    /**
     * initializes remote panels objects
     * @param initSettings decides if settings should be re-initiated, should be true only on first initiation.
     */
    private void initRemoteViews(boolean initSettings) {
        //SETTINGS PANEL
        if(initSettings) {
            settings = new Settings();
            settingsPanel = settings.getSettingsPanel();
            applyButton = settings.getApplyButton();
            applyButton.addActionListener(e -> {
                //switch view
                switchView(1);
                //todo add setting save
            });
            settings.getCancelButton().addActionListener(e -> switchView(1));
        }

        //SYMBOLIC LINK CREATOR
        creatorUI = new HardLinkCreatorUI();
        symbolicLinkCreatorPanel = creatorUI.getPanel();
        linkCreatorProgressBar = creatorUI.getProgressBar();

        //MANUAL SELECTOR
        manualSelectorUI = new ManualSelectorUI();
        manualPanel = manualSelectorUI.getManualPanel();
        backToMainButton = manualSelectorUI.getGoBackToMainButton();

        backToMainButton.addActionListener(e -> {
            endScan();
        });

        //MERGE
        mergeUI = new MergeUI();
        mergePanel = mergeUI.getMergePanel();
        mergeProgress = mergeUI.getProgressBar1();
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
            directory.setText("multiple directories selected");
        else
            directory.setText(chosenDirectories[0].getAbsolutePath());
    }

    /**
     * Enables starting new scan and updating settings after it was disabled on scan start.
     * Should be executed when scan is completed or cancelled.
     */
    private void endScan(){
        switchView(1);
        //set output
        duplicateOutput.setText(fileScanner.getOutput());

        //clear now checking label
        nowChecking.setText("Now checking: none");
        status.setText("Status: COMPLETED");

        //set scan in progress & disable cancel

        scanInProgress = false;
        cancelButton.setEnabled(false);
        scanButton.setEnabled(true);

        initRemoteViews(false);

        applyButton.setEnabled(true);
    }

    /**
     * View changes should only be performed between main view (1) and other views due to
     * way of adding/removing JPanels from main JFrame (better method will be implemented in the future)
     * @param view integer referring to panel that view should be switched to
     */
    private void switchView(int view){
        switch (view){
            case 1:
                frame.remove(manualPanel);
                frame.remove(settingsPanel);
                frame.remove(mergePanel);
                frame.remove(symbolicLinkCreatorPanel);
                frame.add(contentPanel);
                break;
            case 2:
                frame.remove(contentPanel);
                frame.add(settingsPanel);
                break;
            case 3:
                frame.remove(contentPanel);
                frame.add(symbolicLinkCreatorPanel);
                break;
            case 4:
                frame.remove(contentPanel);
                frame.add(manualPanel);
                break;
            case 5:
                frame.remove(contentPanel);
                frame.add(mergePanel);
            default : break;
        }
        frame.repaint();
        frame.revalidate();
    }
}
