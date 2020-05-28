import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.io.File;

public class MainGui implements Runnable {
    //COMPONENTS
    private JTextField directory;
    private JButton browseButton;
    private JPanel contentPanel;
    private JCheckBox recursiveCheckBox;

    //FILES
    private File chosenDirectory;

    //FRAME
    JFrame frame;

    MainGui(){
        chosenDirectory = new File(System.getProperty("user.home"));
        directory.setText(chosenDirectory.getAbsolutePath());
    }

    @Override
    public void run() {
        initFrame();
        addActionListeners();
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setSize(600,600);
        frame.setMinimumSize(new Dimension(300,300));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(contentPanel);
        frame.setVisible(true);
    }

    private void addActionListeners() {
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setCurrentDirectory(chosenDirectory);
            int returnVal = fileChooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
                setChosenDirectory(fileChooser.getSelectedFile());
        });
    }

    private void setChosenDirectory(File chosenDirectory) {
        this.chosenDirectory = chosenDirectory;
        directory.setText(chosenDirectory.getAbsolutePath());
    }
}
