package GUI;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class Settings {
    private JPanel settingsPanel;
    private JButton saveButton;
    private JTextArea acceptPatterns;
    private JTextArea ignorePatterns;
    private JCheckBox acceptFilesThatMatchCheckBox;
    private JCheckBox ignoreFilesThatMatchCheckBox;
    private JTextField smallerTextField;
    private JCheckBox ignoreFilesSmallerThanCheckBox;
    private JCheckBox ignoreFilesBiggerThanCheckbox;
    private JTextField biggerTextField;

    public Settings() {
        initComponents();
        addActionListeners();
    }

    private void initComponents() {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost)
    }

    /**
     * add action listeners to UI components
     */
    private void addActionListeners() {
        acceptFilesThatMatchCheckBox.addActionListener(e -> acceptPatterns.setEnabled(acceptFilesThatMatchCheckBox.isSelected()));
        ignoreFilesThatMatchCheckBox.addActionListener(e -> ignorePatterns.setEnabled(ignoreFilesThatMatchCheckBox.isSelected()));
        ignoreFilesSmallerThanCheckBox.addActionListener(e -> smallerTextField.setEnabled(ignoreFilesSmallerThanCheckBox.isSelected()));
        ignoreFilesBiggerThanCheckbox.addActionListener(e -> biggerTextField.setEnabled(ignoreFilesBiggerThanCheckbox.isSelected()));
    }

    /**
     * @return returns settings content panel
     */
    JPanel getSettingsPanel() {
        return settingsPanel;
    }

    /**
     * @return returns save button object
     */
    JButton getSaveButton() {
        return saveButton;
    }
}
